import { z } from "zod";
import { loadConfig } from "../src/config.mjs";
import { ensureCredentialUser } from "../src/credentials.mjs";
import { openDatabase, transaction } from "../src/database.mjs";
import { verifyMigrations } from "../src/migrations.mjs";

const schema = z.object({
  ADMIN_EMAIL: z.string().email().max(254),
  ADMIN_PASSWORD: z.string().min(12).max(256),
  ADMIN_ORGANIZATION_SLUG: z.string().regex(/^[a-z0-9][a-z0-9-]{1,62}$/).default("runtime-tenant"),
  ADMIN_ORGANIZATION_NAME: z.string().min(2).max(160).default("Runtime Tenant"),
  ADMIN_ROLE: z.enum(["AUTHOR", "LEGAL_REVIEWER", "RECORDS_MANAGER", "AUDITOR"]).default("AUTHOR"),
});
const input = schema.safeParse(process.env);
if (!input.success) throw new Error(`Invalid credential bootstrap configuration: ${input.error.issues.map((issue) => issue.path.join(".")).join(", ")}`);
const config = loadConfig();
const database = openDatabase(config.databasePath);
try {
  const migrationState = verifyMigrations(database);
  if (!migrationState.ok) throw new Error(`Run migrations first: ${migrationState.reason}`);
  const result = transaction(database, () => ensureCredentialUser({
    database, email: input.data.ADMIN_EMAIL, password: input.data.ADMIN_PASSWORD,
    organizationSlug: input.data.ADMIN_ORGANIZATION_SLUG, organizationName: input.data.ADMIN_ORGANIZATION_NAME,
    role: input.data.ADMIN_ROLE,
  }));
  console.info(`Credential bootstrap ${result.created ? "created" : "verified"} one ${input.data.ADMIN_ROLE} user.`);
} finally { database.close(); }
