## Educational snapshot of the enurse project

Final course-deliverable snapshot of a university mobile-development project: a **Laravel 10 REST API** (`backend/`) with JWT auth + L5-Swagger docs, and a **native Android app** (`android/`, Java + Room + Retrofit).

### What's in this release

- **backend/** — Laravel 10 API with nurses / patients / caringtypes / carings resources, JWT auth (tymon/jwt-auth), Firebase Cloud Messaging, L5-Swagger API docs, MySQL schema with seeders
- **android/** — native Java app (API 26+), Room database with FTS search, Retrofit + OkHttp with token interceptor, Firebase messaging; `app-release.apk` built by CI and attached below
- **CI/CD** — GitHub Actions: PHP 8.2 + MySQL 8.0 backend tests (7/7 passing), Android build + unit tests, deploy to Wasmer Edge
- **Documentation** — README (with translated course report), `KNOWN-ISSUES.md` bug catalog (B1–B15, B18–B22 — 20 backend bugs; AB1–AB12 — 12 Android bugs; M1–M5 — 5 medium bugs; BTL1–BTL14 — 14 build/toolchain lessons), `LEARNING.md` lessons (1–20), screenshots + logos

### Live API

The backend is deployed at **https://nurses-task-system.wasmer.app** — API base URL `https://nurses-task-system.wasmer.app/api/`.

- Swagger UI: https://nurses-task-system.wasmer.app/api/documentation
- Demo login: `POST /api/auth/login` with the seeded `admin@example.com` account

The Android release build targets that host by default (`apiUrlRelease` in `android/app/build.gradle`, injected per build type via `BuildConfig`); debug builds target `http://10.0.2.2:8000/api/` for the local emulator loopback.

### Intentionally preserved bugs

This repo is an **educational resource**: known bugs and design failures from the original coursework are deliberately kept intact and catalogued so trainees can study real mistakes. Do not deploy this in production — see [SECURITY.md](https://github.com/srcKod/nurses-task-system/blob/main/SECURITY.md).

### Android APK

`app-release.apk` (attached below, built by CI from this tag's commit) — release build for Android 8.0+ (API 26+). Install on a device or emulator; it talks to the live Wasmer Edge API out of the box.

