import { randomUUID } from "node:crypto";
import express from "express";
import helmet from "helmet";
import { verifyAudit } from "./audit.mjs";
import { HttpError } from "./errors.mjs";
import { verifyMigrations } from "./migrations.mjs";
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

export function createApp({ database, config, fetchImplementation = fetch, logger = console }) {
  const app = express();
  app.disable("x-powered-by");
  app.use((req, res, next) => {
    req.requestId = /^[A-Za-z0-9._:-]{8,100}$/.test(req.get("X-Request-Id") ?? "") ? req.get("X-Request-Id") : randomUUID();
    res.setHeader("X-Request-Id", req.requestId);
    const started = Date.now();
    res.on("finish", () => logger.info?.(JSON.stringify({ event: "http_request", requestId: req.requestId,
      method: req.method, path: req.path, status: res.statusCode, durationMs: Date.now() - started,
      actorTokenId: req.principal?.tokenId ?? null })));
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

  function authenticate(req, _res, next) {
    const token = parseBearer(req.get("Authorization"));
    const row = token ? database.prepare(`SELECT id, organization_id, label, role, active, expires_at
      FROM api_tokens WHERE token_digest = ?`).get(tokenDigest(token)) : null;
    if (!row || row.active !== 1 || row.expires_at <= new Date().toISOString()) {
      return next(new HttpError(401, "UNAUTHENTICATED", "A valid active API token is required"));
    }
    req.principal = { tokenId: row.id, organizationId: row.organization_id, label: row.label, role: row.role };
    next();
  }
  const roles = (...allowed) => (req, _res, next) => allowed.includes(req.principal.role)
    ? next() : next(new HttpError(403, "FORBIDDEN", "This token role cannot perform that action"));

  app.use("/v1", authenticate);
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
