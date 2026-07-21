import { createHash, randomUUID } from "node:crypto";
import { z } from "zod";
import { appendAudit, canonicalJson, verifyAudit } from "./audit.mjs";
import { transaction } from "./database.mjs";
import { HttpError } from "./errors.mjs";
import { prepareOperation, recordOperation } from "./idempotency.mjs";
import { invokeDocumentProvider } from "./provider.mjs";
import { keyedHash } from "./security.mjs";

const uuid = z.string().uuid();
const sha256 = z.string().regex(/^[a-f0-9]{64}$/);
const httpsUri = z.string().url().max(2048).refine((value) => new URL(value).protocol === "https:", "must use HTTPS");
const dateOnly = z.string().regex(/^\d{4}-\d{2}-\d{2}$/).refine((value) => {
  const parsed = new Date(`${value}T00:00:00Z`);
  return !Number.isNaN(parsed.valueOf()) && parsed.toISOString().slice(0, 10) === value;
}, "invalid date");
const expectedVersion = z.number().int().positive();
const jurisdiction = z.string().regex(/^[A-Z]{2}(?:-[A-Z0-9]{1,8})?$/).max(24);

const matterSchema = z.object({
  matterKey: z.string().regex(/^[a-z0-9][a-z0-9-]{1,79}$/), title: z.string().trim().min(2).max(200), jurisdiction,
}).strict();
const accessSchema = z.object({ tokenId: uuid, privilege: z.enum(["CONTRIBUTOR", "REVIEWER", "RECORDS", "READ_ONLY"]), expectedVersion }).strict();
const revokeSchema = z.object({ expectedVersion }).strict();
const templateSchema = z.object({
  templateKey: z.string().regex(/^[a-z0-9][a-z0-9-]{1,79}$/), title: z.string().trim().min(2).max(200), jurisdiction,
  sourceUri: httpsUri, contentSha256: sha256, effectiveFrom: dateOnly, effectiveUntil: dateOnly.nullable().optional(),
}).strict().refine((value) => !value.effectiveUntil || value.effectiveUntil >= value.effectiveFrom, { message: "effectiveUntil must not precede effectiveFrom" });
const contentSchema = z.object({
  sourceUri: httpsUri, sourceSha256: sha256, redactedUri: httpsUri.optional(), redactedSha256: sha256.optional(),
  redactionReason: z.string().trim().min(2).max(500).optional(),
}).strict().refine((value) => [value.redactedUri, value.redactedSha256, value.redactionReason].filter(Boolean).length % 3 === 0,
  { message: "redactedUri, redactedSha256, and redactionReason must be supplied together" });
const documentSchema = contentSchema.extend({ templateId: uuid, title: z.string().trim().min(2).max(200) }).strict();
const versionSchema = contentSchema.extend({ expectedVersion }).strict();
const transitionSchema = z.object({ expectedVersion }).strict();
const reviewSchema = z.object({ expectedVersion, decision: z.enum(["APPROVE", "REJECT"]), note: z.string().trim().min(2).max(2000) }).strict();
const signSchema = z.object({ expectedVersion, signerEmail: z.string().email().max(254) }).strict();
const fileSchema = z.object({ expectedVersion, retentionUntil: dateOnly }).strict();
const holdSchema = z.discriminatedUnion("active", [
  z.object({ active: z.literal(true), expectedVersion, reason: z.string().trim().min(2).max(500) }).strict(),
  z.object({ active: z.literal(false), expectedVersion }).strict(),
]);

function parse(schema, value) {
  const result = schema.safeParse(value);
  if (!result.success) throw new HttpError(400, "VALIDATION_FAILED", "The request body is invalid", result.error.flatten());
  return result.data;
}
const iso = (now = new Date()) => now.toISOString();
const today = (now = new Date()) => now.toISOString().slice(0, 10);

