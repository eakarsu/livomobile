import path from "node:path";
import { z } from "zod";

const booleanValue = z.enum(["true", "false"]).transform((value) => value === "true");
const schema = z.object({
  NODE_ENV: z.enum(["development", "test", "production"]).default("development"),
  PORT: z.coerce.number().int().min(1).max(65535).default(3030),
  DATABASE_PATH: z.string().min(1),
  MIGRATE_ON_START: booleanValue.default("false"),
  ALLOWED_ORIGINS: z.string().default(""),
  IDEMPOTENCY_SECRET: z.string().min(32),
  DOCUMENT_PROVIDER_URL: z.string().url(),
  DOCUMENT_PROVIDER_SECRET: z.string().min(32),
  DOCUMENT_PROVIDER_TIMEOUT_MS: z.coerce.number().int().min(100).max(30_000).default(3000),
  DOCUMENT_PROVIDER_MAX_ATTEMPTS: z.coerce.number().int().min(1).max(5).default(3),
  SESSION_TTL_HOURS: z.coerce.number().int().min(1).max(168).default(12),
  OPENROUTER_API_KEY: z.string().min(1).optional(),
  OPENROUTER_MODEL: z.string().min(2).max(200).default("openai/gpt-4o-mini"),
  OPENROUTER_BASE_URL: z.string().url().default("https://openrouter.ai/api/v1"),
  OPENROUTER_TIMEOUT_MS: z.coerce.number().int().min(1000).max(120_000).default(60_000),
});

export function loadConfig(environment = process.env) {
  const result = schema.safeParse(environment);
  if (!result.success) {
    throw new Error(`Invalid configuration: ${result.error.issues.map((issue) => `${issue.path.join(".")}: ${issue.message}`).join("; ")}`);
  }
  const value = result.data;
  if (value.DATABASE_PATH !== ":memory:" && !path.isAbsolute(value.DATABASE_PATH)) {
    throw new Error("DATABASE_PATH must be absolute or :memory:");
  }
  const providerUrl = new URL(value.DOCUMENT_PROVIDER_URL);
  if (value.NODE_ENV === "production" && providerUrl.protocol !== "https:") {
    throw new Error("DOCUMENT_PROVIDER_URL must use HTTPS in production");
  }
  if (!["http:", "https:"].includes(providerUrl.protocol)) throw new Error("DOCUMENT_PROVIDER_URL must use HTTP or HTTPS");
  const openRouterBaseUrl = new URL(value.OPENROUTER_BASE_URL);
  if (openRouterBaseUrl.toString().replace(/\/$/, "") !== "https://openrouter.ai/api/v1") {
    throw new Error("OPENROUTER_BASE_URL must be the canonical https://openrouter.ai/api/v1 endpoint");
  }
  const allowedOrigins = value.ALLOWED_ORIGINS.split(",").map((item) => item.trim()).filter(Boolean);
  for (const origin of allowedOrigins) {
    if (new URL(origin).origin !== origin) throw new Error(`Invalid ALLOWED_ORIGINS value: ${origin}`);
  }
  return {
    nodeEnv: value.NODE_ENV,
    port: value.PORT,
    databasePath: value.DATABASE_PATH,
    migrateOnStart: value.MIGRATE_ON_START,
    allowedOrigins,
    idempotencySecret: value.IDEMPOTENCY_SECRET,
    documentProviderUrl: providerUrl.toString(),
    documentProviderSecret: value.DOCUMENT_PROVIDER_SECRET,
    documentProviderTimeoutMs: value.DOCUMENT_PROVIDER_TIMEOUT_MS,
    documentProviderMaxAttempts: value.DOCUMENT_PROVIDER_MAX_ATTEMPTS,
    sessionTtlHours: value.SESSION_TTL_HOURS,
    openRouterApiKey: value.OPENROUTER_API_KEY ?? null,
    openRouterModel: value.OPENROUTER_MODEL,
    openRouterBaseUrl: openRouterBaseUrl.toString().replace(/\/$/, ""),
    openRouterTimeoutMs: value.OPENROUTER_TIMEOUT_MS,
  };
}
