import { randomUUID } from "node:crypto";
import express from "express";
import helmet from "helmet";
import { z } from "zod";
import { verifyAudit } from "./audit.mjs";
import {
  authenticateCredentials, createCredentialSession, parseSessionBearer, revokeCredentialSession, sessionPrincipal,
} from "./credentials.mjs";
import { HttpError } from "./errors.mjs";
import { verifyMigrations } from "./migrations.mjs";
import { invokeOpenRouter, OpenRouterError } from "./openrouter.mjs";
import { parseBearer, tokenDigest } from "./security.mjs";
import {
  createDocument, createDocumentVersion, createMatter, disposeDocument, exportMatter, fileDocument,
  getMatterView, grantAccess, registerTemplate, revokeAccess, reviewDocument, runOcr, setLegalHold,
  signDocument, submitDocument,
} from "./workflow.mjs";

const asyncRoute = (handler) => (req, res, next) => Promise.resolve(handler(req, res, next)).catch(next);
const send = (res, result) => {
  if (result.replayed) res.setHeader("Idempotent-Replay", "true");
  return res.status(result.status).json(result.body);
};
const loginSchema = z.object({ email: z.string().email().max(254), password: z.string().min(12).max(256) }).strict();
const aiRequestSchema = z.object({ prompt: z.string().trim().min(3).max(12_000) }).strict();

function identityBody(principal) {
  return {
    userId: principal.userId, email: principal.email, organizationId: principal.organizationId,
    organizationSlug: principal.organizationSlug, role: principal.role, authType: principal.authType,
  };
}