function matter(database, principal, matterId) {
  const row = database.prepare("SELECT * FROM matters WHERE id = ? AND organization_id = ?").get(matterId, principal.organizationId);
  if (!row) throw new HttpError(404, "MATTER_NOT_FOUND", "Matter not found");
  return row;
}
function document(database, principal, documentId) {
  const row = database.prepare("SELECT * FROM documents WHERE id = ? AND organization_id = ?").get(documentId, principal.organizationId);
  if (!row) throw new HttpError(404, "DOCUMENT_NOT_FOUND", "Document not found");
  return row;
}
function requireAccess(database, principal, matterId, allowed) {
  const row = database.prepare(`SELECT privilege FROM matter_access
    WHERE matter_id = ? AND organization_id = ? AND token_id = ? AND active = 1`)
    .get(matterId, principal.organizationId, principal.tokenId);
  if (!row || !allowed.includes(row.privilege)) throw new HttpError(403, "MATTER_ACCESS_DENIED", "Active matter-scoped access is required");
  return row.privilege;
}
function requireVersion(row, value) {
  if (row.version !== value) throw new HttpError(409, "VERSION_CONFLICT", "The record changed; reload and retry", { currentVersion: row.version });
}
function replay(context) {
  return context.replay ? { ...context.replay, replayed: true } : null;
}
function syncOperation({ database, config, principal, key, request, status = 200, operation }) {
  const context = prepareOperation(database, config, principal, key, request);
  if (context.replay) return replay(context);
  return transaction(database, () => {
    const body = operation(context);
    recordOperation(database, principal, context, status, body);
    return { status, body, replayed: false };
  });
}
function documentBody(row) {
  return {
    id: row.id, matterId: row.matter_id, templateId: row.template_id, title: row.title,
    state: row.state, version: row.version, currentVersion: row.current_version,
    reviewerId: row.reviewer_id, signatureReceipt: row.signature_receipt,
    filingReceipt: row.filing_receipt, repositoryUri: row.repository_uri,
    repositorySha256: row.repository_sha256,
    retentionUntil: row.retention_until, legalHold: Boolean(row.legal_hold), disposedAt: row.disposed_at,
  };
}

export function createMatter({ database, config, principal, key, body, now = new Date() }) {
  const input = parse(matterSchema, body);
  return syncOperation({ database, config, principal, key, request: { action: "createMatter", input }, status: 201, operation: () => {
    const id = randomUUID();
    database.prepare(`INSERT INTO matters
      (id, organization_id, matter_key, title, jurisdiction, opened_by, created_at, updated_at)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?)`)
      .run(id, principal.organizationId, input.matterKey, input.title, input.jurisdiction, principal.tokenId, iso(now), iso(now));
    database.prepare(`INSERT INTO matter_access
      (id, organization_id, matter_id, token_id, privilege, granted_by, granted_at)
      VALUES (?, ?, ?, ?, 'CONTRIBUTOR', ?, ?)`)
      .run(randomUUID(), principal.organizationId, id, principal.tokenId, principal.tokenId, iso(now));
    appendAudit(database, { organizationId: principal.organizationId, matterId: id, actorTokenId: principal.tokenId,
      eventType: "MATTER_CREATED", toState: "OPEN", payload: { matterKey: input.matterKey, jurisdiction: input.jurisdiction }, createdAt: now });
    return { matter: { id, ...input, state: "OPEN", version: 1 } };
  }});
}

export function grantAccess({ database, config, principal, key, matterId, body, now = new Date() }) {
  const input = parse(accessSchema, body);
  return syncOperation({ database, config, principal, key, request: { action: "grantAccess", matterId, input }, operation: () => {
    const current = matter(database, principal, matterId);
    requireAccess(database, principal, matterId, ["CONTRIBUTOR", "RECORDS"]);
    requireVersion(current, input.expectedVersion);
    const target = database.prepare("SELECT id, role, active, expires_at FROM api_tokens WHERE id = ? AND organization_id = ?")
      .get(input.tokenId, principal.organizationId);
    if (!target || target.active !== 1 || target.expires_at <= iso(now)) {
      throw new HttpError(400, "INVALID_ACCESS_TARGET", "The target must be an active, unexpired token in this tenant");
    }
    const compatible = {
      CONTRIBUTOR: ["AUTHOR"], REVIEWER: ["LEGAL_REVIEWER"], RECORDS: ["RECORDS_MANAGER"],
      READ_ONLY: ["AUTHOR", "LEGAL_REVIEWER", "RECORDS_MANAGER", "AUDITOR"],
    };
    if (!compatible[input.privilege].includes(target.role)) throw new HttpError(400, "PRIVILEGE_ROLE_MISMATCH", "The target role cannot receive that privilege");
    const existing = database.prepare("SELECT id, active FROM matter_access WHERE matter_id = ? AND token_id = ?").get(matterId, input.tokenId);
    if (existing?.active === 1) throw new HttpError(409, "ACCESS_ALREADY_ACTIVE", "That token already has active matter access");
    if (existing) {
      database.prepare(`UPDATE matter_access SET privilege = ?, active = 1, granted_by = ?, revoked_by = NULL,
        granted_at = ?, revoked_at = NULL WHERE id = ?`).run(input.privilege, principal.tokenId, iso(now), existing.id);
    } else {
      database.prepare(`INSERT INTO matter_access
        (id, organization_id, matter_id, token_id, privilege, granted_by, granted_at) VALUES (?, ?, ?, ?, ?, ?, ?)`)
        .run(randomUUID(), principal.organizationId, matterId, input.tokenId, input.privilege, principal.tokenId, iso(now));
    }
    database.prepare("UPDATE matters SET version = version + 1, updated_at = ? WHERE id = ?").run(iso(now), matterId);
    appendAudit(database, { organizationId: principal.organizationId, matterId, actorTokenId: principal.tokenId,
      eventType: "ACCESS_GRANTED", payload: { tokenId: input.tokenId, privilege: input.privilege }, createdAt: now });
    return { matterId, tokenId: input.tokenId, privilege: input.privilege, active: true, matterVersion: current.version + 1 };
  }});
}

