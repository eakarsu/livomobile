import assert from "node:assert/strict";
import test from "node:test";
import { generateApiToken, parseBearer, tokenDigest } from "../../src/security.mjs";
test("high-entropy API tokens are parsed but only digests need persistence", () => {
  const token = generateApiToken();
  assert.match(token, /^lvm_/);
  assert.equal(parseBearer(`Bearer ${token}`), token);
  assert.equal(tokenDigest(token).length, 64);
  assert.equal(parseBearer("Bearer short"), null);
});