export function createApp({ database, config, fetchImplementation = fetch, logger = console }) {
  const app = express();
  app.disable("x-powered-by");
  app.use((req, res, next) => {
    req.requestId = /^[A-Za-z0-9._:-]{8,100}$/.test(req.get("X-Request-Id") ?? "") ? req.get("X-Request-Id") : randomUUID();
    res.setHeader("X-Request-Id", req.requestId);
    const started = Date.now();
    res.on("finish", () => logger.info?.(JSON.stringify({ event: "http_request", requestId: req.requestId,
      method: req.method, path: req.path, status: res.statusCode, durationMs: Date.now() - started,
      actorTokenId: req.principal?.tokenId ?? null, credentialUserId: req.principal?.userId ?? null })));
    next();
  });
  app.use((req, res, next) => {
    const origin = req.get("Origin");
    if (origin && !config.allowedOrigins.includes(origin)) return next(new HttpError(403, "ORIGIN_REJECTED", "This origin is not allowed"));
    if (origin) { res.setHeader("Access-Control-Allow-Origin", origin); res.setHeader("Vary", "Origin"); }
    if (req.method === "OPTIONS") {
      res.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
      res.setHeader("Access-Control-Allow-Headers", "Authorization,Content-Type,Idempotency-Key,X-Request-Id");
      return res.status(204).end();
    }
    next();
  });
  app.use(helmet());
  app.use(express.json({ limit: "64kb", strict: true }));

  app.get("/health/live", (_req, res) => res.json({ status: "live" }));
  app.get("/health/ready", (_req, res) => {
    const migrations = verifyMigrations(database);
    const activeTokens = migrations.ok ? database.prepare("SELECT count(*) AS count FROM api_tokens WHERE active = 1 AND expires_at > ?")
      .get(new Date().toISOString()).count : 0;
    const ready = migrations.ok && activeTokens > 0;
    res.status(ready ? 200 : 503).json({ status: ready ? "ready" : "not_ready", migrations, activeTokens: Number(activeTokens) });
  });

  app.post("/v1/auth/login", (req, res, next) => {
    try {
      const parsed = loginSchema.safeParse(req.body);
      const user = parsed.success ? authenticateCredentials(database, parsed.data.email, parsed.data.password) : null;
      if (!user) throw new HttpError(401, "INVALID_CREDENTIALS", "The email or password is invalid");
      const session = createCredentialSession(database, user, config.sessionTtlHours);
      res.setHeader("Cache-Control", "no-store");
      res.json({
        accessToken: session.token, tokenType: "Bearer", expiresAt: session.expiresAt.toISOString(),
        identity: { userId: user.id, email: user.email, organizationId: user.organization_id,
          organizationSlug: user.organization_slug, role: user.role, authType: "SESSION" },
      });
    } catch (error) { next(error); }
  });

  function authenticate(req, _res, next) {
    const sessionToken = parseSessionBearer(req.get("Authorization"));
    const credentialPrincipal = sessionToken ? sessionPrincipal(database, sessionToken) : null;
    if (credentialPrincipal) { req.principal = credentialPrincipal; return next(); }
    const token = parseBearer(req.get("Authorization"));
    const row = token ? database.prepare(`SELECT t.id, t.organization_id, t.label, t.role, t.active, t.expires_at,
      o.slug AS organization_slug FROM api_tokens t JOIN organizations o ON o.id = t.organization_id
      WHERE t.token_digest = ?`).get(tokenDigest(token)) : null;
    if (!row || row.active !== 1 || row.expires_at <= new Date().toISOString()) {
      return next(new HttpError(401, "UNAUTHENTICATED", "A valid active API token is required"));
    }
    req.principal = { authType: "API_TOKEN", tokenId: row.id, organizationId: row.organization_id,
      organizationSlug: row.organization_slug, label: row.label, role: row.role };
    next();
  }
  const roles = (...allowed) => (req, _res, next) => allowed.includes(req.principal.role)
    ? next() : next(new HttpError(403, "FORBIDDEN", "This token role cannot perform that action"));

  app.use("/v1", authenticate);
  app.get("/v1/auth/me", (req, res, next) => {
    if (req.principal.authType !== "SESSION") return next(new HttpError(403, "SESSION_REQUIRED", "Credential session authentication is required"));
    res.json(identityBody(req.principal));
  });
  app.post("/v1/auth/logout", (req, res, next) => {
    if (req.principal.authType !== "SESSION") return next(new HttpError(403, "SESSION_REQUIRED", "Credential session authentication is required"));
    revokeCredentialSession(database, req.principal.sessionId);
    res.status(204).end();
  });
  app.post("/v1/ai/ask", asyncRoute(async (req, res) => {
    if (req.principal.authType !== "SESSION") throw new HttpError(403, "SESSION_REQUIRED", "Credential session authentication is required");
    const parsed = aiRequestSchema.safeParse(req.body);
    if (!parsed.success) throw new HttpError(400, "INVALID_AI_REQUEST", "prompt must contain between 3 and 12000 characters");
    const interactionId = randomUUID();
    const startedAt = new Date();
    database.prepare(`INSERT INTO ai_interactions
      (id, organization_id, user_id, session_id, requested_model, prompt, status, started_at)
      VALUES (?, ?, ?, ?, ?, ?, 'PENDING', ?)`)
      .run(interactionId, req.principal.organizationId, req.principal.userId, req.principal.sessionId,
        config.openRouterModel, parsed.data.prompt, startedAt.toISOString());
    try {
      const provider = await invokeOpenRouter(config, parsed.data.prompt, fetchImplementation);
      database.prepare(`UPDATE ai_interactions SET provider_receipt = ?, provider_model = ?, output_text = ?,
        finish_reason = ?, status = 'SUCCEEDED', completed_at = ?, latency_ms = ? WHERE id = ? AND status = 'PENDING'`)
        .run(provider.receipt, provider.model, provider.output, provider.finishReason,
          new Date().toISOString(), provider.latencyMs, interactionId);
      res.json({ interactionId, providerReceipt: provider.receipt, requestedModel: config.openRouterModel,
        providerModel: provider.model, output: provider.output, finishReason: provider.finishReason });
    } catch (error) {
      const code = error instanceof OpenRouterError ? error.code : "AI_PERSISTENCE_FAILED";
      database.prepare(`UPDATE ai_interactions SET status = 'FAILED', error_code = ?, completed_at = ?, latency_ms = ?
        WHERE id = ? AND status = 'PENDING'`).run(code, new Date().toISOString(), Date.now() - startedAt.getTime(), interactionId);
      if (error instanceof OpenRouterError) throw new HttpError(error.status, error.code, error.message);
      throw error;
    }
  }));
  app.get("/v1/ai/interactions/:interactionId", (req, res, next) => {
    if (req.principal.authType !== "SESSION") return next(new HttpError(403, "SESSION_REQUIRED", "Credential session authentication is required"));
    const row = database.prepare(`SELECT id, provider, provider_receipt, requested_model, provider_model, output_text,
      finish_reason, status, error_code, started_at, completed_at, latency_ms FROM ai_interactions
      WHERE id = ? AND organization_id = ? AND user_id = ?`)
      .get(req.params.interactionId, req.principal.organizationId, req.principal.userId);
    if (!row) return next(new HttpError(404, "AI_INTERACTION_NOT_FOUND", "The AI interaction was not found"));
    res.json({ interactionId: row.id, provider: row.provider, providerReceipt: row.provider_receipt,
      requestedModel: row.requested_model, providerModel: row.provider_model, output: row.output_text,
      finishReason: row.finish_reason, status: row.status, errorCode: row.error_code,
      startedAt: row.started_at, completedAt: row.completed_at, latencyMs: row.latency_ms });
  });
  app.post("/v1/matters", roles("AUTHOR"), (req, res, next) => {
    try { send(res, createMatter({ database, config, principal: req.principal, key: req.get("Idempotency-Key"), body: req.body })); } catch (error) { next(error); }
  });
  app.post("/v1/matters/:matterId/access", roles("AUTHOR", "RECORDS_MANAGER"), (req, res, next) => {
    try { send(res, grantAccess({ database, config, principal: req.principal, key: req.get("Idempotency-Key"), matterId: req.params.matterId, body: req.body })); } catch (error) { next(error); }
  });
  app.post("/v1/matters/:matterId/access/:tokenId/revoke", roles("AUTHOR", "RECORDS_MANAGER"), (req, res, next) => {
    try { send(res, revokeAccess({ database, config, principal: req.principal, key: req.get("Idempotency-Key"), matterId: req.params.matterId, tokenId: req.params.tokenId, body: req.body })); } catch (error) { next(error); }
  });
  app.post("/v1/templates", roles("RECORDS_MANAGER"), (req, res, next) => {
    try { send(res, registerTemplate({ database, config, principal: req.principal, key: req.get("Idempotency-Key"), body: req.body })); } catch (error) { next(error); }
  });
  app.post("/v1/matters/:matterId/documents", roles("AUTHOR"), (req, res, next) => {
    try { send(res, createDocument({ database, config, principal: req.principal, key: req.get("Idempotency-Key"), matterId: req.params.matterId, body: req.body })); } catch (error) { next(error); }
  });
  app.post("/v1/documents/:documentId/versions", roles("AUTHOR"), (req, res, next) => {
    try { send(res, createDocumentVersion({ database, config, principal: req.principal, key: req.get("Idempotency-Key"), documentId: req.params.documentId, body: req.body })); } catch (error) { next(error); }
  });
  app.post("/v1/documents/:documentId/ocr", roles("AUTHOR"), asyncRoute(async (req, res) => send(res,
    await runOcr({ database, config, principal: req.principal, key: req.get("Idempotency-Key"), documentId: req.params.documentId, body: req.body, fetchImplementation }))));
  app.post("/v1/documents/:documentId/submit", roles("AUTHOR"), (req, res, next) => {
    try { send(res, submitDocument({ database, config, principal: req.principal, key: req.get("Idempotency-Key"), documentId: req.params.documentId, body: req.body })); } catch (error) { next(error); }
  });
  app.post("/v1/documents/:documentId/review", roles("LEGAL_REVIEWER"), (req, res, next) => {
    try { send(res, reviewDocument({ database, config, principal: req.principal, key: req.get("Idempotency-Key"), documentId: req.params.documentId, body: req.body })); } catch (error) { next(error); }
  });
  app.post("/v1/documents/:documentId/sign", roles("AUTHOR"), asyncRoute(async (req, res) => send(res,
    await signDocument({ database, config, principal: req.principal, key: req.get("Idempotency-Key"), documentId: req.params.documentId, body: req.body, fetchImplementation }))));
  app.post("/v1/documents/:documentId/file", roles("RECORDS_MANAGER"), asyncRoute(async (req, res) => send(res,
    await fileDocument({ database, config, principal: req.principal, key: req.get("Idempotency-Key"), documentId: req.params.documentId, body: req.body, fetchImplementation }))));
  app.post("/v1/documents/:documentId/hold", roles("RECORDS_MANAGER"), (req, res, next) => {
    try { send(res, setLegalHold({ database, config, principal: req.principal, key: req.get("Idempotency-Key"), documentId: req.params.documentId, body: req.body })); } catch (error) { next(error); }
  });
  app.post("/v1/documents/:documentId/dispose", roles("RECORDS_MANAGER"), (req, res, next) => {
    try { send(res, disposeDocument({ database, config, principal: req.principal, key: req.get("Idempotency-Key"), documentId: req.params.documentId, body: req.body })); } catch (error) { next(error); }
  });
  app.post("/v1/matters/:matterId/export", (req, res, next) => {
    try { send(res, exportMatter({ database, config, principal: req.principal, key: req.get("Idempotency-Key"), matterId: req.params.matterId, body: req.body })); } catch (error) { next(error); }
  });
  app.get("/v1/matters/:matterId", (req, res, next) => {
    try { res.json(getMatterView(database, req.principal, req.params.matterId)); } catch (error) { next(error); }
  });
  app.get("/v1/audit/verify", roles("RECORDS_MANAGER", "AUDITOR"), (req, res) => {
    const result = verifyAudit(database, req.principal.organizationId);
    res.status(result.ok ? 200 : 409).json(result);
  });

  app.use((req, _res, next) => next(new HttpError(404, "NOT_FOUND", `No route for ${req.method} ${req.path}`)));
  app.use((error, req, res, _next) => {
    const constraint = error?.code?.startsWith?.("SQLITE_CONSTRAINT");
    const status = error instanceof HttpError ? error.status : error?.type === "entity.parse.failed" ? 400 : constraint ? 409 : 500;
    if (status >= 500) logger.error?.(JSON.stringify({ event: "request_failed", requestId: req.requestId, message: error.message }));
    res.status(status).json({ error: {
      code: error instanceof HttpError ? error.code : status === 400 ? "INVALID_JSON" : constraint ? "CONSTRAINT_CONFLICT" : "INTERNAL_ERROR",
      message: error instanceof HttpError ? error.message : status === 400 ? "The JSON body is invalid" : constraint ? "The operation conflicts with existing governed data" : "The request failed",
      ...(error instanceof HttpError && error.details ? { details: error.details } : {}), requestId: req.requestId,
    } });
  });
  return app;
}