export function revokeAccess({ database, config, principal, key, matterId, tokenId, body, now = new Date() }) {
  const input = parse(revokeSchema, body);
  return syncOperation({ database, config, principal, key, request: { action: "revokeAccess", matterId, tokenId, input }, operation: () => {
    const current = matter(database, principal, matterId);
    requireAccess(database, principal, matterId, ["CONTRIBUTOR", "RECORDS"]);
    requireVersion(current, input.expectedVersion);
    if (tokenId === current.opened_by) throw new HttpError(409, "OPENER_ACCESS_REQUIRED", "The matter opener cannot be revoked");
    const access = database.prepare("SELECT id, active FROM matter_access WHERE matter_id = ? AND token_id = ?").get(matterId, tokenId);
    if (!access || access.active !== 1) throw new HttpError(404, "ACTIVE_ACCESS_NOT_FOUND", "Active access not found");
    database.prepare("UPDATE matter_access SET active = 0, revoked_by = ?, revoked_at = ? WHERE id = ?")
      .run(principal.tokenId, iso(now), access.id);
    database.prepare("UPDATE matters SET version = version + 1, updated_at = ? WHERE id = ?").run(iso(now), matterId);
    appendAudit(database, { organizationId: principal.organizationId, matterId, actorTokenId: principal.tokenId,
      eventType: "ACCESS_REVOKED", payload: { tokenId }, createdAt: now });
    return { matterId, tokenId, active: false, matterVersion: current.version + 1 };
  }});
}

export function registerTemplate({ database, config, principal, key, body, now = new Date() }) {
  const input = parse(templateSchema, body);
  return syncOperation({ database, config, principal, key, request: { action: "registerTemplate", input }, status: 201, operation: () => {
    const id = randomUUID();
    database.prepare(`INSERT INTO authoritative_templates
      (id, organization_id, template_key, title, jurisdiction, source_uri, content_sha256, effective_from, effective_until, created_by, created_at)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`)
      .run(id, principal.organizationId, input.templateKey, input.title, input.jurisdiction, input.sourceUri,
        input.contentSha256, input.effectiveFrom, input.effectiveUntil ?? null, principal.tokenId, iso(now));
    appendAudit(database, { organizationId: principal.organizationId, actorTokenId: principal.tokenId,
      eventType: "TEMPLATE_REGISTERED", payload: { templateId: id, templateKey: input.templateKey,
        jurisdiction: input.jurisdiction, contentSha256: input.contentSha256, effectiveFrom: input.effectiveFrom,
        effectiveUntil: input.effectiveUntil ?? null }, createdAt: now });
    return { template: { id, ...input } };
  }});
}

function validateTemplate(database, principal, templateId, currentMatter, asOf) {
  const template = database.prepare("SELECT * FROM authoritative_templates WHERE id = ? AND organization_id = ?")
    .get(templateId, principal.organizationId);
  if (!template) throw new HttpError(404, "TEMPLATE_NOT_FOUND", "Authoritative template not found");
  if (template.jurisdiction !== currentMatter.jurisdiction) throw new HttpError(409, "JURISDICTION_MISMATCH", "Template and matter jurisdictions differ");
  if (template.effective_from > asOf || (template.effective_until && template.effective_until < asOf)) {
    throw new HttpError(409, "TEMPLATE_NOT_EFFECTIVE", "The template is not effective on the operation date");
  }
  return template;
}
function insertVersion(database, principal, documentId, number, input, now) {
  const id = randomUUID();
  database.prepare(`INSERT INTO document_versions
    (id, organization_id, document_id, version_number, source_uri, source_sha256, redacted_uri, redacted_sha256,
     redaction_reason, created_by, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`)
    .run(id, principal.organizationId, documentId, number, input.sourceUri, input.sourceSha256,
      input.redactedUri ?? null, input.redactedSha256 ?? null, input.redactionReason ?? null, principal.tokenId, iso(now));
  return id;
}

