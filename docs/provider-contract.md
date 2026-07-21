# Document provider contract

The governed service sends one exact JSON object to `DOCUMENT_PROVIDER_URL`. The request has `Content-Type: application/json`, an opaque tenant/actor-scoped HMAC derivative of the workflow idempotency key, and `X-Livo-Document-Signature-SHA256`, which is lowercase hex HMAC-SHA256 over the exact request bytes using `DOCUMENT_PROVIDER_SECRET`.

Actions are:

- `OCR`: tenant, matter, document and version identifiers plus the immutable HTTPS source URI and SHA-256.
- `SIGN`: the redacted artifact when present (otherwise source), its SHA-256, and signer email.
- `FILE`: the signed artifact evidence, signature receipt, and retention date.

A success response is strict JSON:

```json
{
  "receiptId": "provider-unique-receipt",
  "artifactUri": "https://records.example/object/123",
  "artifactSha256": "optional-lowercase-sha256"
}
```

`receiptId` is always required. `FILE` also requires an HTTPS `artifactUri` and `artifactSha256`; undeclared fields or malformed evidence fail closed. The provider must persist idempotency results for longer than the client's maximum recovery window and return the original result for an identical key and body. A key reused with a different body must be rejected.

The service retries only within the configured bound and records every attempt without response bodies or personal data. TLS certificate validation is mandatory. Production provider URLs must use HTTPS. Contract onboarding must test signature verification, duplicate delivery, timeout, malformed JSON, 4xx/5xx, signer rejection, and receipt lookup.
