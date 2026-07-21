import assert from "node:assert/strict";
import test from "node:test";
import { loadConfig } from "../../src/config.mjs";
const base = {
  NODE_ENV: "test", PORT: "3030", DATABASE_PATH: ":memory:", MIGRATE_ON_START: "false",
  ALLOWED_ORIGINS: "https://legal.example",
  IDEMPOTENCY_SECRET: "i".repeat(32), DOCUMENT_PROVIDER_URL: "http://127.0.0.1:9/operations",
  DOCUMENT_PROVIDER_SECRET: "p".repeat(32), DOCUMENT_PROVIDER_TIMEOUT_MS: "500",
  DOCUMENT_PROVIDER_MAX_ATTEMPTS: "2",
};
test("configuration is parsed and origins are exact", () => {
  const config = loadConfig(base);
  assert.deepEqual(config.allowedOrigins, ["https://legal.example"]);
  assert.equal(config.documentProviderMaxAttempts, 2);
});
test("production refuses a non-HTTPS document provider", () => {
  assert.throws(() => loadConfig({ ...base, NODE_ENV: "production" }), /must use HTTPS/);
});
test("database paths and origins fail closed", () => {
  assert.throws(() => loadConfig({ ...base, DATABASE_PATH: "relative.sqlite" }), /must be absolute/);
  assert.throws(() => loadConfig({ ...base, ALLOWED_ORIGINS: "https://legal.example/path" }), /Invalid ALLOWED_ORIGINS/);
});
