import assert from "node:assert/strict";
import test from "node:test";
import { openDatabase } from "../../src/database.mjs";
import { migrate, verifyMigrations } from "../../src/migrations.mjs";

test("repeat migrations are stable and checksum drift fails closed", () => {
  const database = openDatabase(":memory:");
  try {
    migrate(database, new Date("2026-07-20T00:00:00Z"));
    migrate(database, new Date("2026-07-20T00:00:01Z"));
    assert.deepEqual(verifyMigrations(database), { ok: true, count: 2 });
    database.prepare("UPDATE schema_migrations SET checksum = ?").run("0".repeat(64));
    assert.equal(verifyMigrations(database).ok, false);
    assert.throws(() => migrate(database), /checksum mismatch/);
  } finally {
    database.close();
  }
});
