# Security

This is a learning project, not a security boundary. The application code
in `backend/` and `android/` is known to be insecure in several ways
documented in [`KNOWN-ISSUES.md`](./KNOWN-ISSUES.md) — including plaintext
password storage on the Android client, a hard-coded API URL, a shared
primary key between the access-token and FCM-token tables, and a leaked
Firebase `google-services.json` template (replaced with a placeholder; the
real file is gitignored).

**Do not deploy this application to a real environment.**

If you spot a real issue that is *not* already in `KNOWN-ISSUES.md`, please
open an issue and tag it `security`. There is no private disclosure channel
because this is a learning project, not a maintained product.

## Sanitization

Before publishing, the pre-commit sanitization workflow documented in
[`KNOWN-ISSUES.md` BTL7](./KNOWN-ISSUES.md#btl7--pre-commit-sanitization-workflow-observed-2026-08-27)
must be executed end-to-end: substitution table → cache regeneration
(`view:clear`, `l5-swagger:generate`) → local DB re-seed → rotation of
the original credentials in their dashboards. The runnable form lives at
`scripts/sanitize.sh`. The substitution table is reusable; the rotation
step is not.