export function createDocument({ database, config, principal, key, matterId, body, now = new Date() }) {
  const input = parse(documentSchema, body);
  return syncOperation({ database, config, principal, key, request: { action: "createDocument", matterId, input }, status: 201, operation: () => {
    const currentMatter = matter(database, principal, matterId);
    requireAccess(database, principal, matterId, ["CONTRIBUTOR"]);
    if (currentMatter.state !== "OPEN") throw new HttpError(409, "MATTER_CLOSED", "Documents cannot be added to a closed matter");
    validateTemplate(database, principal, input.templateId, currentMatter, today(now));
    const id = randomUUID();
    database.prepare(`INSERT INTO documents
      (id, organization_id, matter_id, template_id, title, created_by, created_at, updated_at)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?)`)
      .run(id, principal.organizationId, matterId, input.templateId, input.title, principal.tokenId, iso(now), iso(now));
    const versionId = insertVersion(database, principal, id, 1, input, now);
    appendAudit(database, { organizationId: principal.organizationId, matterId, documentId: id, documentVersionId: versionId,
      actorTokenId: principal.tokenId, eventType: "DOCUMENT_CREATED", toState: "DRAFT",
      payload: { templateId: input.templateId, versionNumber: 1, sourceSha256: input.sourceSha256,
        redactedSha256: input.redactedSha256 ?? null }, createdAt: now });
    return { document: { id, matterId, templateId: input.templateId, title: input.title, state: "DRAFT", version: 1, currentVersion: 1 }, versionId };
  }});
}

export function createDocumentVersion({ database, config, principal, key, documentId, body, now = new Date() }) {
  const input = parse(versionSchema, body);
  return syncOperation({ database, config, principal, key, request: { action: "createDocumentVersion", documentId, input }, status: 201, operation: () => {
    const current = document(database, principal, documentId);
    requireAccess(database, principal, current.matter_id, ["CONTRIBUTOR"]);
    requireVersion(current, input.expectedVersion);
    if (!["DRAFT", "REJECTED"].includes(current.state)) throw new HttpError(409, "VERSION_NOT_ALLOWED", "A new version requires a draft or rejected document");
    const number = current.current_version + 1;
    const versionId = insertVersion(database, principal, documentId, number, input, now);
    database.prepare(`UPDATE documents SET current_version = ?, state = 'DRAFT', version = version + 1,
      reviewer_id = NULL, review_note = NULL, updated_at = ? WHERE id = ?`).run(number, iso(now), documentId);
    appendAudit(database, { organizationId: principal.organizationId, matterId: current.matter_id, documentId,
      documentVersionId: versionId, actorTokenId: principal.tokenId, eventType: "DOCUMENT_VERSION_CREATED",
      fromState: current.state, toState: "DRAFT", payload: { versionNumber: number, sourceSha256: input.sourceSha256,
        redactedSha256: input.redactedSha256 ?? null }, createdAt: now });
    return { documentId, versionId, currentVersion: number, state: "DRAFT", version: current.version + 1 };
  }});
}

function saveAttempts(database, principal, documentId, action, attempts, now) {
  const statement = database.prepare(`INSERT INTO provider_attempts
    (id, organization_id, document_id, action, attempt_number, outcome, error_code, provider_status, created_at)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`);
  for (const attempt of attempts) statement.run(randomUUID(), principal.organizationId, documentId, action,
    attempt.attempt, attempt.outcome, attempt.errorCode, attempt.providerStatus, iso(now));
}

