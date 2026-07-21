# Security model

## Boundaries and authorization

The supported container contains only `governed-matter/`. The legacy monorepo is not an authentication, storage, or deployment dependency. Every governed row carries a tenant identifier and composite foreign keys prevent cross-tenant actor or artifact references.

Coarse token roles never substitute for matter access. A valid token must also have an active matter grant for reads and workflow actions. Role-to-grant compatibility prevents an author from being made a legal reviewer or records manager. The matter opener cannot be revoked, avoiding an orphaned record. Disable a token for organization-wide revocation; revoke a grant for matter-only revocation.

## Sensitive and privileged data

Artifact bytes are not stored in SQLite. Only HTTPS locations, content digests, provider receipts, and workflow evidence are persisted. Exports deliberately omit source and repository URIs and never include legal-hold reasons or signer email. Signer email is sent only to the configured provider; the database stores a keyed digest. Audit payloads store review-note digests and hold-reason digests, not privileged text.

Redacted evidence must be supplied as a complete URI/digest/reason triple. Signatures use the redacted artifact when it exists. Access to the external object store must independently enforce tenant, matter, and purpose constraints; an HTTPS URL alone is not authorization.

## Integrity and abuse controls

Document content fields and audit events have database triggers preventing modification or deletion. Optimistic versions prevent stale transitions, and idempotency records prevent duplicate or conflicting mutations. The audit verifier recomputes every per-tenant chain link before export and during backup verification.

HTTP bodies are limited, security headers are enabled, CORS origins require exact matches, errors do not expose SQL/provider details, and structured request logs omit authorization values and bodies. Put the service behind an authenticated rate-limiting gateway; this bounded service intentionally does not infer client IP through untrusted forwarding headers.

## Required secret handling

Generate token values and the two HMAC secrets independently. Store them in a secret manager, rotate on staff/provider changes, and never reuse them between environments. API tokens are shown once and stored mode 0600 by the bootstrap command; only SHA-256 token digests are retained. See `secret-remediation.md` for the compromised historical repository credentials.
