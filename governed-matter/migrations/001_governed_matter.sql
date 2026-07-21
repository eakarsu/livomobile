PRAGMA foreign_keys = ON;

CREATE TABLE organizations (
  id TEXT PRIMARY KEY CHECK (length(id) = 36),
  slug TEXT NOT NULL UNIQUE CHECK (slug = lower(slug) AND length(slug) BETWEEN 2 AND 63),
  name TEXT NOT NULL CHECK (length(name) BETWEEN 2 AND 160),
  created_at TEXT NOT NULL
) STRICT;

CREATE TABLE api_tokens (
  id TEXT PRIMARY KEY CHECK (length(id) = 36),
  organization_id TEXT NOT NULL REFERENCES organizations(id) ON DELETE RESTRICT,
  label TEXT NOT NULL CHECK (length(label) BETWEEN 2 AND 120),
  role TEXT NOT NULL CHECK (role IN ('AUTHOR', 'LEGAL_REVIEWER', 'RECORDS_MANAGER', 'AUDITOR')),
  token_digest TEXT NOT NULL UNIQUE CHECK (length(token_digest) = 64),
  active INTEGER NOT NULL DEFAULT 1 CHECK (active IN (0, 1)),
  expires_at TEXT NOT NULL,
  created_at TEXT NOT NULL,
  UNIQUE (id, organization_id)
) STRICT;

CREATE TABLE matters (
  id TEXT PRIMARY KEY CHECK (length(id) = 36),
  organization_id TEXT NOT NULL REFERENCES organizations(id) ON DELETE RESTRICT,
  matter_key TEXT NOT NULL CHECK (matter_key = lower(matter_key) AND length(matter_key) BETWEEN 2 AND 80),
  title TEXT NOT NULL CHECK (length(title) BETWEEN 2 AND 200),
  jurisdiction TEXT NOT NULL CHECK (length(jurisdiction) BETWEEN 2 AND 24),
  state TEXT NOT NULL DEFAULT 'OPEN' CHECK (state IN ('OPEN', 'CLOSED')),
  version INTEGER NOT NULL DEFAULT 1 CHECK (version > 0),
  opened_by TEXT NOT NULL,
  created_at TEXT NOT NULL,
  updated_at TEXT NOT NULL,
  UNIQUE (organization_id, matter_key),
  UNIQUE (id, organization_id),
  FOREIGN KEY (opened_by, organization_id) REFERENCES api_tokens(id, organization_id) ON DELETE RESTRICT
) STRICT;

CREATE TABLE matter_access (
  id TEXT PRIMARY KEY CHECK (length(id) = 36),
  organization_id TEXT NOT NULL,
  matter_id TEXT NOT NULL,
  token_id TEXT NOT NULL,
  privilege TEXT NOT NULL CHECK (privilege IN ('CONTRIBUTOR', 'REVIEWER', 'RECORDS', 'READ_ONLY')),
  active INTEGER NOT NULL DEFAULT 1 CHECK (active IN (0, 1)),
  granted_by TEXT NOT NULL,
  revoked_by TEXT,
  granted_at TEXT NOT NULL,
  revoked_at TEXT,
  UNIQUE (matter_id, token_id),
  FOREIGN KEY (matter_id, organization_id) REFERENCES matters(id, organization_id) ON DELETE RESTRICT,
  FOREIGN KEY (token_id, organization_id) REFERENCES api_tokens(id, organization_id) ON DELETE RESTRICT,
  FOREIGN KEY (granted_by, organization_id) REFERENCES api_tokens(id, organization_id) ON DELETE RESTRICT,
  FOREIGN KEY (revoked_by, organization_id) REFERENCES api_tokens(id, organization_id) ON DELETE RESTRICT,
  CHECK ((active = 1 AND revoked_at IS NULL AND revoked_by IS NULL) OR (active = 0 AND revoked_at IS NOT NULL AND revoked_by IS NOT NULL))
) STRICT;