export async function runOcr({ database, config, principal, key, documentId, body, fetchImplementation = fetch, now = new Date() }) {
  const input = parse(transitionSchema, body);
  const context = prepareOperation(database, config, principal, key, { action: "runOcr", documentId, input });
  if (context.replay) return replay(context);
  const started = transaction(database, () => {
    const current = document(database, principal, documentId);
    requireAccess(database, principal, current.matter_id, ["CONTRIBUTOR"]);
    requireVersion(current, input.expectedVersion);
    if (current.state !== "DRAFT") throw new HttpError(409, "OCR_NOT_ALLOWED", "OCR is allowed only for a draft");
    const version = database.prepare("SELECT * FROM document_versions WHERE document_id = ? AND version_number = ?")
      .get(documentId, current.current_version);
    if (!["PENDING", "FAILED"].includes(version.ocr_state)) throw new HttpError(409, "OCR_ALREADY_RUNNING_OR_COMPLETE", "OCR is already running or complete");
    database.prepare(`UPDATE document_versions SET ocr_state = 'PROCESSING', ocr_operation_hash = ?, ocr_started_at = ?,
      ocr_error_code = NULL WHERE id = ?`).run(context.keyHash, iso(now), version.id);
    database.prepare("UPDATE documents SET version = version + 1, updated_at = ? WHERE id = ?").run(iso(now), documentId);
    appendAudit(database, { organizationId: principal.organizationId, matterId: current.matter_id, documentId,
      documentVersionId: version.id, actorTokenId: principal.tokenId, eventType: "OCR_STARTED",
      payload: { versionNumber: version.version_number }, createdAt: now });
    return { current, version };
  });
  const provider = await invokeDocumentProvider(config, {
    action: "OCR", organizationId: principal.organizationId, matterId: started.current.matter_id, documentId,
    versionNumber: started.version.version_number, sourceUri: started.version.source_uri,
    sourceSha256: started.version.source_sha256,
  }, context.keyHash, fetchImplementation);
  return transaction(database, () => {
    saveAttempts(database, principal, documentId, "OCR", provider.attempts, now);
    const finalState = provider.ok ? "VERIFIED" : "FAILED";
    const errorCode = provider.ok ? null : provider.attempts.at(-1).errorCode;
    const changed = database.prepare(`UPDATE document_versions SET ocr_state = ?, ocr_receipt = ?, ocr_error_code = ?,
      ocr_operation_hash = NULL, ocr_started_at = NULL WHERE id = ? AND ocr_operation_hash = ?`)
      .run(finalState, provider.ok ? provider.receiptId : null, errorCode, started.version.id, context.keyHash);
    if (changed.changes !== 1) throw new HttpError(409, "PROVIDER_OPERATION_LOST", "The OCR operation lease no longer matches");
    database.prepare("UPDATE documents SET version = version + 1, updated_at = ? WHERE id = ?").run(iso(now), documentId);
    appendAudit(database, { organizationId: principal.organizationId, matterId: started.current.matter_id, documentId,
      documentVersionId: started.version.id, actorTokenId: principal.tokenId,
      eventType: provider.ok ? "OCR_VERIFIED" : "OCR_FAILED", payload: provider.ok ? { receiptId: provider.receiptId } : { errorCode }, createdAt: now });
    const status = provider.ok ? 200 : 502;
    const response = { documentId, documentVersionId: started.version.id, ocrState: finalState,
      receiptId: provider.ok ? provider.receiptId : null, errorCode, version: started.current.version + 2 };
    recordOperation(database, principal, context, status, response, now);
    return { status, body: response, replayed: false };
  });
}

export function submitDocument({ database, config, principal, key, documentId, body, now = new Date() }) {
  const input = parse(transitionSchema, body);
  return syncOperation({ database, config, principal, key, request: { action: "submitDocument", documentId, input }, operation: () => {
    const current = document(database, principal, documentId);
    requireAccess(database, principal, current.matter_id, ["CONTRIBUTOR"]);
    requireVersion(current, input.expectedVersion);
    if (current.state !== "DRAFT") throw new HttpError(409, "SUBMIT_NOT_ALLOWED", "Only a draft can be submitted");
    const currentMatter = matter(database, principal, current.matter_id);
    validateTemplate(database, principal, current.template_id, currentMatter, today(now));
    const version = database.prepare("SELECT id, ocr_state FROM document_versions WHERE document_id = ? AND version_number = ?")
      .get(documentId, current.current_version);
    if (version.ocr_state !== "VERIFIED") throw new HttpError(409, "OCR_REQUIRED", "The current immutable version must have verified OCR evidence");
    database.prepare("UPDATE documents SET state = 'IN_REVIEW', version = version + 1, updated_at = ? WHERE id = ?")
      .run(iso(now), documentId);
    appendAudit(database, { organizationId: principal.organizationId, matterId: current.matter_id, documentId,
      documentVersionId: version.id, actorTokenId: principal.tokenId, eventType: "DOCUMENT_SUBMITTED",
      fromState: "DRAFT", toState: "IN_REVIEW", payload: { versionNumber: current.current_version }, createdAt: now });
    return { document: { ...documentBody(current), state: "IN_REVIEW", version: current.version + 1 } };
  }});
}

