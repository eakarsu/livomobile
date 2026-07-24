import assert from "node:assert/strict";
import test from "node:test";
import { createApp } from "../../src/app.mjs";
import { ensureCredentialUser } from "../../src/credentials.mjs";
import { openDatabase, transaction } from "../../src/database.mjs";
import { migrate } from "../../src/migrations.mjs";

test("credential login yields a durable session and protected OpenRouter receipt", async (t) => {
  const database = openDatabase(":memory:");
  migrate(database, new Date("2026-07-24T00:00:00Z"));
  transaction(database, () => ensureCredentialUser({
    database, email: "operator@example.com", password: "correct horse battery staple",
    organizationSlug: "runtime-tenant", organizationName: "Runtime Tenant", role: "AUTHOR",
    now: new Date("2026-07-24T00:00:00Z"),
  }));
  const providerCalls = [];
  const fetchImplementation = async (url, options) => {
    providerCalls.push({ url, options });
    return Response.json({
      id: "generation-test-receipt", model: "openai/test-model",
      choices: [{ message: { content: "Review the current matter state and confirm required approvals." }, finish_reason: "stop" }],
    });
  };
  const config = {
    allowedOrigins: [], sessionTtlHours: 12, openRouterApiKey: "test-provider-credential",
    openRouterModel: "openai/test-model", openRouterBaseUrl: "https://openrouter.ai/api/v1", openRouterTimeoutMs: 5000,
  };
  const app = createApp({ database, config, fetchImplementation, logger: { info() {}, error() {} } });
  const server = app.listen(0);
  t.after(() => { server.close(); database.close(); });
  const baseUrl = `http://127.0.0.1:${server.address().port}`;

  let response = await fetch(`${baseUrl}/v1/ai/ask`, { method: "POST", headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ prompt: "What should happen next?" }) });
  assert.equal(response.status, 401);

  response = await fetch(`${baseUrl}/v1/auth/login`, { method: "POST", headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email: "operator@example.com", password: "wrong-password-value" }) });
  assert.equal(response.status, 401);

  response = await fetch(`${baseUrl}/v1/auth/login`, { method: "POST", headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email: "operator@example.com", password: "correct horse battery staple" }) });
  assert.equal(response.status, 200);
  const login = await response.json();
  assert.match(login.accessToken, /^lvs_/);
  assert.equal(login.identity.email, "operator@example.com");
  const authorization = `Bearer ${login.accessToken}`;

  response = await fetch(`${baseUrl}/v1/auth/me`, { headers: { Authorization: authorization } });
  assert.equal(response.status, 200);
  const identity = await response.json();
  assert.deepEqual({ email: identity.email, role: identity.role, authType: identity.authType },
    { email: "operator@example.com", role: "AUTHOR", authType: "SESSION" });

  response = await fetch(`${baseUrl}/v1/ai/ask`, { method: "POST",
    headers: { Authorization: authorization, "Content-Type": "application/json" },
    body: JSON.stringify({ prompt: "Summarize the operational next step without legal advice." }) });
  assert.equal(response.status, 200);
  const ai = await response.json();
  assert.equal(ai.providerReceipt, "generation-test-receipt");
  assert.equal(ai.providerModel, "openai/test-model");
  assert.equal(providerCalls.length, 1);
  assert.equal(providerCalls[0].url, "https://openrouter.ai/api/v1/chat/completions");
  assert.equal(providerCalls[0].options.headers.Authorization, "Bearer test-provider-credential");

  response = await fetch(`${baseUrl}/v1/ai/interactions/${ai.interactionId}`, { headers: { Authorization: authorization } });
  assert.equal(response.status, 200);
  const persisted = await response.json();
  assert.equal(persisted.status, "SUCCEEDED");
  assert.equal(persisted.providerReceipt, "generation-test-receipt");
  assert.match(persisted.output, /required approvals/);
  assert.equal(database.prepare("SELECT count(*) AS count FROM credential_sessions").get().count, 1);
  assert.equal(database.prepare("SELECT count(*) AS count FROM ai_interactions WHERE status = 'SUCCEEDED'").get().count, 1);

  response = await fetch(`${baseUrl}/v1/auth/logout`, { method: "POST", headers: { Authorization: authorization } });
  assert.equal(response.status, 204);
  response = await fetch(`${baseUrl}/v1/auth/me`, { headers: { Authorization: authorization } });
  assert.equal(response.status, 401);
});
