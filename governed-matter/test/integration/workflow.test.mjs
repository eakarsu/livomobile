import assert from "node:assert/strict";
import { randomUUID } from "node:crypto";
import test from "node:test";
import { createApp } from "../../src/app.mjs";
import { openDatabase } from "../../src/database.mjs";
import { migrate } from "../../src/migrations.mjs";
import { generateApiToken, tokenDigest } from "../../src/security.mjs";

function fixture() {
  const database = openDatabase(":memory:");
  migrate(database, new Date("2026-07-20T00:00:00Z"));
  const organizationId = randomUUID();
  database.prepare("INSERT INTO organizations (id, slug, name, created_at) VALUES (?, 'legal-team', 'Legal Team', ?)")
    .run(organizationId, new Date("2026-07-20T00:00:00Z").toISOString());
  const principals = {};
  for (const [name, role] of Object.entries({ author: "AUTHOR", reviewer: "LEGAL_REVIEWER", records: "RECORDS_MANAGER", auditor: "AUDITOR" })) {
    const token = generateApiToken();
    const id = randomUUID();
    database.prepare(`INSERT INTO api_tokens
      (id, organization_id, label, role, token_digest, expires_at, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)`)
      .run(id, organizationId, name, role, tokenDigest(token), "2027-07-20T00:00:00.000Z", "2026-07-20T00:00:00.000Z");
    principals[name] = { id, token, role };
  }
  const outsiderOrganizationId = randomUUID();
  database.prepare("INSERT INTO organizations (id, slug, name, created_at) VALUES (?, 'outside-team', 'Outside Team', ?)")
    .run(outsiderOrganizationId, "2026-07-20T00:00:00.000Z");
  const outsiderToken = generateApiToken();
  const outsiderId = randomUUID();
  database.prepare(`INSERT INTO api_tokens
    (id, organization_id, label, role, token_digest, expires_at, created_at) VALUES (?, ?, 'outsider', 'AUTHOR', ?, ?, ?)`)
    .run(outsiderId, outsiderOrganizationId, tokenDigest(outsiderToken), "2027-07-20T00:00:00.000Z", "2026-07-20T00:00:00.000Z");
  principals.outsider = { id: outsiderId, token: outsiderToken, role: "AUTHOR" };
  const providerCalls = [];
  let failNextSignature = true;
  const fetchImplementation = async (_url, options) => {
    const payload = JSON.parse(options.body);
    providerCalls.push(payload);
    if (payload.action === "SIGN" && failNextSignature) {
      failNextSignature = false;
      return new Response("signer declined", { status: 422 });
    }
    if (payload.action === "FILE") return Response.json({ receiptId: "file-receipt-1",
      artifactUri: "https://records.example/filed/document-1", artifactSha256: "f".repeat(64) });
    return Response.json({ receiptId: `${payload.action.toLowerCase()}-receipt-1` });
  };
  const config = {
    allowedOrigins: ["https://legal.example"], idempotencySecret: "i".repeat(32),
    documentProviderUrl: "https://provider.example/operations", documentProviderSecret: "p".repeat(32),
    documentProviderTimeoutMs: 500, documentProviderMaxAttempts: 1,
  };
  const app = createApp({ database, config, fetchImplementation, logger: { info() {}, error() {} } });
  const server = app.listen(0);
  const baseUrl = `http://127.0.0.1:${server.address().port}`;
  let sequence = 0;
  async function request(actor, path, { method = "GET", body, key, origin } = {}) {
    sequence += 1;
    const headers = { Authorization: `Bearer ${principals[actor].token}` };
    if (body !== undefined) headers["Content-Type"] = "application/json";
    if (method === "POST") headers["Idempotency-Key"] = key ?? `workflow-operation-${String(sequence).padStart(4, "0")}`;
    if (origin) headers.Origin = origin;
    const response = await fetch(`${baseUrl}${path}`, { method, headers, body: body === undefined ? undefined : JSON.stringify(body) });
    return { response, body: await response.json() };
  }
  return { database, principals, providerCalls, server, request };
}