export function reviewDocument({ database, config, principal, key, documentId, body, now = new Date() }) {
  const input = parse(reviewSchema, body);
  return syncOperation({ database, config, principal, key, request: { action: "reviewDocument", documentId, input }, operation: () => {
    const current = document(database, principal, documentId);
    requireAccess(database, principal, current.matter_id, ["REVIEWER"]);
    requireVersion(current, input.expectedVersion);
    if (current.state !== "IN_REVIEW") throw new HttpError(409, "REVIEW_NOT_ALLOWED", "The document is not awaiting review");
    const version = database.prepare("SELECT id, created_by FROM document_versions WHERE document_id = ? AND version_number = ?")
      .get(documentId, current.current_version);
    validateTemplate(database, principal, current.template_id, matter(database, principal, current.matter_id), today(now));
    if (current.created_by === principal.tokenId || version.created_by === principal.tokenId) {
      throw new HttpError(409, "INDEPENDENT_REVIEW_REQUIRED", "A document author cannot approve or reject their own version");
    }
    const state = input.decision === "APPROVE" ? "APPROVED" : "REJECTED";
    database.prepare("UPDATE documents SET state = ?, reviewer_id = ?, review_note = ?, version = version + 1, updated_at = ? WHERE id = ?")
      .run(state, principal.tokenId, input.note, iso(now), documentId);
    appendAudit(database, { organizationId: principal.organizationId, matterId: current.matter_id, documentId,
      documentVersionId: version.id, actorTokenId: principal.tokenId,
      eventType: state === "APPROVED" ? "DOCUMENT_APPROVED" : "DOCUMENT_REJECTED",
      fromState: "IN_REVIEW", toState: state, payload: { reviewNoteSha256: createHash("sha256").update(input.note).digest("hex") }, createdAt: now });
    return { document: { ...documentBody(current), state, version: current.version + 1, reviewerId: principal.tokenId } };
  }});
}

async function providerTransition({ database, config, principal, key, documentId, input, action, allowedStates,
  activeState, failedState, successState, startedEvent, failedEvent, successEvent, access, payload, validateResult,
  successFields, fetchImplementation, now }) {
  const context = prepareOperation(database, config, principal, key, { action, documentId, input });
  if (context.replay) return replay(context);
  const started = transaction(database, () => {
    const current = document(database, principal, documentId);
    requireAccess(database, principal, current.matter_id, access);
    requireVersion(current, input.expectedVersion);
    if (!allowedStates.includes(current.state)) throw new HttpError(409, `${action}_NOT_ALLOWED`, `${action} is not allowed from ${current.state}`);
    const version = database.prepare("SELECT * FROM document_versions WHERE document_id = ? AND version_number = ?")
      .get(documentId, current.current_version);
    database.prepare(`UPDATE documents SET state = ?, provider_operation_hash = ?, processing_started_at = ?,
      version = version + 1, updated_at = ? WHERE id = ?`).run(activeState, context.keyHash, iso(now), iso(now), documentId);
    appendAudit(database, { organizationId: principal.organizationId, matterId: current.matter_id, documentId,
      documentVersionId: version.id, actorTokenId: principal.tokenId, eventType: startedEvent,
      fromState: current.state, toState: activeState, payload: {}, createdAt: now });
    return { current, version };
  });
  const provider = await invokeDocumentProvider(config, payload(started), context.keyHash, fetchImplementation);
  const accepted = provider.ok && (!validateResult || validateResult(provider));
  if (provider.ok && !accepted) {
    provider.ok = false;
    provider.attempts[provider.attempts.length - 1] = {
      ...provider.attempts.at(-1), outcome: "FAILED", errorCode: "INVALID_PROVIDER_EVIDENCE",
    };
  }
  return transaction(database, () => {
    saveAttempts(database, principal, documentId, action, provider.attempts, now);
    const state = accepted ? successState : failedState;
    const errorCode = accepted ? null : provider.attempts.at(-1).errorCode;
    const fields = accepted ? successFields(provider) : {};
    const setters = ["state = ?", "provider_operation_hash = NULL", "processing_started_at = NULL", "version = version + 1", "updated_at = ?"];
    const values = [state, iso(now)];
    for (const [column, value] of Object.entries(fields)) { setters.push(`${column} = ?`); values.push(value); }
    values.push(documentId, context.keyHash);
    const changed = database.prepare(`UPDATE documents SET ${setters.join(", ")} WHERE id = ? AND provider_operation_hash = ?`).run(...values);
    if (changed.changes !== 1) throw new HttpError(409, "PROVIDER_OPERATION_LOST", "The provider operation lease no longer matches");
    appendAudit(database, { organizationId: principal.organizationId, matterId: started.current.matter_id, documentId,
      documentVersionId: started.version.id, actorTokenId: principal.tokenId, eventType: accepted ? successEvent : failedEvent,
      fromState: activeState, toState: state, payload: accepted ? { receiptId: provider.receiptId } : { errorCode }, createdAt: now });
    const status = accepted ? 200 : 502;
    const response = { documentId, state, version: started.current.version + 2, receiptId: accepted ? provider.receiptId : null, errorCode };
    recordOperation(database, principal, context, status, response, now);
    return { status, body: response, replayed: false };
  });
}

