PRAGMA foreign_keys = ON;

CREATE TABLE credential_users (
  id TEXT PRIMARY KEY CHECK (length(id) = 36),
  organization_id TEXT NOT NULL REFERENCES organizations(id) ON DELETE RESTRICT,
  actor_token_id TEXT NOT NULL,
  email TEXT NOT NULL COLLATE NOCASE UNIQUE CHECK (email = lower(email) AND length(email) BETWEEN 3 AND 254),
  password_salt TEXT NOT NULL CHECK (length(password_salt) = 32),
  password_hash TEXT NOT NULL CHECK (length(password_hash) = 128),
  role TEXT NOT NULL CHECK (role IN ('AUTHOR', 'LEGAL_REVIEWER', 'RECORDS_MANAGER', 'AUDITOR')),
  active INTEGER NOT NULL DEFAULT 1 CHECK (active IN (0, 1)),
  created_at TEXT NOT NULL,
  updated_at TEXT NOT NULL,
  last_login_at TEXT,
  UNIQUE (id, organization_id),
  FOREIGN KEY (actor_token_id, organization_id) REFERENCES api_tokens(id, organization_id) ON DELETE RESTRICT
) STRICT;

CREATE TABLE credential_sessions (
  id TEXT PRIMARY KEY CHECK (length(id) = 36),
  user_id TEXT NOT NULL REFERENCES credential_users(id) ON DELETE RESTRICT,
  token_digest TEXT NOT NULL UNIQUE CHECK (length(token_digest) = 64),
  created_at TEXT NOT NULL,
  expires_at TEXT NOT NULL,
  last_seen_at TEXT NOT NULL,
  revoked_at TEXT,
  UNIQUE (id, user_id),
  CHECK (revoked_at IS NULL OR revoked_at >= created_at)
) STRICT;

CREATE INDEX credential_sessions_active_idx
  ON credential_sessions (token_digest, expires_at) WHERE revoked_at IS NULL;

CREATE TABLE ai_interactions (
  id TEXT PRIMARY KEY CHECK (length(id) = 36),
  organization_id TEXT NOT NULL REFERENCES organizations(id) ON DELETE RESTRICT,
  user_id TEXT NOT NULL,
  session_id TEXT NOT NULL,
  provider TEXT NOT NULL DEFAULT 'OPENROUTER' CHECK (provider = 'OPENROUTER'),
  provider_receipt TEXT UNIQUE CHECK (provider_receipt IS NULL OR length(provider_receipt) BETWEEN 3 AND 240),
  requested_model TEXT NOT NULL CHECK (length(requested_model) BETWEEN 2 AND 200),
  provider_model TEXT CHECK (provider_model IS NULL OR length(provider_model) BETWEEN 2 AND 200),
  prompt TEXT NOT NULL CHECK (length(prompt) BETWEEN 1 AND 12000),
  output_text TEXT CHECK (output_text IS NULL OR length(output_text) <= 100000),
  finish_reason TEXT CHECK (finish_reason IS NULL OR length(finish_reason) <= 80),
  status TEXT NOT NULL CHECK (status IN ('PENDING', 'SUCCEEDED', 'FAILED')),
  error_code TEXT CHECK (error_code IS NULL OR length(error_code) <= 80),
  started_at TEXT NOT NULL,
  completed_at TEXT,
  latency_ms INTEGER CHECK (latency_ms IS NULL OR latency_ms >= 0),
  FOREIGN KEY (user_id, organization_id) REFERENCES credential_users(id, organization_id) ON DELETE RESTRICT,
  FOREIGN KEY (session_id, user_id) REFERENCES credential_sessions(id, user_id) ON DELETE RESTRICT,
  CHECK (
    (status = 'PENDING' AND provider_receipt IS NULL AND provider_model IS NULL AND output_text IS NULL AND error_code IS NULL AND completed_at IS NULL)
    OR
    (status = 'SUCCEEDED' AND provider_receipt IS NOT NULL AND provider_model IS NOT NULL AND output_text IS NOT NULL AND error_code IS NULL AND completed_at IS NOT NULL)
    OR
    (status = 'FAILED' AND provider_receipt IS NULL AND output_text IS NULL AND error_code IS NOT NULL AND completed_at IS NOT NULL)
  )
) STRICT;

CREATE INDEX ai_interactions_owner_idx
  ON ai_interactions (organization_id, user_id, started_at DESC);

CREATE TRIGGER ai_interactions_terminal_immutable
BEFORE UPDATE ON ai_interactions
WHEN OLD.status <> 'PENDING'
BEGIN
  SELECT RAISE(ABORT, 'terminal AI interactions are immutable');
END;

CREATE TRIGGER ai_interactions_no_delete
BEFORE DELETE ON ai_interactions
BEGIN
  SELECT RAISE(ABORT, 'AI interactions are append-only');
END;
