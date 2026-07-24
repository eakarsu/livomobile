#!/usr/bin/env bash

set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
app_dir="$script_dir/governed-matter"

if [[ -f "$script_dir/.env" ]]; then
  set -a
  # shellcheck disable=SC1091
  source "$script_dir/.env"
  set +a
fi

cd "$app_dir"

[ -d node_modules ] || { printf 'Dependencies are missing; run npm ci in governed-matter.\n' >&2; exit 1; }
export PORT="${PORT:-3030}"
if [[ ! "$PORT" =~ ^[0-9]+$ ]] || (( PORT < 1024 || PORT > 65535 )); then
  printf 'PORT must be a numeric assigned port between 1024 and 65535.\n' >&2
  exit 1
fi
if command -v lsof >/dev/null 2>&1 && lsof -tiTCP:"$PORT" -sTCP:LISTEN >/dev/null 2>&1; then
  printf 'Assigned port %s is already occupied.\n' "$PORT" >&2
  exit 1
fi
export DATABASE_PATH="${DATABASE_PATH:-${DB_PATH:-}}"
export IDEMPOTENCY_SECRET="${IDEMPOTENCY_SECRET:-${JWT_SECRET:-}}"
export DOCUMENT_PROVIDER_URL="${DOCUMENT_PROVIDER_URL:-http://127.0.0.1:9/v1/operations}"
export DOCUMENT_PROVIDER_SECRET="${DOCUMENT_PROVIDER_SECRET:-${JWT_REFRESH_SECRET:-}}"
export MIGRATE_ON_START="false"
: "${DATABASE_PATH:?DATABASE_PATH or DB_PATH is required}"
: "${IDEMPOTENCY_SECRET:?IDEMPOTENCY_SECRET or JWT_SECRET is required}"
: "${DOCUMENT_PROVIDER_SECRET:?DOCUMENT_PROVIDER_SECRET or JWT_REFRESH_SECRET is required}"

npm run db:migrate
if [[ -n "${ADMIN_EMAIL:-}" || -n "${ADMIN_PASSWORD:-}" ]]; then
  : "${ADMIN_EMAIL:?ADMIN_EMAIL is required when credential bootstrap is enabled}"
  : "${ADMIN_PASSWORD:?ADMIN_PASSWORD is required when credential bootstrap is enabled}"
  npm run admin:bootstrap
fi

exec npm start