export async function signDocument(args) {
  const input = parse(signSchema, args.body);
  return providerTransition({ ...args, input, action: "SIGN", allowedStates: ["APPROVED", "SIGNATURE_FAILED"],
    activeState: "SIGNING", failedState: "SIGNATURE_FAILED", successState: "SIGNED",
    startedEvent: "SIGNING_STARTED", failedEvent: "SIGNATURE_FAILED", successEvent: "DOCUMENT_SIGNED",
    access: ["CONTRIBUTOR"], now: args.now ?? new Date(),
    payload: ({ current, version }) => ({ action: "SIGN", organizationId: args.principal.organizationId,
      matterId: current.matter_id, documentId: current.id, versionNumber: current.current_version,
      sourceUri: version.redacted_uri ?? version.source_uri, sourceSha256: version.redacted_sha256 ?? version.source_sha256,
      signerEmail: input.signerEmail }),
    successFields: (provider) => ({ signature_receipt: provider.receiptId,
      signer_hash: keyedHash(input.signerEmail.toLowerCase(), args.config.idempotencySecret) }),
  });
}

export async function fileDocument(args) {
  const input = parse(fileSchema, args.body);
  if (input.retentionUntil < today(args.now ?? new Date())) throw new HttpError(400, "INVALID_RETENTION_DATE", "retentionUntil cannot be in the past");
  return providerTransition({ ...args, input, action: "FILE", allowedStates: ["SIGNED", "FILING_FAILED"],
    activeState: "FILING", failedState: "FILING_FAILED", successState: "FILED",
    startedEvent: "FILING_STARTED", failedEvent: "FILING_FAILED", successEvent: "DOCUMENT_FILED",
    access: ["RECORDS"], now: args.now ?? new Date(),
    payload: ({ current, version }) => ({ action: "FILE", organizationId: args.principal.organizationId,
      matterId: current.matter_id, documentId: current.id, versionNumber: current.current_version,
      sourceUri: version.redacted_uri ?? version.source_uri, sourceSha256: version.redacted_sha256 ?? version.source_sha256,
      signatureReceipt: current.signature_receipt, retentionUntil: input.retentionUntil }),
    validateResult: (provider) => provider.artifactUri && provider.artifactSha256 && new URL(provider.artifactUri).protocol === "https:",
    successFields: (provider) => ({ filing_receipt: provider.receiptId, repository_uri: provider.artifactUri,
      repository_sha256: provider.artifactSha256, retention_until: input.retentionUntil }),
  });
}

export function setLegalHold({ database, config, principal, key, documentId, body, now = new Date() }) {
  const input = parse(holdSchema, body);
  return syncOperation({ database, config, principal, key, request: { action: "setLegalHold", documentId, input }, operation: () => {
    const current = document(database, principal, documentId);
    requireAccess(database, principal, current.matter_id, ["RECORDS"]);
    requireVersion(current, input.expectedVersion);
    if (current.state !== "FILED") throw new HttpError(409, "HOLD_NOT_ALLOWED", "Legal hold changes require a filed document");
    if (Boolean(current.legal_hold) === input.active) throw new HttpError(409, "HOLD_UNCHANGED", "The requested legal-hold state is already active");
    database.prepare("UPDATE documents SET legal_hold = ?, hold_reason = ?, version = version + 1, updated_at = ? WHERE id = ?")
      .run(input.active ? 1 : 0, input.active ? input.reason : null, iso(now), documentId);
    appendAudit(database, { organizationId: principal.organizationId, matterId: current.matter_id, documentId,
      actorTokenId: principal.tokenId, eventType: input.active ? "LEGAL_HOLD_APPLIED" : "LEGAL_HOLD_RELEASED",
      payload: input.active ? { reasonDigest: keyedHash(input.reason, config.idempotencySecret) } : {}, createdAt: now });
    return { documentId, legalHold: input.active, version: current.version + 1 };
  }});
}