CREATE TABLE authoritative_templates (
  id TEXT PRIMARY KEY CHECK (length(id) = 36),
  organization_id TEXT NOT NULL REFERENCES organizations(id) ON DELETE RESTRICT,
  template_key TEXT NOT NULL CHECK (template_key = lower(template_key) AND length(template_key) BETWEEN 2 AND 80),
  title TEXT NOT NULL CHECK (length(title) BETWEEN 2 AND 200),
  jurisdiction TEXT NOT NULL CHECK (length(jurisdiction) BETWEEN 2 AND 24),
  source_uri TEXT NOT NULL CHECK (substr(source_uri, 1, 8) = 'https://' AND length(source_uri) <= 2048),
  content_sha256 TEXT NOT NULL CHECK (length(content_sha256) = 64),
  effective_from TEXT NOT NULL CHECK (length(effective_from) = 10),
  effective_until TEXT CHECK (effective_until IS NULL OR (length(effective_until) = 10 AND effective_until >= effective_from)),
  created_by TEXT NOT NULL,
  created_at TEXT NOT NULL,
  UNIQUE (organization_id, template_key, effective_from),
  UNIQUE (id, organization_id),
  FOREIGN KEY (created_by, organization_id) REFERENCES api_tokens(id, organization_id) ON DELETE RESTRICT
) STRICT;

CREATE TRIGGER authoritative_templates_no_update
BEFORE UPDATE ON authoritative_templates
BEGIN
  SELECT RAISE(ABORT, 'authoritative templates are append-only');
END;

CREATE TRIGGER authoritative_templates_no_delete
BEFORE DELETE ON authoritative_templates
BEGIN
  SELECT RAISE(ABORT, 'authoritative templates are append-only');
END;

CREATE TABLE documents (
  id TEXT PRIMARY KEY CHECK (length(id) = 36),
  organization_id TEXT NOT NULL,
  matter_id TEXT NOT NULL,
  template_id TEXT NOT NULL,
  title TEXT NOT NULL CHECK (length(title) BETWEEN 2 AND 200),
  state TEXT NOT NULL DEFAULT 'DRAFT' CHECK (state IN ('DRAFT', 'IN_REVIEW', 'APPROVED', 'REJECTED', 'SIGNING', 'SIGNATURE_FAILED', 'SIGNED', 'FILING', 'FILING_FAILED', 'FILED', 'DISPOSED')),
  version INTEGER NOT NULL DEFAULT 1 CHECK (version > 0),
  current_version INTEGER NOT NULL DEFAULT 1 CHECK (current_version > 0),
  created_by TEXT NOT NULL,
  reviewer_id TEXT,
  review_note TEXT CHECK (review_note IS NULL OR length(review_note) <= 2000),
  signer_hash TEXT CHECK (signer_hash IS NULL OR length(signer_hash) = 64),
  signature_receipt TEXT CHECK (signature_receipt IS NULL OR length(signature_receipt) <= 200),
  filing_receipt TEXT CHECK (filing_receipt IS NULL OR length(filing_receipt) <= 200),
  repository_uri TEXT CHECK (repository_uri IS NULL OR (substr(repository_uri, 1, 8) = 'https://' AND length(repository_uri) <= 2048)),
  repository_sha256 TEXT CHECK (repository_sha256 IS NULL OR length(repository_sha256) = 64),
  retention_until TEXT CHECK (retention_until IS NULL OR length(retention_until) = 10),
  legal_hold INTEGER NOT NULL DEFAULT 0 CHECK (legal_hold IN (0, 1)),
  hold_reason TEXT CHECK (hold_reason IS NULL OR length(hold_reason) BETWEEN 2 AND 500),
  provider_operation_hash TEXT CHECK (provider_operation_hash IS NULL OR length(provider_operation_hash) = 64),
  processing_started_at TEXT,
  disposed_at TEXT,
  created_at TEXT NOT NULL,
  updated_at TEXT NOT NULL,
  UNIQUE (id, organization_id),
  FOREIGN KEY (matter_id, organization_id) REFERENCES matters(id, organization_id) ON DELETE RESTRICT,
  FOREIGN KEY (template_id, organization_id) REFERENCES authoritative_templates(id, organization_id) ON DELETE RESTRICT,
  FOREIGN KEY (created_by, organization_id) REFERENCES api_tokens(id, organization_id) ON DELETE RESTRICT,
  FOREIGN KEY (reviewer_id, organization_id) REFERENCES api_tokens(id, organization_id) ON DELETE RESTRICT,
  CHECK (reviewer_id IS NULL OR reviewer_id <> created_by),
  CHECK ((legal_hold = 0 AND hold_reason IS NULL) OR (legal_hold = 1 AND hold_reason IS NOT NULL))
) STRICT;

