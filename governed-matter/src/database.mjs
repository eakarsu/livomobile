import { mkdirSync } from "node:fs";
import path from "node:path";
import Database from "better-sqlite3";

export function openDatabase(databasePath) {
  if (databasePath !== ":memory:") mkdirSync(path.dirname(databasePath), { recursive: true, mode: 0o700 });
  const database = new Database(databasePath);
  database.exec("PRAGMA foreign_keys = ON; PRAGMA busy_timeout = 5000; PRAGMA journal_mode = WAL; PRAGMA synchronous = FULL;");
  return database;
}

export function transaction(database, operation) {
  database.exec("BEGIN IMMEDIATE");
  try {
    const result = operation();
    database.exec("COMMIT");
    return result;
  } catch (error) {
    database.exec("ROLLBACK");
    throw error;
  }
}
