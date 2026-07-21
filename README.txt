LivoMobile archive and governed matter workflow
==============================================

This repository contains a historical 2012-2020 mobile-platform monorepo and a
new, isolated supported workflow in `governed-matter/`.

The historical Java, Cordova, Android, iOS, and Cassandra applications are
retained for reference only. They are not part of the supported runtime,
container image, or release gate. Their obsolete deployment instructions and
embedded credentials have been retired. Any credential that ever appeared in
this Git repository must be considered compromised, revoked, and rotated; see
`docs/secret-remediation.md`.

Supported workflow
------------------

`governed-matter/` implements one bounded legal-document journey: create a
matter, grant least-privilege access, select an authoritative jurisdictional
template, create immutable document versions, redact sensitive text, obtain an
independent human legal review, collect a provider-backed signature, file the
signed document, apply retention or legal hold, and export verifiable evidence.

Start with `governed-matter/README.md` and `_COMPLETENESS_REVIEW.md`.
