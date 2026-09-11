# LEARNING — what I learned the hard way

---

## Lesson 1 — Sanitize sensitive data *before* you commit, not after

The workflow for putting a project on a public host is "substitution table → regenerate caches → re-seed → rotate." Skipping any of the four leaves a leak somewhere:

1. **Build a substitution table as the single source of truth** (one TSV / heredoc / JSON, with longer / more specific patterns *first* so search-and-replace doesn't double-eat). Don't sprinkle `sed` calls across the codebase.
2. **Apply it across every source type** — `.env` templates, JSON, XML, Java, Blade, Markdown — but **skip `composer.lock` / `package-lock.json`** (they contain upstream package authors' public emails; modifying them is a no-op anyway). Skip `.env` if it's gitignored; you keep local dev working.
3. **Caches leak the originals** even after the source is fixed. `php artisan view:clear` removes compiled Blade views that snapshot the unsanitized source; `php artisan l5-swagger:generate` regenerates the OpenAPI spec. The local MySQL DB holds PII rows from the *previous* seed; `migrate:fresh --seed` re-runs the now-clean seeders.
4. **Use format-valid placeholders for SDK-validated secrets.** Firebase's API-key regex is `A[\w-]{38}` (39 chars, verified against firebase-installations 18.0.0 by disassembling the AAR); a "your-key-here" placeholder crashes the app at boot. Match the *shape* of the original value, not just the meaning of "placeholder."
5. **Rotate the real credentials in their dashboards.** Repo changes cover the *future*; the originals are already burned if they were ever on a public host. The substitution table is reusable; the rotation step is not.

See [`KNOWN-ISSUES.md` BTL7](./KNOWN-ISSUES.md#btl7--pre-commit-sanitization-workflow-observed-2026-08-27) for the full substitution table, file-by-file coverage, and verification commands.

---

## Lesson 2 — Format-valid placeholders for SDK-validated secrets

Firebase, Pusher, Stripe, Google Maps — every SDK with a public-facing credential has a *shape check*, not just a "does this look like a key" string check. The Android Firebase key regex is `A[\w-]{38}` — exactly 39 characters, starts with `A`, the rest are word characters or hyphens. The Windows-side `firebase-windows-auth-key-validator` checks something similar. A "your-key-here" placeholder fails *at boot*, because Firebase auto-initializes via a `ContentProvider` *before* `Application.onCreate()`. **Two takeaways:** (a) find the regex the SDK checks against (often by disassembling the AAR with `javap` and reading the resulting class); (b) build a dummy that matches the shape — `AIzaSyDUMMY0000000000000000000000000000` passes the regex and keeps the app bootable, so trainees can clone and run without Firebase being a real working backend. Match the *shape* of the original, not the *meaning* of "placeholder."

```bash
# The SDK's regex (firebase-installations 18.0.0, by inspection):
# \AA[\w-]{38}\z
# The verified dummy passes:
$ printf '%s' AIzaSyDUMMY0000000000000000000000000000 | wc -c
39
```

See [`KNOWN-ISSUES.md` BTL6](./KNOWN-ISSUES.md#btl6--a-placeholder-google-servicesjson-with-a-non-format-api-key-crashes-the-app-at-boot-resolved-2026-08-27) for the full bug-level writeup and crash chain.

---

## Lesson 3 — Don't read async results synchronously

A common Android antipattern: kick off an async fetch (`Futures.addCallback` with a Guava executor, a coroutine `launch`, an RxJava `subscribe`), then use the captured variable *outside* the callback later in the same function — `chain.proceed(newRequestWithAccessToken(request, accessToken))`. The async chain hasn't executed yet, so the variable is still `null`. Every first request after login fails authentication. **Two ways it fails in this codebase** — `AccessTokenInterceptor` and `Authenticator` both reach for the access token from the Room DB asynchronously and then use it in the *same* synchronous expression. **Fix either way:** block on the future from a background thread (`executor.submit(() -> dao.selectToken().get())` for OkHttp's interceptor thread), or hold a synchronized token cache that's *only* invalidated by the auth callback. Never trust that a callback's variable is available to a later statement in the same function.

```java
// WRONG (captures accessToken = null because callback hasn't run yet):
Futures.addCallback(database.AccessTokenDao().selectToken(),
    new FutureCallback<String>() {
        @Override public void onSuccess(String r) { accessToken = r; }
    }, new AppExecutors().diskIO());
chain.proceed(newRequestWithAccessToken(chain.request(), accessToken)); // null!
```

See [`KNOWN-ISSUES.md` High — Android](./KNOWN-ISSUES.md#high--android) for the two manifestations and their bug-level writeups.

---

## Lesson 4 — `Route::patch('/nurses', ...)` is a literal path, not a "family" match

Laravel's `Route::patch('/nurses', ...)` matches `PATCH /nurses` *exactly* — no `{id}`. To get `PATCH /nurses/{id}`, you must write `Route::patch('/nurses/{id}', ...)`. The OpenAPI annotations in this project describe PATCH-with-id correctly (`nurse_id` is in the path), but the route table omitted `{id}`, so the spec and implementation disagreed silently. **Two takeaways:** (a) REST verbs without an `{id}` aren't useful — almost every PATCH/PUT/DELETE expects to identify a resource; (b) if you generate API docs from PHP attributes (`l5-swagger`), generate them *from* `routes/api.php` or validate the spec against the route table; hand-written specs drift. **Rule of thumb:** the OpenAPI spec is the contract; the route table is part of the implementation. They must match.

```php
// WRONG (matches PATCH /nurses, returns "Caring list"):
Route::patch('/nurses', [NurseController::class, 'update']);

// RIGHT (matches PATCH /nurses/{id}):
Route::patch('/nurses/{id}', [NurseController::class, 'update']);
```

See [`KNOWN-ISSUES.md` B18](./KNOWN-ISSUES.md#b18--patch-routes-registered-without-id-placeholder-patch-on-a-specific-resource-returns-405-observed-2026-08-26) for the matching bug pair.

---

## Lesson 5 — `FormRequest::validated()` returns only fields the rules know about

`FormRequest::validated()` returns *only* the keys present in the `$rules` array — no more, no less. The controller's `Model::create($request->validated())` then `INSERT`s with that subset; any DB-level NOT NULL on a field the rules omitted becomes a runtime error at insert. **Two safeguards worth considering in any Laravel project:**

1. Use `prepareForValidation()` to *add* defaults for missing-but-required fields before validation runs, so the rules array can validate them. e.g. `role`, `status`, `created_by` — fields you always want to set.
2. Generate the `StoreRequest` *from* the migration (`php artisan make:model <Name> --all` then add custom rules on top). Starting from "what the DB needs" prevents the rules-vs-schema gap that the under-specified rules create.

The bug looks like a runtime Laravel error; the fix is a *schema-vs-rules* discipline lesson.

See [`KNOWN-ISSUES.md` B13](./KNOWN-ISSUES.md#b13--nursestorerequest-validation-rules-under-specify-the-model-missing-fields-hit-the-db-as-null-observed-2026-08-26) for the full bug-level writeup.

---

## Lesson 6 — `DB::statement()` and `DB::raw()` are intentionally non-portable

The `DB::statement()` / `DB::select()` / `DB::raw()` family in Laravel is *intentionally* non-portable. It exists so you can write driver-specific SQL when you need to. The *default* — when you want portable code — is the query builder, the schema builder, or Eloquent. **Rule of thumb:** if you find yourself typing `SET ...` in a Laravel file (e.g., `SET FOREIGN_KEY_CHECKS = 0`), stop and look for `Schema::disableForeignKeyConstraints()` (or `EnableForeignKeyConstraints()`). The framework already has the abstraction; you usually just need to find it. The four seeder files in this project all use raw `DB::statement('SET FOREIGN_KEY_CHECKS=0')` for MySQL — broken on SQLite, broken in CI, fixed by switching to the `Schema::` facade.

```php
// WRONG (raw MySQL DDL, breaks on SQLite / Postgres / test DB):
DB::statement('SET FOREIGN_KEY_CHECKS=0');
foreach ($rows as $row) { DB::table('...')->insert($row); }
DB::statement('SET FOREIGN_KEY_CHECKS=1');

// RIGHT (portable — Laravel translates per driver):
Schema::disableForeignKeyConstraints();
foreach ($rows as $row) { DB::table('...')->insert($row); }
Schema::enableForeignKeyConstraints();
```

See [`KNOWN-ISSUES.md` B11](./KNOWN-ISSUES.md#b11--all-four-seeders-use-raw-set-foreign_key_checks-mysql-sql-breaking-on-sqlite-observed-2026-08-26) for the full bug-level writeup.

---

## Lesson 7 — Separate request and response DTOs; never serialize hashes on the wire

A DTO / `Entity` that doubles as both an *API request body* and a *response model* will have every field serialized by Retrofit / Jackson / Gson by default. That means a `passwordHash` field in an entity that's used as both will (a) be hashed on the server when the client sends it, (b) come back to the client on read, and (c) be hashed a second time if the user edits and posts the response body back to the server. The Android `NurseEntityRemote` in this project includes `password` for both directions; the backend then needs `Hash::make()` on every receive, which masks the issue at first glance. **Rule of thumb:** separate the request DTO from the response DTO. The request model accepts plaintext (or hashed, server-side) credentials on the dedicated fields; the response model `@JsonIgnore`s password-like fields entirely. Retrofit/Moshi/Codable all support per-class `@Body` versus per-field `@JsonIgnore`; use them.

```kotlin
// WRONG (one class used for both, password flows both ways):
@Parcelize data class NurseEntityRemote(
    val id: Long,
    val name: String,
    val email: String,
    val password: String?,           // sent, then echoed back, then re-hashed!
    /* ... */
) : Parcelable

// RIGHT (two classes; the response one excludes the password):
@Parcelize data class NurseRequest(
    val name: String, val email: String, val password: String,
) : Parcelable
@Parcelize data class NurseResponse(
    val id: Long, val name: String, val email: String,
) : Parcelable
```

See `KNOWN-ISSUES.md`'s **High — Android** entries (the `NurseEntityRemote`-with-password and the Android `PATCH /nurses`-no-id bugs are co-listed there) for the matching bug-level writeups.

---

## Lesson 8 — Scaffolded auth code rots when you rename the user model

This project was scaffolded with Breeze/Splade and a `User` model, then the auth model was renamed to `Nurse`. The scaffolded `ProfileTest`, `ProfileUpdateRequest`, and `User::factory()` references were never updated. The test suite was entirely red before any assertion ran — a classic case of boilerplate outliving its context. **Two takeaways:** (a) after renaming the auth model, run `grep -rn "App\\Models\\User"` and update every hit in tests, requests, and factories; (b) the nullsafe operator (`$this->nurse?->id`) can silently mask a typo as a `null` with the *wrong semantics*. If `?->` appears on a property you don't define, treat it as suspicious. A broken test suite that fails before asserting is the best kind of red — it names the exact class that doesn't exist.

See [`KNOWN-ISSUES.md` B19](./KNOWN-ISSUES.md#b19--api-nurse-is-registered-with-authsanctum-middleware-but-the-project-uses-jwt-returns-302-redirect-instead-of-401-json-observed-2026-08-26) and [`B20`](./KNOWN-ISSUES.md#b20--breezes-profiletest-targeted-the-deleted-user-model-and-the-profile-update-request-it-guards-has-a-broken-unique-ignore-resolved-2026-08-27) for the matching bug-level writeups.

---

## Lesson 9 — API response wrappers need a real envelope, or no envelope at all

`ApiResponseTrait` is a method called `apiResponse($data, $status, $message)` that returns `response($data, $status)` — the envelope array is commented out. Meanwhile `NurseStoreRequest::failedValidation()` returns HTTP 200 with `{success: false}` because `response()->json($data)` defaults to 200. **Two sides of the same coin:** if you promise an envelope, implement it; if you don't want an envelope, delete the trait and call `response()` directly. For validation errors, Laravel's convention is 422 — not 200 with a custom body. The same principle applies to error handlers, exception renderers, and any place where the HTTP status code is the *contract* and the JSON body is the *explanation*.

See [`KNOWN-ISSUES.md` B5](./KNOWN-ISSUES.md#b5--apiresponsetrait-is-an-empty-wrapper-around-responsedata-status) and [`B15`](./KNOWN-ISSUES.md#b15--nursestorerequestfailedvalidation-returns-http-200-with-success-false-body-instead-of-422-observed-2026-08-26) for the matching bug-level writeups.

---

## Lesson 10 — Keep the OpenAPI spec and the route table in sync

The OpenAPI annotations describe `PATCH /api/nurses/{id}`, but `routes/api.php` registers `Route::patch('/nurses', 'updateNurse')` (no `{id}`). The spec says one thing; the implementation does another; the client (Android app, Swagger UI) hits a 405. **Rule of thumb:** the OpenAPI spec is the contract; the route table is part of the implementation. They must match. If you generate API docs from PHP attributes, generate them *from* `routes/api.php` or add a CI check that diffs the generated spec against the annotated controllers. Hand-written specs drift; generated specs stay coupled to the router.

See [`KNOWN-ISSUES.md` B18](./KNOWN-ISSUES.md#b18--patch-routes-registered-without-id-placeholder-patch-on-a-specific-resource-returns-405-observed-2026-08-26) for the matching bug-level writeup.

---

## Lesson 11 — Verify binary assets after copying a project

Two launcher PNGs in the copied Android tree had their PNG magic bytes corrupted (`24 1A 9C 92` instead of `89 50 4E 47`). They looked plausible in a file browser and had reasonable sizes, but AAPT rejected them and blocked every build. The `file` command is the cheapest integrity check for binary assets — `file app/src/main/res/mipmap-*/*.png` finishes in seconds and tells you whether a PNG is actually a PNG. The same lesson applies to any copied project: zips, rars, USB transfers, and cloud sync can all corrupt a single byte in a binary without changing the file size enough to notice.

See [`KNOWN-ISSUES.md` BTL5](./KNOWN-ISSUES.md#btl5--two-launcher-pngs-in-the-copied-tree-were-corrupted-failing-aapt-resource-compilation-resolved-2026-08-27) for the matching bug-level writeup.

---

## Lesson 12 — Add CI/CD before the first production deploy

This project shipped without CI/CD: every deploy was a manual `wasmer deploy` run with no regression guard. The fix is a two-job GitHub Actions pipeline:

- **`test`** — spins up MySQL 8.0, installs PHP 8.2 + extensions, runs `composer install`, `migrate`, `db:seed`, `php artisan test`
- **`deploy`** — runs after tests pass on push to `main`; installs Wasmer CLI, logs in via `${{ secrets.WASMER_TOKEN }}`, runs `wasmer deploy`

**Lesson:** CI/CD for a serverless-edge Laravel app is two jobs: test (full stack with real MySQL) then deploy (Wasmer Edge). The `needs: test` gate ensures broken code never reaches production. This lesson applies to any deployment target — VPS, cloud run, edge — the CI job should always test *before* deploy.

See [`KNOWN-ISSUES.md` BTL8](./KNOWN-ISSUES.md#btl8--ci-cd-pipeline-missing-github-actions--wasmer-edge) for the matching bug-level writeup.

---

## Lesson 13 — Serverless edge DB vars come from secrets, never from .env

Wasmer Edge originally used TiDB Cloud (MySQL-compatible serverless), not local MySQL. If DB vars are hard-coded in `.env` or `wasmer.toml`, they leak into the deployed image. The fix:

- `backend/app.yaml` — set `name: nurses-task-system`, `owner: srckod`
- `backend/wasmer.toml` — new Wasmer package config: PHP 8.3, fs mount, command
- `backend/.env.example` — documents the DB vars shape (placeholders only)
- DB vars injected via `wasmer app secrets create KEY VALUE`

**Lesson:** Serverless edge deployments use cloud databases, not local MySQL. DB connection details must come from the platform, never from committed files. The `.env.example` documents the *shape* of the vars; the real values live in the platform's secret store. This is the same principle as BTL7 (pre-commit sanitization) but applied to deployment: the platform's secret mechanism is the single source of truth for production secrets.

**Update (2026-09-09):** The project later switched to Wasmer's built-in MySQL (`capabilities.database` in `app.yaml`): Wasmer auto-provisions the DB and injects `DB_HOST`/`DB_PORT`/`DB_NAME`/`DB_USERNAME`/`DB_PASSWORD` at runtime — zero manual DB secrets. Migrations run via the `migrate-on-deploy` post-deployment job. The principle stands: production credentials come from the platform, and the only manually-managed secrets (`APP_KEY`, `JWT_SECRET`, …) go up once via `wasmer app secrets create --from-file=.env`.

See [`KNOWN-ISSUES.md` BTL9](./KNOWN-ISSUES.md#btl9--wasmer-edge-db-vars-tidb-cloud-must-be-set-as-secrets-never-in-env) for the matching bug-level writeup.

---

## Lesson 14 — "Injected at runtime" breaks the moment you define the same name yourself

`capabilities.database` injects `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`
and `DB_PASSWORD` into the app. But app-level env vars and secrets are merged
*over* capability-injected values. Because the local `.env` contained
self-referencing placeholders:

```dotenv
DB_HOST="${DB_HOST}"
DB_DATABASE="${DB_NAME}"
```

…uploading it with `wasmer app secrets create --from-file=.env` replaced the
working credentials with empty strings. `env('DB_HOST')` became `''`, PDO fell
back to a unix socket, and the sandbox refused it:

```
SQLSTATE[HY000] [2002] Operation not permitted
```

The error names a connection problem, not a configuration problem, so the first
instinct — "is the database down?" — is wrong.

Two compounding mistakes made this expensive to find:

1. **Wrong secret set.** `--from-file=.env` uploads *everything*, including
   variables the platform already provides. Upload only what the platform
   cannot know (`APP_KEY`, `JWT_SECRET`, mail credentials).
2. **A failing pre-deployment job blocks its own fix.** The job could not
   connect, so the deploy aborted, so no code or config change ever reached
   production. When a gate depends on the thing you are trying to fix, plan an
   escape hatch (fix the env vars in the dashboard first, or temporarily switch
   the job to `post-deployment`).

**What to do instead:** keep self-referencing `${VAR}` placeholders out of any
file that can be uploaded; print which variables the runtime actually sees
(masked) before guessing; and keep seeding out of the deploy path entirely, so
a routine deploy can never truncate production data.

> **Check the assumption, not just the mechanism.** On this app the compiled
> workload never actually contained a `database` capability
> (`wasmer app deployment list <app> -f json` →
> `…json_config.spec.workload.capabilities` lists only `locality`,
> `network_gateway`, `wasi`, `web_gateway`). The managed instance exists and is
> attached, but the injected variables were absent — the `DB_*` app secrets
> were doing the work. "Injected at runtime" is a claim to verify with one
> command, not to assume from a config file.

See [`KNOWN-ISSUES.md` BTL10](./KNOWN-ISSUES.md#btl10--app-level-wasmer-env-vars-shadow-the-db-credentials-injected-by-capabilitiesdatabase) for the matching bug-level writeup.

---

## Lesson 15 — A release gate that can block its own fix is a trap; verify the effect, not the exit code

The migration job that was supposed to run `php artisan migrate` on every deploy
was declared like this:

```yaml
jobs:
  - name: migrate-on-deploy
    trigger: pre-deployment
    action:
      execute:
        command: php          # not a command in the package
        cli_args: ["artisan", "migrate", "--force"]
```

On a WASI runtime there is no `php` on `PATH`; runnable binaries are the
`[[command]]` blocks in `wasmer.toml`. The job did nothing, and nothing
anywhere reported that it did nothing. Production ran with an unmigrated
database while every deploy looked healthy.

Then the second half of the trap: the job was `pre-deployment`. Once it started
failing loudly (`WASI exit code 1`, `[2002] Operation not permitted`), it
aborted the deploy that carried the fix for the failure. The corrected
`app.yaml` could never ship. An escape hatch had to be opened by hand.

And the third: an intermediate version had CI call a token-protected
`POST /api/deploy/setup` endpoint that caught exceptions and returned HTTP 200
with an error body. `curl` exited 0, the step was green, the database was still
empty.

**What to do instead:**

1. **Use a trigger that cannot block the release.** A gate that runs before
the new version exists can refuse to publish its own fix. Whatever the
mechanism, keep migration work off the critical path of the release itself —
and confirm the mechanism actually runs (see Lesson 16: on this app the
`post-deployment` job was dropped by the compiler too, so migrations now run
from CI against the database).
2. **Reference a command that exists.** Declare it in `wasmer.toml` and use
`command: artisan`, then assert the *recorded* config after deploy:

```bash
wasmer app deployment list nurses-task-system -f json \
  | jq -r '.[0].app_version.user_yaml_config'
```

3. **Verify the effect, not the exit code.** Any claim about production should
end in an observable effect — a query that only works on a migrated schema, a
login that only works with seeded rows. Exit codes are not evidence.
4. **Prefer deleting the hook over hardening it.** `DeployController`, its
route, `DEPLOY_TOKEN`, the custom `app:deploy-setup` command and the matching
test were all removable once migration became a plain CI step — less surface,
no shared secret, no curl exit-code ambiguity.

See [`KNOWN-ISSUES.md` BTL11](./KNOWN-ISSUES.md#btl11--wasmer-pre-deployment-jobs-are-a-deadlock-trap-use-post-deployment-and-verify-the-result) for the full diagnosis and the CLI commands that expose what actually deployed.

## Lesson 16 — The config you wrote is not the config that runs

After the job command was fixed (`command: artisan`) and the trigger moved to
`post-deployment`, the job still never ran. The YAML was accepted, stored and
handed back by the API:

```bash
wasmer app deployment list nurses-task-system -f json \
  | jq '.[0].app_version.user_yaml_config.jobs'
# -> [{ "name": "migrate-on-deploy", "trigger": "post-deployment", ... }]
```

But the compiled spec that the runtime receives had no jobs at all:

```bash
wasmer app deployment list nurses-task-system -f json \
  | jq '.[0].app_version.json_config.spec.jobs'
# -> null
```

The CLI compiles `App.v0` YAML into an internal `App.v1` spec, and this version
of the compiler drops the `jobs:` section. There is no `--dry-run` for
`wasmer deploy`, so nothing warns you: the deploy is green, the database is
empty, and the config you can read back is not the config that ran.

**What to do instead:**

1. **Treat input config and compiled config as two different artifacts.**
Wherever a platform compiles your config, read back the compiled form before
trusting a feature.
2. **Do not build a release path on a feature you have not seen run.** The
migration job had three separate silent-failure modes across two lessons
(missing command, blocking trigger, dropped section). The working solution
removed the dependency entirely: CI connects to the database directly and runs
`php artisan migrate --force && php artisan db:seed --force` after the deploy.
3. **Make the observable effect the acceptance test.** A migrated schema is a
fact you can query; a green deploy step is not. When the migration step ran
from CI, the database went from two tables to the full schema — that is the
evidence that the fix worked.

See [`KNOWN-ISSUES.md` BTL12](./KNOWN-ISSUES.md#btl12--wasmer-cli-silently-drops-the-appyaml-jobs-section-when-compiling-the-app-spec) for the full diagnosis.

## Lesson 17 — "The primary key is already there" is not what the server sees

Wasmer's managed MySQL enables `sql_require_primary_key`, and the
`password_reset_tokens` migration failed against it even though it declared
`$table->primary('email')`:

```
SQLSTATE[HY000]: General error: 3750 Unable to create or change a table
without a primary key ... (SQL: create table `password_reset_tokens` ...)
```

Laravel 10.7's `MySqlGrammar` does not inline primary keys into
`create table`; it emits the `create table` and then a separate
`alter table … add primary key`. The policy check happens on the first
statement, so the second never runs. The schema definition was fine — the
*compiled SQL* was not.

**What to do instead:**

1. **Read the SQL in the error, not the migration source.** Laravel prints the
   exact statement it sent; that is the artifact to reason about.
2. **Inline the constraint where the policy requires it.** The fix uses
   driver-aware raw DDL (`primary key (email)` inside `create table`) for
   MySQL and keeps `Schema::create()` for SQLite, so the test database is
   unaffected.
3. **Reproduce against the real managed database.** It is reachable from
   outside Wasmer on its non-standard port, so `php artisan migrate --force`
   with the Wasmer credentials reproduces the failure locally instead of
   costing a deploy cycle. (`SET SESSION sql_require_primary_key = 0` also
   works, but it only patches the session and hides the portability problem.)

See [`KNOWN-ISSUES.md` BTL13](./KNOWN-ISSUES.md#btl13--wasmer-managed-mysql-rejects-password_reset_tokens-because-laravel-adds-the-primary-key-in-a-second-statement-resolved-2026-09-09) for the full diagnosis.

## Lesson 18 — A green deploy and a valid package can still mean a dead app

After the deploy pipeline was finally green and the migrations succeeded, the
app still returned Wasmer's own error page for *every* route:

```html
<title>500 Internal Server Error</title> ... An unknown error occurred
```

No Laravel log, nothing in `wasmer app logs`. The cause was in
`wasmer.toml`: a second `[[command]]` (`artisan`, added for the dropped
migration job) made the package entrypoint ambiguous.

```
$ wasmer run .
Unable to determine the package's entrypoint
Please specify one of the following entrypoints:
  - run
  - artisan
```

A single `[[command]]` resolves automatically. Two commands and no explicit
entrypoint means the workload never starts — and the platform reports that as a
generic 500, not as a configuration error.

**What to do instead:**

1. **Keep one command per deployable package.** Extra commands are a
   convenience for local tooling and a liability in a manifest that also has to
   boot.
2. **Smoke-test the way the platform starts it.** `wasmer run .` with no extra
   flags is the honest test; `wasmer run . --command-name run` passes even when
   the deployed app is dead, which is exactly how this regression slipped
   through.
3. **When a 500 has no application log, suspect the entrypoint.** Check
   `x-edge-request-outcome` on the response: `workload_failure` means the WASI
   worker crashed before Laravel ran.

See [`KNOWN-ISSUES.md` BTL14](./KNOWN-ISSUES.md#btl14--a-wasmer-package-may-declare-only-one-command-a-second-one-makes-the-entrypoint-ambiguous-and-every-request-returns-500-resolved-2026-09-09) for the full diagnosis.

## Lesson 19 — A hard-coded `localhost` redirect silently fails everywhere else

**Severity:** High on a deployed app; invisible in local dev.

`/api-docs` returned a `302` redirect to `http://localhost:8000/api/documentation`
— a URL hard-coded in `DashboardController::redirectToApiDocs()`. On a developer's
machine that happens to line up with the local server, so the bug is invisible.
On Wasmer Edge the browser is asked to navigate to `localhost:8000`, which does
not exist in the user's browser, so the Swagger UI never loads even though
`/api/documentation` itself serves a perfect HTTP 200.

The l5-swagger package registers `/api/documentation` as a route on its own;
the only thing the wrapper controller did was redirect to it — and it did so by
spelling out a host.

**What to do instead:**

1. **Redirect by path, never by absolute URL.** `Redirect::to('/api/documentation')`
   preserves the scheme+host+port of the incoming request. It is correct in
   dev, staging, and production with zero per-environment configuration.
2. **Treat "works on my machine" as a smell, not proof.** Anything that names a
   host (`localhost`, `127.0.0.1`, a specific port) is a latent production bug.
   The CI test job runs on `127.0.0.1` with a fresh MySQL — that same property
   hides every host-baking bug from the test suite.
3. **Prefer named routes.** The package exposes `route('l5-swagger.default.api')`;
   routing through the named route means a package version bump that moves the
   docs path does not silently break the shortcut.

See [`KNOWN-ISSUES.md` B21](./KNOWN-ISSUES.md#b21--dashboardcontrollerredirecttoapidocshard-codes-localhost8000-breaking-api-docs-on-any-deployed-host-resolved-2026-09-09) for the diagnosis and the one-line fix.

## Lesson 20 — What l5-swagger serves comes from gitignored paths; a source-only deploy loses it

**Severity:** High on a deployed app; invisible in local dev.

After fixing the `/api-docs` redirect (Lesson 19) the docs page loaded on
production but rendered as broken unstyled HTML: every asset it references —
`/docs/asset/swagger-ui.css`, `swagger-ui-bundle.js`, the favicons — returned
404, and `/docs/api-docs.json` did too.

The root cause has two layers, and the second one only showed up after fixing
the first:

*Layer 1 — gitignored source paths.* The l5-swagger package reads two
gitignored locations by default:

1. `vendor/swagger-api/swagger-ui/dist/` — the UI assets. `vendor/` is
   gitignored, and Wasmer (like most PaaS builds) ships source-only, so the
   directory simply does not exist in the deployed package.
2. `storage/api-docs/api-docs.json` — the generated OpenAPI spec. Also
   gitignored, also absent.

*Layer 2 — the PHP controllers 404 in the WASI sandbox even when the files
exist.* After re-pointing the config at committed files
(`public/vendor/swagger-ui/` + `public/api-docs/`) the docs page still 404'd on
`/docs/asset/*` and `/docs/api-docs.json`. Reproduced 1:1 in a local
`wasmer run --net` against the exact deployed package — so the package was not
stale and the bug is in the code/config itself. `SwaggerAssetController@index`
and `SwaggerController@docs` both resolve their files through
`realpath()`-based helpers that return false inside the WASI runtime, aborting
with 404 even though the files physically exist in the package. Direct static
access to those same files (`/vendor/swagger-ui/favicon-32x32.png`, or the
spec at its public path) returned 200 — the static/web-gateway layer serves a
URL by mapping it to a file under `public/` without ever invoking PHP.

The Swagger routes themselves were registered — `SwaggerAssetController@index`
and `SwaggerController@docs` exist — but they were the wrong serving mechanism
for this runtime. This is the same class of bug as Lesson 11 (verify binary
assets after copying a project): a framework feature that silently depends on a
directory that version control excludes, and beyond that, a runtime whose
file-resolution semantics differ from your host PHP.

**What to do instead:**

1. **Point framework paths at `public/` when the deploy is source-only.**
   `public/` is the one directory that must be committed. Here that means
   `swagger_ui_assets_path => 'public/docs/asset/'`,
   `'docs' => public_path('docs')`, and copying the six assets the
   published view actually references from `vendor/swagger-api/swagger-ui/dist/`
   into `public/docs/asset/`. The page references only a handful of
   files; do not copy the whole dist tree with `.map` files.
2. **Mirror the emitted URLs under `public/` and let the static layer serve
   them.** The published view emits `/docs/asset/<file>` and
   `/docs/api-docs.json` (the `docs` route group). Put the files at exactly
   those URL-identical paths under `public/` (`public/docs/asset/...`,
   `public/docs/api-docs.json`) so the static layer handles them. Do not route
   them through the Swagger PHP controllers — `realpath()`-based resolution
   silently 404s in WASI. *Serving the same content via controller vs static
   file is not interchangeable: verify the mechanism, not just that the file is
   committed.*
3. **Turn off absolute asset URLs in production.** l5-swagger's
   `use_absolute_path` defaulted to `true`, so the HTML embedded absolute
   `https://<host>/docs/asset/...` URLs — which on a deployed host baked in the
   wrong host. `env('L5_SWAGGER_USE_ABSOLUTE_PATH', false)` makes the generated
   HTML use relative `/docs/asset/...` paths that work on any host.
4. **Audit every package's default path the same way.** Any config key whose
   default is `vendor/...`, `storage/...`, or `bootstrap/cache/...` is a
   candidate for the same failure. Ask: *which directory does this resolve to,
   is that directory in the deployed package, and does the runtime serve it via
   controller or static file?* — not *does it work locally?*
5. **Regenerate into the tracked location.** With `paths.docs` moved to
   `public/docs/`, `php artisan l5-swagger:generate` writes a file that is
   actually committed, and `L5_SWAGGER_GENERATE_ALWAYS=true` keeps it current
   on every request.

See [`KNOWN-ISSUES.md` B22](./KNOWN-ISSUES.md#b22--swagger-ui-assets-and-api-docsjson-404-on-a-deployed-source-only-host-because-l5-swagger-reads-them-from-gitignored-directories--and-the-swagger-php-controllers-silently-404-in-the-wasi-sandbox-resolved-2026-09-10) for the diagnosis and the config diff.
