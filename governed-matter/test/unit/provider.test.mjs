import assert from "node:assert/strict";
import { createHmac } from "node:crypto";
import test from "node:test";
import { invokeDocumentProvider } from "../../src/provider.mjs";
const config = {
  documentProviderUrl: "https://provider.example/operations", documentProviderSecret: "s".repeat(32),
  documentProviderTimeoutMs: 500, documentProviderMaxAttempts: 2,
};
test("provider requests are signed, retry-bounded, and idempotent", async () => {
  const calls = [];
  const payload = { action: "OCR", documentId: "document-1" };
  const result = await invokeDocumentProvider(config, payload, "provider-operation-0001", async (_url, options) => {
    calls.push(options);
    if (calls.length === 1) return new Response("unavailable", { status: 503 });
    return Response.json({ receiptId: "ocr-receipt-1" });
  });
  assert.equal(result.ok, true);
  assert.equal(calls.length, 2);
  assert.equal(calls[0].headers["Idempotency-Key"], "provider-operation-0001");
  assert.equal(calls[0].headers["X-Livo-Document-Signature-SHA256"],
    createHmac("sha256", config.documentProviderSecret).update(JSON.stringify(payload)).digest("hex"));
});
test("provider responses reject undeclared fields", async () => {
  const result = await invokeDocumentProvider({ ...config, documentProviderMaxAttempts: 1 }, { action: "SIGN" }, "provider-operation-0002",
    async () => Response.json({ receiptId: "receipt", unexpected: true }));
  assert.equal(result.ok, false);
  assert.equal(result.attempts[0].errorCode, "INVALID_PROVIDER_RESPONSE");
});

test("non-retryable provider rejection stops after one call", async () => {
  let calls = 0;
  const result = await invokeDocumentProvider(config, { action: "SIGN" }, "provider-operation-0003", async () => {
    calls += 1;
    return new Response("signer rejected", { status: 422 });
  });
  assert.equal(result.ok, false);
  assert.equal(calls, 1);
  assert.equal(result.attempts[0].errorCode, "PROVIDER_REJECTED");
});
