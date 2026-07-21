import { createHash, createHmac, randomBytes, randomUUID } from "node:crypto";

export function tokenDigest(token) {
  return createHash("sha256").update(token).digest("hex");
}

export function generateApiToken() {
  return `lvm_${randomUUID()}_${randomBytes(32).toString("base64url")}`;
}

export function keyedHash(value, secret) {
  return createHmac("sha256", secret).update(value).digest("hex");
}

export function parseBearer(header) {
  if (typeof header !== "string") return null;
  return /^Bearer (lvm_[A-Za-z0-9_-]{75,140})$/.exec(header)?.[1] ?? null;
}