test("redacted document completes independent review, signer retry, filing, hold, retention, and export", async (t) => {
  const f = fixture();
  t.after(() => { f.server.close(); f.database.close(); });
  let result = await f.request("author", "/v1/matters", { method: "POST", key: "create-matter-0001",
    body: { matterKey: "nda-2026", title: "Mutual NDA", jurisdiction: "US-NY" } });
  assert.equal(result.response.status, 201);
  const matterId = result.body.matter.id;
  result = await f.request("author", "/v1/matters", { method: "POST", key: "create-matter-0001",
    body: { matterKey: "nda-2026", title: "Mutual NDA", jurisdiction: "US-NY" } });
  assert.equal(result.response.headers.get("Idempotent-Replay"), "true");
  let matterVersion = 1;
  for (const [actor, privilege] of [["reviewer", "REVIEWER"], ["records", "RECORDS"], ["auditor", "READ_ONLY"]]) {
    result = await f.request("author", `/v1/matters/${matterId}/access`, { method: "POST",
      body: { tokenId: f.principals[actor].id, privilege, expectedVersion: matterVersion } });
    assert.equal(result.response.status, 200);
    matterVersion = result.body.matterVersion;
  }
  result = await f.request("records", "/v1/templates", { method: "POST", body: {
    templateKey: "mutual-nda-ca", title: "Wrong jurisdiction", jurisdiction: "US-CA",
    sourceUri: "https://authority.example/templates/nda-ca.pdf", contentSha256: "a".repeat(64),
    effectiveFrom: "2026-01-01", effectiveUntil: null,
  }});
  const wrongTemplateId = result.body.template.id;
  result = await f.request("records", "/v1/templates", { method: "POST", body: {
    templateKey: "mutual-nda-ny", title: "New York Mutual NDA", jurisdiction: "US-NY",
    sourceUri: "https://authority.example/templates/nda-ny.pdf", contentSha256: "b".repeat(64),
    effectiveFrom: "2026-01-01", effectiveUntil: null,
  }});
  assert.equal(result.response.status, 201);
  const templateId = result.body.template.id;
  result = await f.request("records", "/v1/templates", { method: "POST", body: {
    templateKey: "expired-nda-ny", title: "Expired New York NDA", jurisdiction: "US-NY",
    sourceUri: "https://authority.example/templates/expired-nda-ny.pdf", contentSha256: "9".repeat(64),
    effectiveFrom: "2025-01-01", effectiveUntil: "2025-12-31",
  }});
  const expiredTemplateId = result.body.template.id;
  const content = { title: "Acme Mutual NDA", sourceUri: "https://objects.example/raw/nda-v1.pdf",
    sourceSha256: "c".repeat(64), redactedUri: "https://objects.example/redacted/nda-v1.pdf",
    redactedSha256: "d".repeat(64), redactionReason: "Personal identifiers removed from reviewer copy" };
  result = await f.request("author", `/v1/matters/${matterId}/documents`, { method: "POST", body: { ...content, templateId: wrongTemplateId } });
  assert.equal(result.response.status, 409);
  assert.equal(result.body.error.code, "JURISDICTION_MISMATCH");
  result = await f.request("author", `/v1/matters/${matterId}/documents`, { method: "POST", body: { ...content, templateId: expiredTemplateId } });
  assert.equal(result.response.status, 409);
  assert.equal(result.body.error.code, "TEMPLATE_NOT_EFFECTIVE");
  result = await f.request("author", `/v1/matters/${matterId}/documents`, { method: "POST", body: { ...content, templateId } });
  assert.equal(result.response.status, 201);
  const documentId = result.body.document.id;
  result = await f.request("author", `/v1/documents/${documentId}/ocr`, { method: "POST", body: { expectedVersion: 1 } });
  assert.equal(result.response.status, 200);
  assert.equal(result.body.ocrState, "VERIFIED");
  let documentVersion = result.body.version;
  result = await f.request("author", `/v1/documents/${documentId}/submit`, { method: "POST", body: { expectedVersion: 999 } });
  assert.equal(result.response.status, 409);
  assert.equal(result.body.error.code, "VERSION_CONFLICT");
  result = await f.request("author", `/v1/documents/${documentId}/submit`, { method: "POST", body: { expectedVersion: documentVersion } });
  assert.equal(result.response.status, 200);
  documentVersion = result.body.document.version;
  result = await f.request("reviewer", `/v1/documents/${documentId}/review`, { method: "POST",
    body: { expectedVersion: documentVersion, decision: "APPROVE", note: "Jurisdiction, effective date, and redactions reviewed by counsel" } });
  assert.equal(result.response.status, 200);
  documentVersion = result.body.document.version;
  result = await f.request("author", `/v1/documents/${documentId}/sign`, { method: "POST",
    body: { expectedVersion: documentVersion, signerEmail: "signer@example.com" } });
  assert.equal(result.response.status, 502);
  assert.equal(result.body.state, "SIGNATURE_FAILED");
  documentVersion = result.body.version;
  result = await f.request("author", `/v1/documents/${documentId}/sign`, { method: "POST",
    body: { expectedVersion: documentVersion, signerEmail: "signer@example.com" } });
  assert.equal(result.response.status, 200);
  assert.equal(result.body.state, "SIGNED");
  documentVersion = result.body.version;
  const signPayload = f.providerCalls.filter((call) => call.action === "SIGN").at(-1);
  assert.equal(signPayload.sourceUri, content.redactedUri);
  assert.equal(signPayload.sourceSha256, content.redactedSha256);
  const retentionUntil = new Date().toISOString().slice(0, 10);
  result = await f.request("records", `/v1/documents/${documentId}/file`, { method: "POST",
    body: { expectedVersion: documentVersion, retentionUntil } });
  assert.equal(result.response.status, 200);
  assert.equal(result.body.state, "FILED");
  documentVersion = result.body.version;
  result = await f.request("records", `/v1/documents/${documentId}/hold`, { method: "POST",
    body: { active: true, expectedVersion: documentVersion, reason: "Pending litigation preservation notice" } });
  assert.equal(result.response.status, 200);
  documentVersion = result.body.version;
  result = await f.request("records", `/v1/documents/${documentId}/dispose`, { method: "POST", body: { expectedVersion: documentVersion } });
  assert.equal(result.response.status, 409);
  assert.equal(result.body.error.code, "LEGAL_HOLD_ACTIVE");
  result = await f.request("records", `/v1/documents/${documentId}/hold`, { method: "POST",
    body: { active: false, expectedVersion: documentVersion } });
  documentVersion = result.body.version;
  result = await f.request("records", `/v1/documents/${documentId}/dispose`, { method: "POST", body: { expectedVersion: documentVersion } });
  assert.equal(result.response.status, 200);
  assert.equal(result.body.state, "DISPOSED");
  result = await f.request("auditor", `/v1/matters/${matterId}/export`, { method: "POST", body: {} });
  assert.equal(result.response.status, 200);
  assert.equal(result.body.manifest.versions[0].redactedSha256, content.redactedSha256);
  assert.equal(result.body.manifest.documents[0].repositorySha256, "f".repeat(64));
  assert.match(result.body.manifestSha256, /^[a-f0-9]{64}$/);
  result = await f.request("auditor", "/v1/audit/verify");
  assert.equal(result.response.status, 200);
  assert.equal(result.body.ok, true);
  assert.throws(() => f.database.prepare("UPDATE document_versions SET source_sha256 = ? WHERE document_id = ?")
    .run("e".repeat(64), documentId), /immutable/);
  assert.throws(() => f.database.prepare("UPDATE authoritative_templates SET title = 'Changed' WHERE id = ?")
    .run(templateId), /append-only/);
});