CREATE INDEX documents_queue_idx ON documents (organization_id, matter_id, state, updated_at DESC);

CREATE TABLE document_versions (
  id TEXT PRIMARY KEY CHECK (length(id) = 36),
  organization_id TEXT NOT NULL,
  document_id TEXT NOT NULL,
  version_number INTEGER NOT NULL CHECK (version_number > 0),
  source_uri TEXT NOT NULL CHECK (substr(source_uri, 1, 8) = 'https://' AND length(source_uri) <= 2048),
  source_sha256 TEXT NOT NULL CHECK (length(source_sha256) = 64),
  redacted_uri TEXT CHECK (redacted_uri IS NULL OR (substr(redacted_uri, 1, 8) = 'https://' AND length(redacted_uri) <= 2048)),
  redacted_sha256 TEXT CHECK (redacted_sha256 IS NULL OR length(redacted_sha256) = 64),
  redaction_reason TEXT CHECK (redaction_reason IS NULL OR length(redaction_reason) BETWEEN 2 AND 500),
  ocr_state TEXT NOT NULL DEFAULT 'PENDING' CHECK (ocr_state IN ('PENDING', 'PROCESSING', 'VERIFIED', 'FAILED')),
  ocr_receipt TEXT CHECK (ocr_receipt IS NULL OR length(ocr_receipt) <= 200),
  ocr_error_code TEXT CHECK (ocr_error_code IS NULL OR length(ocr_error_code) <= 80),
  ocr_operation_hash TEXT CHECK (ocr_operation_hash IS NULL OR length(ocr_operation_hash) = 64),
  ocr_started_at TEXT,
  created_by TEXT NOT NULL,
  created_at TEXT NOT NULL,
  UNIQUE (document_id, version_number),
  UNIQUE (id, organization_id),
  FOREIGN KEY (document_id, organization_id) REFERENCES documents(id, organization_id) ON DELETE RESTRICT,
  FOREIGN KEY (created_by, organization_id) REFERENCES api_tokens(id, organization_id) ON DELETE RESTRICT,
  CHECK ((redacted_uri IS NULL AND redacted_sha256 IS NULL AND redaction_reason IS NULL) OR (redacted_uri IS NOT NULL AND redacted_sha256 IS NOT NULL AND redaction_reason IS NOT NULL))
) STRICT;

CREATE TRIGGER document_versions_content_immutable
BEFORE UPDATE ON document_versions
WHEN OLD.document_id <> NEW.document_id OR OLD.version_number <> NEW.version_number
  OR OLD.source_uri <> NEW.source_uri OR OLD.source_sha256 <> NEW.source_sha256
  OR COALESCE(OLD.redacted_uri, '') <> COALESCE(NEW.redacted_uri, '')
  OR COALESCE(OLD.redacted_sha256, '') <> COALESCE(NEW.redacted_sha256, '')
  OR COALESCE(OLD.redaction_reason, '') <> COALESCE(NEW.redaction_reason, '')
  OR OLD.created_by <> NEW.created_by OR OLD.created_at <> NEW.created_at
BEGIN
  SELECT RAISE(ABORT, 'document version content is immutable');
END;

CREATE TRIGGER document_versions_no_delete
BEFORE DELETE ON document_versions
BEGIN
  SELECT RAISE(ABORT, 'document versions are append-only');
END;

