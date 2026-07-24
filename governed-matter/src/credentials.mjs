import { randomBytes, randomUUID, scryptSync, timingSafeEqual } from "node:crypto";
import { z } from "zod";
import { tokenDigest } from "./security.mjs";

const emailSchema = z.string().trim().toLowerCase().email().max(254);
const passwordSchema = z.string().min(12).max(256);
const roleSchema = z.enum(["AUTHOR", "LEGAL_REVIEWER", "RECORDS_MANAGER", "AUDITOR"]);

function passwordMaterial(password, salt) {
  return scryptSync(password, Buffer.from(salt, "hex"), 64, { N: 16_384, r: 8, p: 1, maxmem: 64 * 1024 * 1024 });
}

export function passwordRecord(password) {
  const parsed = passwordSchema.parse(password);
  const salt = randomBytes(16).toString("hex");
  return { salt, hash: passwordMaterial(parsed, salt).toString("hex") };
}

export function verifyPassword(password, salt, expectedHash) {
  const parsed = passwordSchema.safeParse(password);
  if (!parsed.success || !/^[a-f0-9]{32}$/.test(salt) || !/^[a-f0-9]{128}$/.test(expectedHash)) return false;
  const actual = passwordMaterial(parsed.data, salt);
  return timingSafeEqual(actual, Buffer.from(expectedHash, "hex"));
}

export function ensureCredentialUser({ database, email, password, organizationSlug, organizationName, role = "AUTHOR", now = new Date() }) {
  const normalizedEmail = emailSchema.parse(email);
  const normalizedRole = roleSchema.parse(role);
  const slug = z.string().regex(/^[a-z0-9][a-z0-9-]{1,62}$/).parse(organizationSlug);
  const name = z.string().trim().min(2).max(160).parse(organizationName);
  const existing = database.prepare("SELECT * FROM credential_users WHERE email = ?").get(normalizedEmail);
  if (existing) {
    if (existing.role !== normalizedRole) throw new Error("Credential role does not match the existing user");
    const existingOrganization = database.prepare("SELECT slug FROM organizations WHERE id = ?").get(existing.organization_id);
    if (!existingOrganization || existingOrganization.slug !== slug) throw new Error("Credential organization does not match the existing user");
    if (!verifyPassword(password, existing.password_salt, existing.password_hash)) {
      const replacement = passwordRecord(password);
      database.prepare("UPDATE credential_users SET password_salt = ?, password_hash = ?, updated_at = ? WHERE id = ?")
        .run(replacement.salt, replacement.hash, now.toISOString(), existing.id);
      database.prepare("UPDATE credential_sessions SET revoked_at = ? WHERE user_id = ? AND revoked_at IS NULL")
        .run(now.toISOString(), existing.id);
    }
    return { id: existing.id, organizationId: existing.organization_id, actorTokenId: existing.actor_token_id, created: false };
  }

  let organization = database.prepare("SELECT id, slug FROM organizations WHERE slug = ?").get(slug);
  if (!organization) {
    organization = { id: randomUUID(), slug };
    database.prepare("INSERT INTO organizations (id, slug, name, created_at) VALUES (?, ?, ?, ?)")
      .run(organization.id, slug, name, now.toISOString());
  }
  const actorTokenId = randomUUID();
  database.prepare(`INSERT INTO api_tokens
    (id, organization_id, label, role, token_digest, expires_at, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)`)
    .run(actorTokenId, organization.id, `credential-${normalizedEmail}`.slice(0, 120), normalizedRole,
      randomBytes(32).toString("hex"), new Date(now.getTime() + 3650 * 86_400_000).toISOString(), now.toISOString());
  const userId = randomUUID();
  const record = passwordRecord(password);
  database.prepare(`INSERT INTO credential_users
    (id, organization_id, actor_token_id, email, password_salt, password_hash, role, created_at, updated_at)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`)
    .run(userId, organization.id, actorTokenId, normalizedEmail, record.salt, record.hash, normalizedRole,
      now.toISOString(), now.toISOString());
  return { id: userId, organizationId: organization.id, actorTokenId, created: true };
}

export function authenticateCredentials(database, email, password, now = new Date()) {
  const parsedEmail = emailSchema.safeParse(email);
  const parsedPassword = passwordSchema.safeParse(password);
  const row = parsedEmail.success ? database.prepare(`SELECT u.*, o.slug AS organization_slug
    FROM credential_users u JOIN organizations o ON o.id = u.organization_id WHERE u.email = ?`).get(parsedEmail.data) : null;
  if (!parsedPassword.success || !row || row.active !== 1 || !verifyPassword(parsedPassword.data, row.password_salt, row.password_hash)) return null;
  database.prepare("UPDATE credential_users SET last_login_at = ?, updated_at = ? WHERE id = ?")
    .run(now.toISOString(), now.toISOString(), row.id);
  return row;
}

export function createCredentialSession(database, user, ttlHours, now = new Date()) {
  const token = `lvs_${randomUUID()}_${randomBytes(32).toString("base64url")}`;
  const session = { id: randomUUID(), token, expiresAt: new Date(now.getTime() + ttlHours * 3_600_000) };
  database.prepare(`INSERT INTO credential_sessions
    (id, user_id, token_digest, created_at, expires_at, last_seen_at) VALUES (?, ?, ?, ?, ?, ?)`)
    .run(session.id, user.id, tokenDigest(token), now.toISOString(), session.expiresAt.toISOString(), now.toISOString());
  return session;
}

export function parseSessionBearer(header) {
  if (typeof header !== "string") return null;
  return /^Bearer (lvs_[A-Za-z0-9_-]{75,140})$/.exec(header)?.[1] ?? null;
}

export function sessionPrincipal(database, token, now = new Date()) {
  const row = database.prepare(`SELECT s.id AS session_id, s.expires_at, u.id AS user_id, u.organization_id,
    u.actor_token_id, u.email, u.role, u.active, o.slug AS organization_slug
    FROM credential_sessions s
    JOIN credential_users u ON u.id = s.user_id
    JOIN organizations o ON o.id = u.organization_id
    WHERE s.token_digest = ? AND s.revoked_at IS NULL`).get(tokenDigest(token));
  if (!row || row.active !== 1 || row.expires_at <= now.toISOString()) return null;
  database.prepare("UPDATE credential_sessions SET last_seen_at = ? WHERE id = ?").run(now.toISOString(), row.session_id);
  return {
    authType: "SESSION", sessionId: row.session_id, userId: row.user_id, tokenId: row.actor_token_id,
    organizationId: row.organization_id, organizationSlug: row.organization_slug,
    email: row.email, label: row.email, role: row.role,
  };
}

export function revokeCredentialSession(database, sessionId, now = new Date()) {
  return database.prepare("UPDATE credential_sessions SET revoked_at = ? WHERE id = ? AND revoked_at IS NULL")
    .run(now.toISOString(), sessionId).changes === 1;
}
