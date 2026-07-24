# Operations runbook

## Release and startup

1. Review the migration checksum and application diff.
2. Back up the database and verify the backup before deployment.
3. Run `npm run db:migrate`; run it again to prove it is repeatable.
4. Run `npm run db:verify`, then start the service. `MIGRATE_ON_START=true` is suitable for the single-instance Compose topology; coordinated multi-instance deployment must run migration as a separate one-shot job.
5. Require `/health/live` and `/health/ready` to pass. Readiness requires verified migrations and at least one unexpired active token.

The root launcher sources its ignored `.env`, runs the checksum-verified migrations, and idempotently verifies or creates the configured credential user before binding the assigned port. After startup, verify anonymous denial, credential login, `/v1/auth/me`, and logout/revocation. When AI is enabled, record the returned interaction ID and verify its provider receipt and output through `/v1/ai/interactions/:interactionId`; never log bearer sessions or provider credentials.

The root Compose file runs as UID 10001, drops all capabilities, uses a read-only root filesystem and named database volume, binds only to loopback by default, and caps local logs. Terminate TLS at the ingress and restrict provider egress by DNS and network policy.

## Monitoring and response

Alert on 5xx rates, repeated `PROVIDER_*` failures, readiness failure, unexpected token or access changes, audit-chain failure, disk/WAL growth, and backup age. Logs include request ID, route, status, duration, and actor token ID; they omit bodies and credentials.

Normal OCR, signer, or filing failures become `FAILED` workflow states. Confirm the provider's receipt/status, correct the external condition, reload the current optimistic version, and retry with a new application idempotency key. If a process dies while a row remains `PROCESSING`, `SIGNING`, or `FILING`, quarantine the matter and reconcile the stored operation hash with provider logs before changing state; do not blindly repeat a potentially completed signature or filing. This version deliberately requires operator reconciliation for that ambiguous crash window.

On suspected credential exposure, disable affected token rows, revoke matter grants where applicable, rotate provider and idempotency HMAC secrets, inspect audit events, and preserve evidence. Changing the idempotency secret invalidates comparison with in-flight operation hashes, so drain or reconcile workflows first.

## Backup and restore

`npm run backup -- /secure/path/matters.sqlite` performs SQLite's online backup after migration, integrity, foreign-key, trigger, and audit-chain checks; it verifies the resulting file again. `npm run restore:verify -- BACKUP EMPTY_TARGET` restores to a new path and repeats the checks. Encrypt backups outside this process, restrict them to records operations, copy them to an independent failure domain, and test restoration on schedule.

Never overwrite the live database during a drill. For recovery, stop writers, preserve the failed database and WAL, restore to an empty path, verify, atomically repoint the service, and retain the recovery record.