export function disposeDocument({ database, config, principal, key, documentId, body, now = new Date() }) {
  const input = parse(transitionSchema, body);
  return syncOperation({ database, config, principal, key, request: { action: "disposeDocument", documentId, input }, operation: () => {
    const current = document(database, principal, documentId);
    requireAccess(database, principal, current.matter_id, ["RECORDS"]);
    requireVersion(current, input.expectedVersion);
    if (current.state !== "FILED") throw new HttpError(409, "DISPOSITION_NOT_ALLOWED", "Only a filed document can be disposed");
    if (current.legal_hold === 1) throw new HttpError(409, "LEGAL_HOLD_ACTIVE", "Release the legal hold before disposition");
    if (!current.retention_until || current.retention_until > today(now)) throw new HttpError(409, "RETENTION_ACTIVE", "The retention period has not elapsed");
    database.prepare("UPDATE documents SET state = 'DISPOSED', disposed_at = ?, version = version + 1, updated_at = ? WHERE id = ?")
      .run(iso(now), iso(now), documentId);
    appendAudit(database, { organizationId: principal.organizationId, matterId: current.matter_id, documentId,
      actorTokenId: principal.tokenId, eventType: "DOCUMENT_DISPOSED", fromState: "FILED", toState: "DISPOSED",
      payload: { retentionUntil: current.retention_until }, createdAt: now });
    return { documentId, state: "DISPOSED", version: current.version + 1 };
  }});
}

export function exportMatter({ database, config, principal, key, matterId, body, now = new Date() }) {
  const input = parse(z.object({}).strict(), body);
  return syncOperation({ database, config, principal, key, request: { action: "exportMatter", matterId, input }, operation: () => {
    const current = matter(database, principal, matterId);
    requireAccess(database, principal, matterId, ["CONTRIBUTOR", "REVIEWER", "RECORDS", "READ_ONLY"]);
    const documents = database.prepare(`SELECT id, template_id, title, state, version, current_version, reviewer_id,
      signature_receipt, filing_receipt, repository_sha256, retention_until, legal_hold, disposed_at FROM documents
      WHERE matter_id = ? AND organization_id = ? ORDER BY created_at, id`).all(matterId, principal.organizationId);
    const versions = database.prepare(`SELECT dv.document_id, dv.id, dv.version_number, dv.source_sha256, dv.redacted_sha256,
      dv.ocr_state, dv.ocr_receipt, dv.created_by, dv.created_at FROM document_versions dv
      JOIN documents d ON d.id = dv.document_id WHERE d.matter_id = ? AND dv.organization_id = ?
      ORDER BY dv.document_id, dv.version_number`).all(matterId, principal.organizationId);
    const audit = verifyAudit(database, principal.organizationId);
    if (!audit.ok) throw new HttpError(409, "AUDIT_CHAIN_INVALID", "The audit chain must verify before export");
    const manifest = { schemaVersion: 1, exportedAt: iso(now), matter: { id: current.id, matterKey: current.matter_key,
      title: current.title, jurisdiction: current.jurisdiction, state: current.state, version: current.version },
      documents: documents.map((row) => ({ id: row.id, templateId: row.template_id, title: row.title, state: row.state,
        version: row.version, currentVersion: row.current_version, reviewerId: row.reviewer_id,
        signatureReceipt: row.signature_receipt, filingReceipt: row.filing_receipt,
        repositorySha256: row.repository_sha256,
        retentionUntil: row.retention_until, legalHold: Boolean(row.legal_hold), disposedAt: row.disposed_at })),
      versions: versions.map((row) => ({ documentId: row.document_id, id: row.id, versionNumber: row.version_number,
        sourceSha256: row.source_sha256, redactedSha256: row.redacted_sha256, ocrState: row.ocr_state,
        ocrReceipt: row.ocr_receipt, createdBy: row.created_by, createdAt: row.created_at })),
      auditHead: audit.head, auditEventsChecked: audit.checked };
    const manifestSha256 = createHash("sha256").update(canonicalJson(manifest)).digest("hex");
    appendAudit(database, { organizationId: principal.organizationId, matterId, actorTokenId: principal.tokenId,
      eventType: "MATTER_EXPORTED", payload: { manifestSha256 }, createdAt: now });
    return { manifest, manifestSha256 };
  }});
}

export function getMatterView(database, principal, matterId) {
  const current = matter(database, principal, matterId);
  const privilege = requireAccess(database, principal, matterId, ["CONTRIBUTOR", "REVIEWER", "RECORDS", "READ_ONLY"]);
  const docs = database.prepare("SELECT * FROM documents WHERE matter_id = ? AND organization_id = ? ORDER BY created_at")
    .all(matterId, principal.organizationId).map((row) => {
      const view = documentBody(row);
      if (!["CONTRIBUTOR", "RECORDS"].includes(privilege)) view.repositoryUri = null;
      return view;
    });
  return { matter: { id: current.id, matterKey: current.matter_key, title: current.title, jurisdiction: current.jurisdiction,
    state: current.state, version: current.version, privilege }, documents: docs };
}