CREATE TABLE provider_attempts (
  id TEXT PRIMARY KEY CHECK (length(id) = 36),
  organization_id TEXT NOT NULL,
  document_id TEXT NOT NULL,
  action TEXT NOT NULL CHECK (action IN ('OCR', 'SIGN', 'FILE')),
  attempt_number INTEGER NOT NULL CHECK (attempt_number BETWEEN 1 AND 10),
  outcome TEXT NOT NULL CHECK (outcome IN ('SUCCEEDED', 'FAILED')),
  error_code TEXT CHECK (error_code IS NULL OR length(error_code) <= 80),
  provider_status INTEGER,
  created_at TEXT NOT NULL,
  FOREIGN KEY (document_id, organization_id) REFERENCES documents(id, organization_id) ON DELETE RESTRICT
) STRICT;

CREATE TABLE idempotency_operations (
  id TEXT PRIMARY KEY CHECK (length(id) = 36),
  organization_id TEXT NOT NULL REFERENCES organizations(id) ON DELETE RESTRICT,
  actor_token_id TEXT NOT NULL,
  key_hash TEXT NOT NULL CHECK (length(key_hash) = 64),
  request_hash TEXT NOT NULL CHECK (length(request_hash) = 64),
  status_code INTEGER NOT NULL CHECK (status_code BETWEEN 200 AND 599),
  response_json TEXT NOT NULL,
  created_at TEXT NOT NULL,
  UNIQUE (organization_id, actor_token_id, key_hash),
  FOREIGN KEY (actor_token_id, organization_id) REFERENCES api_tokens(id, organization_id) ON DELETE RESTRICT
) STRICT;

CREATE TABLE audit_events (
  sequence INTEGER PRIMARY KEY AUTOINCREMENT,
  id TEXT NOT NULL UNIQUE CHECK (length(id) = 36),
  organization_id TEXT NOT NULL REFERENCES organizations(id) ON DELETE RESTRICT,
  matter_id TEXT,
  document_id TEXT,
  document_version_id TEXT,
  actor_token_id TEXT,
  event_type TEXT NOT NULL CHECK (event_type IN ('MATTER_CREATED', 'ACCESS_GRANTED', 'ACCESS_REVOKED', 'TEMPLATE_REGISTERED', 'DOCUMENT_CREATED', 'DOCUMENT_VERSION_CREATED', 'OCR_STARTED', 'OCR_VERIFIED', 'OCR_FAILED', 'DOCUMENT_SUBMITTED', 'DOCUMENT_APPROVED', 'DOCUMENT_REJECTED', 'SIGNING_STARTED', 'SIGNATURE_FAILED', 'DOCUMENT_SIGNED', 'FILING_STARTED', 'FILING_FAILED', 'DOCUMENT_FILED', 'LEGAL_HOLD_APPLIED', 'LEGAL_HOLD_RELEASED', 'DOCUMENT_DISPOSED', 'MATTER_EXPORTED')),
  from_state TEXT,
  to_state TEXT,
  payload_json TEXT NOT NULL,
  previous_hash TEXT CHECK (previous_hash IS NULL OR length(previous_hash) = 64),
  row_hash TEXT NOT NULL CHECK (length(row_hash) = 64),
  created_at TEXT NOT NULL,
  FOREIGN KEY (matter_id, organization_id) REFERENCES matters(id, organization_id) ON DELETE RESTRICT,
  FOREIGN KEY (document_id, organization_id) REFERENCES documents(id, organization_id) ON DELETE RESTRICT,
  FOREIGN KEY (document_version_id, organization_id) REFERENCES document_versions(id, organization_id) ON DELETE RESTRICT,
  FOREIGN KEY (actor_token_id, organization_id) REFERENCES api_tokens(id, organization_id) ON DELETE RESTRICT
) STRICT;

CREATE INDEX audit_events_chain_idx ON audit_events (organization_id, sequence);

CREATE TRIGGER audit_events_no_update BEFORE UPDATE ON audit_events
BEGIN SELECT RAISE(ABORT, 'audit_events is append-only'); END;

CREATE TRIGGER audit_events_no_delete BEFORE DELETE ON audit_events
BEGIN SELECT RAISE(ABORT, 'audit_events is append-only'); END;