test("matter access revocation, tenant isolation, and immutable evidence fail closed", async (t) => {
  const f = fixture();
  t.after(() => { f.server.close(); f.database.close(); });
  let result = await f.request("author", "/v1/matters", { method: "POST",
    body: { matterKey: "access-test", title: "Access Test", jurisdiction: "US-NY" } });
  const matterId = result.body.matter.id;
  result = await f.request("author", `/v1/matters/${matterId}/access`, { method: "POST",
    body: { tokenId: f.principals.reviewer.id, privilege: "REVIEWER", expectedVersion: 1 } });
  assert.equal(result.response.status, 200);
  result = await f.request("reviewer", `/v1/matters/${matterId}`);
  assert.equal(result.response.status, 200);
  result = await f.request("author", `/v1/matters/${matterId}/access/${f.principals.reviewer.id}/revoke`, { method: "POST",
    body: { expectedVersion: 2 } });
  assert.equal(result.response.status, 200);
  result = await f.request("reviewer", `/v1/matters/${matterId}`);
  assert.equal(result.response.status, 403);
  result = await f.request("outsider", `/v1/matters/${matterId}`);
  assert.equal(result.response.status, 404);
  result = await f.request("author", `/v1/matters/${matterId}`, { origin: "https://evil.example" });
  assert.equal(result.response.status, 403);
  f.database.prepare("UPDATE api_tokens SET active = 0 WHERE id = ?").run(f.principals.reviewer.id);
  result = await f.request("reviewer", `/v1/matters/${matterId}`);
  assert.equal(result.response.status, 401);
  assert.throws(() => f.database.prepare("UPDATE audit_events SET payload_json = '{}' WHERE matter_id = ?").run(matterId), /append-only/);
});
