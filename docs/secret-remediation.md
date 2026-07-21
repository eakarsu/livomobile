# Historical credential remediation

Current legacy source no longer contains the exposed AWS, payment, Mailgun, Mailchimp, test-service, and vendor-demo key literals. Credential-bearing obsolete PHP handlers were removed, the root credential/deployment note was replaced, and remaining legacy integrations read environment variables or fail outside their unsupported runtime.

Raw Git history still contains Gitleaks findings in exactly two old commits: `ebcdaaf5931400739f11cb7759227cfde2db62a2` and `f8d76c15129bf27903ce0266517c2774e42798f0`. Those immutable commit fingerprints are explicitly baselined so CI can reject findings in every future commit. A CocoaPods checksum has a narrow path exception because it is dependency metadata, not a credential.

Before any launch, the owner must:

1. Revoke and rotate every credential that ever appeared here, including cloud, payment, email, newsletter, and third-party test-service credentials; do not rely on repository removal.
2. Review provider access logs, invoices, payment/email activity, IAM policy changes, and downstream data for misuse from initial exposure through revocation.
3. Decide with repository owners whether to rewrite history and coordinate every clone, fork, tag, release archive, CI cache, and backup. History rewriting without that coordination is incomplete remediation.
4. Record revocation evidence and incident scope outside this repository without copying secret values.

The Gitleaks allowlist is an audit boundary, not a statement that those credentials are safe.
