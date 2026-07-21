import { existsSync, mkdirSync } from "node:fs";
import path from "node:path";
import { loadConfig } from "../src/config.mjs";
import { openDatabase } from "../src/database.mjs";
import { verifyDatabase } from "./verify.mjs";
const destination = path.resolve(process.argv[2] ?? `backups/matters-${new Date().toISOString().replace(/[:.]/g, "-")}.sqlite`);
if (existsSync(destination)) throw new Error(`Refusing to overwrite existing backup: ${destination}`);
mkdirSync(path.dirname(destination), { recursive: true, mode: 0o700 });
const config = loadConfig();
const source = openDatabase(config.databasePath);
try { verifyDatabase(source); await source.backup(destination); } finally { source.close(); }
const verified = openDatabase(destination);
try { console.info(JSON.stringify({ backup: destination, ...verifyDatabase(verified) })); } finally { verified.close(); }
