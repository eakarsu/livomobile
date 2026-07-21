#!/usr/bin/env bash

set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
app_dir="$script_dir/governed-matter"
cd "$app_dir"

[ -d node_modules ] || { printf 'Dependencies are missing; run npm ci in governed-matter.\n' >&2; exit 1; }
export PORT="${PORT:-3030}"
export DATABASE_PATH="${DATABASE_PATH:-${DB_PATH:-}}"
export IDEMPOTENCY_SECRET="${IDEMPOTENCY_SECRET:-${JWT_SECRET:-}}"
export DOCUMENT_PROVIDER_URL="${DOCUMENT_PROVIDER_URL:-http://127.0.0.1:9/v1/operations}"
export DOCUMENT_PROVIDER_SECRET="${DOCUMENT_PROVIDER_SECRET:-${JWT_REFRESH_SECRET:-}}"
export MIGRATE_ON_START="${MIGRATE_ON_START:-false}"
: "${DATABASE_PATH:?DATABASE_PATH or DB_PATH is required}"
: "${IDEMPOTENCY_SECRET:?IDEMPOTENCY_SECRET or JWT_SECRET is required}"
: "${DOCUMENT_PROVIDER_SECRET:?DOCUMENT_PROVIDER_SECRET or JWT_REFRESH_SECRET is required}"

exec npm start
