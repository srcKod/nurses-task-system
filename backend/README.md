# Backend — Laravel 10 API

[![PHP: 8.2](https://img.shields.io/badge/PHP-8.2-777BB4?logo=php&logoColor=white)](./composer.json)
[![Laravel: 10](https://img.shields.io/badge/Laravel-10-FF2D20?logo=laravel&logoColor=white)](./composer.json)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](../LICENSE)
[![Status: educational](https://img.shields.io/badge/Status-educational-orange)](#what-this-is-not)

The Laravel 10 backend for the **e-Nurses Jobs** mobile-app tutorial project. Provides the REST API the Android client consumes (in [`../android/`](../android/)) and the OpenAPI documentation the front-end team works from.

> **This is a teaching repo, not a production codebase.** It is *intentionally* left with a catalog of known bugs and anti-patterns. See [`../KNOWN-ISSUES.md`](../KNOWN-ISSUES.md) and [`../LEARNING.md`](../LEARNING.md) for the full writeup.

## Table of contents

* [Stack](#stack)
* [What this is not](#what-this-is-not)
* [Quickstart](#quickstart)
* [Setup in detail](#setup-in-detail)
* [Routes](#routes)
* [Tests](#tests)
* [Known issues (in this folder)](#known-issues-in-this-folder)
* [See also](#see-also)

## What this is not

* **Not production software.** Insecure in several documented ways. **Do not deploy.**
* **Not a maintained library.** No support commitment.

---

## Stack

| Layer | Library / version |
|-------|-------------------|
| Framework | Laravel 10 (PHP `^8.1`) |
| Auth | [`tymon/jwt-auth`](https://github.com/tymondesigns/jwt-auth) (JWT bearer tokens) |
| Admin auth scaffolding | [`laravel/sanctum`](https://laravel.com/docs/10.x/sanctum) (kept for Breeze's `ProfileTest`; **see B19** — the project uses JWT, not Sanctum) |
| API docs | [`darkaonline/l5-swagger`](https://github.com/DarkaOnLine/L5-Swagger) (OpenAPI annotations → `/api/documentation`) |
| Query builder | [`spatie/laravel-query-builder`](https://github.com/spatie/laravel-query-builder) + [`kirschbaum-development/eloquent-power-joins`](https://github.com/kirschbaum-development/eloquent-power-joins) |
| Push notifications | [`kreait/laravel-firebase`](https://github.com/kreait/laravel-firebase) + [`laravel-notification-channels/fcm`](https://github.com/laravel-notification-channels/fcm) |
| Mail | [`symfony/mailgun-mailer`](https://github.com/symfony/mailer) (driver for Mailgun) |
| Front-end helpers | [`protonemedia/laravel-splade-breeze`](https://github.com/protonemedia/laravel-splade-breeze) (auth scaffolding) |

The `composer.json` declares `^8.1` but individual packages declare tighter ranges (`~8.1.0 || ~8.2.0`). See **BTL1 / BTL3 / BTL4** below.

---

## Quickstart

The shortest path from a clean checkout to a working dev server (MySQL variant):

```bash
cd backend
composer install --ignore-platform-req=php
cp .env.example .env
php artisan key:generate
php artisan jwt:secret --force
php artisan migrate:fresh --seed
php artisan serve   # http://127.0.0.1:8000
```

That's it for a smoke test. For Android-client testing on a physical device, edit `android/app/src/main/java/.../RemoteDataSource.java` to point at your LAN IP + `:8000` first.

## Setup in detail

### 1. Prerequisites

* PHP **8.2** is the comfortable target (BTL3). PHP 8.1 works; PHP 8.4 requires `composer install --ignore-platform-req=php` and you will see deprecation warnings at runtime (BTL4).
* Composer 2.x.
* A database: MySQL / MariaDB (preferred for parity with the original dev environment), or SQLite (works for a quick start — but **see B11**).
* PHP extensions: `mbstring`, `pdo_mysql` (or `pdo_sqlite`), `openssl`, `tokenizer`, `xml`, `ctype`, `json`, `bcmath`, `gd`, `fileinfo`, `sodium` (for JWT).
* On Windows + XAMPP: enable `sodium` in `php.ini` (`extension=sodium`) — required for `Hash::make` and JWT signing.

### 2. Install dependencies

```bash
cd backend
composer install --ignore-platform-req=php        # see BTL3
cp .env.example .env                              # then edit secrets — see below
php artisan key:generate
php artisan jwt:secret
```

> **Why `--ignore-platform-req=php`?** Several transitive packages declare `~8.1.0 || ~8.2.0` and won't resolve on PHP 8.4. For a *learning project* pinned to Laravel 10's current patch line, the right answer is to relax the platform check at install time and accept the deprecation warnings at runtime (BTL3 / BTL4). For a *production* project, upgrade to Laravel 11 / 12 and bump those packages.

### 3. Configure `.env`

`.env` is gitignored. The committed `.env.example` has format-valid placeholders so the app boots without real credentials. Required variables:

```dotenv
APP_NAME=eNursesJobs
APP_ENV=local
APP_DEBUG=false                                   # NEVER true on a network — see B14
APP_URL=http://localhost:8000

DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=enurse
DB_USERNAME=root
DB_PASSWORD=

JWT_SECRET=                                       # set by `php artisan jwt:secret`
JWT_TTL=60

MAIL_MAILER=mailgun
MAILGUN_DOMAIN=your-mailgun-domain.example
MAILGUN_SECRET=key-your-mailgun-placeholder-here

FIREBASE_CREDENTIALS=/absolute/path/to/firebase-adminsdk-DUMMY.json

LOG_CHANNEL=stack
LOG_LEVEL=debug
```

The placeholder Mailgun secret matches the Mailgun domain-key shape; the placeholder Firebase credentials file (if you keep one) should be a *valid-format* dummy JSON. See [`../KNOWN-ISSUES.md` BTL6](../KNOWN-ISSUES.md#btl6--a-placeholder-google-servicesjson-with-a-non-format-api-key-crashes-the-app-at-boot-resolved-2026-08-27) for the regex/format detail on Firebase-side keys.

### 4. Migrate and seed

```bash
php artisan migrate --seed
```

**On SQLite** (dev / CI): the four seeders (`NurseSeeder`, `CaringSeeder`, `CaringtypeSeeder`, `PatientSeeder`) use raw `DB::statement('SET FOREIGN_KEY_CHECKS=0')` (MySQL-specific). On SQLite this fails — switch them to `Schema::disableForeignKeyConstraints()` / `Schema::enableForeignKeyConstraints()`. **See B11.**

**On MySQL** the seeders work, but `CaringSeeder` writes `'1:09 PM'` into a `timestamp` column — which MySQL coerces silently today (B12). If you migrate to PostgreSQL or strict-mode MySQL this seeder will fail.

### 5. Generate API docs (l5-swagger)

```bash
php artisan l5-swagger:generate
# Browse to /api/documentation
```

The OpenAPI spec is generated from PHP attributes on the controllers. Re-run `l5-swagger:generate` after every route or controller change. Note that the spec and `routes/api.php` can drift — see B18.

### 6. Run the dev server

```bash
php artisan serve --port=8000
```

For Android client testing on a physical device, point `android/app/src/main/java/.../RemoteDataSource.java` at the LAN IP of the dev box (the file currently hard-codes a placeholder URL `https://api.example.com/api/` — change it to your LAN IP + `:8000`).

---

## Deployment — Wasmer Edge

The backend is deployable to [Wasmer Edge](https://wasmer.io/products/edge). See `backend/wasmer.toml` for the package config and `backend/app.yaml` for the app identity.

### Prerequisites
- Wasmer CLI: `curl https://get.wasmer.io -sSfL | sh`
- Wasmer account + namespace: `srckod`
- App secrets (one-time, from your local `.env` — never committed):
  ```bash
  wasmer app secrets create --from-file=.env
  ```
  Stores `APP_KEY`, `JWT_SECRET`, etc. in Wasmer's secret store.

### Database
Wasmer's built-in MySQL is auto-provisioned by `capabilities.database` in
`app.yaml`. Wasmer injects `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`,
`DB_PASSWORD` at runtime (`config/database.php` falls back from `DB_DATABASE`
to `DB_NAME`).

> **Never add `DB_*` to the app environment or secret store.** App-level
> variables are merged *over* the injected ones, so `DB_HOST` becomes empty and
> every request fails with `SQLSTATE[HY000] [2002] Operation not permitted`.
> Only secrets the capability cannot know (`APP_KEY`, `JWT_SECRET`, mail
> credentials) belong there. See [BTL10](../KNOWN-ISSUES.md).

### Migrations & seeding
Wasmer's managed MySQL instance only exists once a version is deployed, and the
platform does **not** run the migration for you: `wasmer deploy` compiles
`app.yaml` into its internal spec and drops the `jobs:` section, so a
`post-deployment` job is silently never executed — see
[BTL12](../KNOWN-ISSUES.md). `backend/app.yaml` therefore carries no job block;
the deploy job in `.github/workflows/backend.yml` runs migrations against the
Wasmer database from CI, after the deploy:

```yaml
- name: Migrate & seed the Wasmer database
  working-directory: backend
  env:
    DB_HOST: ${{ secrets.WASMER_DB_HOST }}
    DB_PORT: ${{ secrets.WASMER_DB_PORT }}
    DB_DATABASE: ${{ secrets.WASMER_DB_DATABASE }}
    DB_USERNAME: ${{ secrets.WASMER_DB_USERNAME }}
    DB_PASSWORD: ${{ secrets.WASMER_DB_PASSWORD }}
  run: |
    php artisan migrate --force
    php artisan db:seed --force
```

Laravel reads process env vars directly and `Dotenv` never overrides variables
that already exist, so no `.env` is needed for this step. The five
`WASMER_DB_*` repo secrets come from `wasmer app database list` (the CLI prints
`n/a` for the password, so read it from the app's secret store or the Adminer
URL).

A pre-deployment job is not an option: it runs *before* the version exists and a
failure there aborts the very deploy that carries its fix — see
[BTL11](../KNOWN-ISSUES.md).

Seeding runs on every deploy, which is intentional: this Wasmer app is the
demo/staging environment, and the seeders truncate their tables before
inserting. If a real production target is added later, move the seed command
behind an explicit condition rather than deleting it silently. Locally:

```bash
php artisan migrate --force
php artisan db:seed --force
```

### Deploy
```bash
cd backend
wasmer deploy
```

### CI/CD
GitHub Actions runs tests on every push (`.github/workflows/backend.yml`), then deploys to Wasmer Edge on push to `main`. The deploy step runs `wasmer deploy --non-interactive --no-wait --no-persist-id` from `backend/`, authenticated with the `WASMER_TOKEN` repo secret, and is followed by a step that runs `php artisan migrate --force && php artisan db:seed --force` against the Wasmer database using the `WASMER_DB_*` secrets.

---

## Routes

See [`routes/api.php`](./routes/api.php) for the canonical list. Major endpoints:

| Method | Path | Controller | Notes |
|--------|------|------------|-------|
| `POST` | `/api/auth/login` | `AuthController` | JWT login |
| `POST` | `/api/auth/register` | `AuthController` | New nurse registration |
| `GET`  | `/api/auth/me` | `AuthController` | Current user (JWT) |
| `GET`  | `/api/nurses` | `NurseController@index` | List with spatie/laravel-query-builder filters |
| `POST` | `/api/nurses` | `NurseController@store` | **`NurseStoreRequest` validation** — see B13 |
| `GET`  | `/api/nurses/{id}` | `NurseController@show` | |
| `PUT`  | `/api/nurses/{id}` | `NurseController@update` | |
| `PATCH`| `/api/nurses/{id}` | `NurseController@update` | **B18 — must be `{id}`** |
| `DELETE`| `/api/nurses/{id}` | `NurseController@destroy` | |
| `GET/POST/PUT/PATCH/DELETE` | `/api/carings`, `/api/caringtypes`, `/api/patients` | … | Mirror the nurse pattern; same known issues |

> **B19:** `/api/nurse` (singular) is registered with `auth:sanctum` but the project actually uses JWT — it returns 302 redirect instead of 401 JSON. Use the `/api/nurses` (plural) endpoints instead.

---

## Tests

```bash
php artisan test
```

The `tests/Feature/ProfileTest.php` test references the deleted `User` model — it fails by default. See **B20** for the fix (remove it, or replace with a `NurseProfileTest` against `App\Models\Nurse`).

For Dusk (browser) tests, see `.env.dusk` — it has its own placeholder secrets.

---

The `scripts/api-sweep.sh` and `scripts/db-reset.sh` helpers exist to re-audit the API surface and reset the local DB. They are *not* a substitute for the credential rotation step — see [`../KNOWN-ISSUES.md` BTL7](../KNOWN-ISSUES.md#btl7--pre-commit-sanitization-workflow-observed-2026-08-27).

---

## Known issues (in this folder)

Full writeup in [`../KNOWN-ISSUES.md`](../KNOWN-ISSUES.md). Short list, scoped to the backend:

* **B11** — All four seeders use raw `SET FOREIGN_KEY_CHECKS` MySQL SQL; breaks on SQLite. Switch to `Schema::disableForeignKeyConstraints()`.
* **B12** — `CaringSeeder` writes `'1:09 PM'` into a `timestamp` column. MySQL coerces; strict-mode DBs won't.
* **B13** — `NurseStoreRequest` rules under-specify the model. Missing fields hit the DB as NULL. Use `prepareForValidation()` to fill defaults.
* **B14** — `APP_DEBUG=true` in non-local environments leaks raw SQL and bcrypt hashes. One-line `.env` fix; deeper lesson in `LEARNING.md` §1.
* **B15** — `failedValidation()` returns HTTP 200 with `success: false` body instead of 422. Clients can't tell success from failure without parsing the body.
* **B18** — `PATCH /nurses` (no `{id}`) registered instead of `PATCH /nurses/{id}`. PATCH-on-resource returns 405. OpenAPI spec is correct; route table is wrong.
* **B19** — `/api/nurse` (singular) uses `auth:sanctum`; project uses JWT — returns 302 instead of 401 JSON.
* **B20** — `ProfileTest` references deleted `User` model. Remove it or rewrite as `NurseProfileTest`.
* **BTL1** — `composer install` fails on Laravel 10 + PHP 8.2 due to `^10.0` minor constraints. Pin or use `--ignore-platform-req`.
* **BTL3** — PHP 8.4 platform mismatch on transitive packages. Use `--ignore-platform-req=php` for learning projects.
* **BTL4** — PHP 8.4 implicit-nullable deprecation warnings during `composer install` and at runtime. Harmless; document.

---

## See also

* [`../LEARNING.md`](../LEARNING.md) — transferable principles distilled from the bug catalog (Lessons 1, 4-6).
* [`../KNOWN-ISSUES.md`](../KNOWN-ISSUES.md) — bug-level writeup with file:line references and "why this isn't fixed here" rationale.
* [`../android/README.md`](../android/README.md) — the Android client that consumes this API.
