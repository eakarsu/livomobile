# Governed matter workflow

This directory is the supported LivoMobile runtime. It implements one bounded legal-document journey; the historical Java, Cordova, Android, iOS, Cassandra, and Tomcat trees at the repository root are archival and are not copied into this image.

## What is enforced

- Tenant-bound, expiring bearer tokens store only SHA-256 digests and reload active status on every request.
- Credential users store salted scrypt password hashes. Login issues expiring, revocable bearer sessions whose digests and last-use state are durable in SQLite.
- Roles (`AUTHOR`, `LEGAL_REVIEWER`, `RECORDS_MANAGER`, `AUDITOR`) are combined with explicit matter grants. Revocation takes effect on the next request.
- Authoritative templates have HTTPS provenance, a SHA-256 digest, jurisdiction, and effective dates. Submission revalidates those controls.
- Source and optional redacted artifacts are immutable, content-addressed document versions. OCR evidence must verify the current version before review.
- An independent human legal reviewer must approve the current version. Authors cannot review their own work.
- OCR, signature, and filing calls use an HMAC-authenticated provider contract, bounded timeout/retry, and provider idempotency keys. Failures become durable workflow states.
- Filing records a provider receipt, HTTPS repository location, and retention date. Legal hold prevents disposition.
- Every mutation requires an idempotency key and workflow mutations use optimistic versions.
- Audit events are append-only and form a per-tenant SHA-256 chain. Evidence exports are canonical JSON with a manifest digest.
- Authenticated OpenRouter requests use the canonical API base. Each accepted request persists its owning session, provider receipt, requested/provider model, output, and latency in an append-only terminal record.

This is workflow software, not legal advice. Template selection and approval remain human legal responsibilities. Provider receipts prove what the configured provider reported; they are not independently a qualified-electronic-signature or court-filing certification.

## Local verification

```sh
npm ci
npm run check
npm audit --audit-level=high
```

Create a disposable database and a one-time bootstrap token:

```sh
export NODE_ENV=development
export DATABASE_PATH="$PWD/matters.sqlite"
export IDEMPOTENCY_SECRET="$(openssl rand -hex 32)"
export DOCUMENT_PROVIDER_URL="https://provider.example/v1/operations"
export DOCUMENT_PROVIDER_SECRET="$(openssl rand -hex 32)"
export TOKEN_ORGANIZATION_SLUG=example-tenant
export TOKEN_ORGANIZATION_NAME="Example Tenant"
export TOKEN_LABEL=initial-author
export TOKEN_ROLE=AUTHOR
export TOKEN_OUTPUT_PATH="$PWD/.tokens/author"
mkdir -p .tokens && chmod 700 .tokens
npm run db:migrate
npm run token:create
npm start
```

Provision separate legal-reviewer, records-manager, and auditor tokens with the same organization slug; the author must grant them the corresponding matter privilege before use. Never put token values in source, command history, logs, URLs, or support tickets.

For credential login, set `ADMIN_EMAIL`, a random 12+ character `ADMIN_PASSWORD`, organization metadata, and `ADMIN_ROLE`, then run the root `./start.sh`. The launcher sources the ignored root `.env`, applies verified migrations, and idempotently bootstraps the credential user without logging the password. `POST /v1/auth/login` returns a bearer session, `GET /v1/auth/me` returns its tenant/user identity, and `POST /v1/auth/logout` revokes it.

The protected `POST /v1/ai/ask` route requires a credential session; legacy API tokens and anonymous callers are rejected. Configure `OPENROUTER_API_KEY`, `OPENROUTER_MODEL`, and the fixed `OPENROUTER_BASE_URL=https://openrouter.ai/api/v1`. `GET /v1/ai/interactions/:interactionId` reads the caller-owned durable receipt and output after completion. AI output assists workflow operations and is not legal advice.

## API journey

All mutations use JSON, `Authorization: Bearer …`, and an `Idempotency-Key` of 16–128 safe characters.

1. `POST /v1/matters` creates a tenant matter and grants its author contributor access.
2. `POST /v1/matters/:matterId/access` grants reviewer, records, or read-only access.
3. `POST /v1/templates` registers an authoritative template (records manager).
4. `POST /v1/matters/:matterId/documents` creates immutable version 1, including a complete redaction evidence triple when applicable.
5. `POST /v1/documents/:documentId/ocr` verifies extractability with the provider.
6. `POST /v1/documents/:documentId/submit` rechecks OCR, jurisdiction, and effective dates.
7. `POST /v1/documents/:documentId/review` records independent counsel approval or rejection.
8. `POST /v1/documents/:documentId/sign` collects signature evidence; a failed signer/provider attempt can be retried with the returned current version and a new idempotency key.
9. `POST /v1/documents/:documentId/file` records provider-backed filing/storage and retention.
10. `POST /v1/documents/:documentId/hold` applies or releases legal hold; `dispose` is allowed only after retention and without hold.
11. `POST /v1/matters/:matterId/export` returns digest-only artifact evidence and a canonical manifest digest.

See [`../docs/operations.md`](../docs/operations.md), [`../docs/security.md`](../docs/security.md), and [`../docs/provider-contract.md`](../docs/provider-contract.md).
