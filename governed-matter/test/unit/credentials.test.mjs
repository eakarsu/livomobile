import assert from "node:assert/strict";
import test from "node:test";
import { passwordRecord, parseSessionBearer, verifyPassword } from "../../src/credentials.mjs";

test("credential passwords use salted scrypt records and session tokens parse narrowly", () => {
  const password = "correct horse battery staple";
  const record = passwordRecord(password);
  assert.equal(record.salt.length, 32);
  assert.equal(record.hash.length, 128);
  assert.equal(verifyPassword(password, record.salt, record.hash), true);
  assert.equal(verifyPassword("incorrect-password", record.salt, record.hash), false);
  const sessionToken = `lvs_${"a".repeat(36)}_${"b".repeat(43)}`;
  assert.equal(parseSessionBearer(`Bearer ${sessionToken}`), sessionToken);
  assert.equal(parseSessionBearer("Bearer lvs_short"), null);
});
