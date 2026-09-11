# Android — e-Nurses Jobs client

[![Java: 17](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](./app/build.gradle)
[![Android: API 26+](https://img.shields.io/badge/Android-API_26%2B-3DDC84?logo=android&logoColor=white)](./app/build.gradle)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](../LICENSE)
[![Status: educational](https://img.shields.io/badge/Status-educational-orange)](#what-this-is-not)

The Android client for the **e-Nurses Jobs** mobile-app tutorial project. A native Java Android app that consumes the REST API exposed by [`../backend/`](../backend/).

> **This is a teaching repo, not a production codebase.** It is *intentionally* left with a catalog of known bugs and anti-patterns. See [`../KNOWN-ISSUES.md`](../KNOWN-ISSUES.md) (the *High — Android* section) and [`../LEARNING.md`](../LEARNING.md) for the full writeup.

## Table of contents

* [Stack](#stack)
* [What this is not](#what-this-is-not)
* [Quickstart](#quickstart)
* [Setup in detail](#setup-in-detail)
* [Source layout](#source-layout)
* [Key flows](#key-flows)
* [Known issues (in this folder)](#known-issues-in-this-folder)
* [Tests](#tests)
* [See also](#see-also)

## What this is not

* **Not production software.** Insecure in several documented ways. **Do not deploy.**
* **Not a maintained library.** No support commitment.

---

## Stack

| Layer | Library / version |
|-------|-------------------|
| Language | Java 17 source / 8 bytecode (`sourceCompatibility VERSION_17`) |
| Min SDK / Target SDK | 26 / 34 (`compileSdk 34`) |
| Build | Android Gradle Plugin, Gradle wrapper (`./gradlew`), `id 'com.google.gms.google-services'` for Firebase plugin |
| Architecture | MVVM with a single `DataRepository` (god-class — see below), Room cache, Retrofit for remote, Paging 3 for lists |
| HTTP | Retrofit 2 + OkHttp 4 + Gson. Custom `AccessTokenInterceptor` and `Authenticator` for JWT bearer tokens |
| Cache | Room (SQLite ORM) for offline cache of nurses, carings, caringtypes, patients, and the access token |
| Navigation | AndroidX Navigation Component + Safe Args plugin |
| UI | ViewPager 2 + Fragments, Material Components, RecyclerView adapters with Paging 3 (`PagingDataAdapter`) |
| Push notifications | Firebase Cloud Messaging (`com.google.gms:google-services` plugin + Firebase BOM) |
| Realtime | Pusher Channels (`com.pusher:pusher-java-client`) — *server-side credentials in `strings.xml` are placeholder* |
| Image loading | Glide (for avatars) |
| Async | `androidx.lifecycle:lifecycle-viewmodel` + coroutines in `AppExecutors` (background, IO, main) |

`build.gradle` is in [`app/build.gradle`](./app/build.gradle). The root [`build.gradle`](./build.gradle) declares classpath plugins; the wrapper version is in [`gradle/wrapper/gradle-wrapper.properties`](./gradle/wrapper/gradle-wrapper.properties).

---

## Quickstart

The shortest path from a clean checkout to a debug build:

```bash
cd android
cp local.properties.example local.properties   # then edit sdk.dir
cp app/google-services.json.example app/google-services.json   # placeholder is fine for boot-only
./gradlew assembleDebug
```

The APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

## Setup in detail

### 1. Prerequisites

* Android Studio Hedgehog (2023.1) or newer with AGP 8.x.
* JDK 17 (`JAVA_HOME` env var). AGP 8.x requires 17 minimum.
* Android SDK 34 + a device / emulator on API 26+.
* A reachable backend (the `../backend/` Laravel app on `:8000`, or your own deployment). Debug builds point at the emulator host (`http://10.0.2.2:8000/api/`) by default; for a physical device on your LAN set `apiUrlDebug` in `gradle.properties` (see [§4](#4-point-the-api-at-your-backend)).

### 2. Configure local SDK path

`local.properties` is **gitignored** (it's machine-specific). The committed `local.properties.example` is the template:

```properties
# local.properties.example — copy to local.properties and edit
sdk.dir=C\:\\Users\\YOURNAME\\AppData\\Local\\Android\\Sdk
```

The placeholder Android API key placeholder for `local.properties`:

```properties
# Map value for Map<String, Object> literal "API_KEY_MAP" if you use one:
# (none defined; included for parity with future configs)
```

### 3. Place a format-valid placeholder Firebase config

Firebase auto-initialization reads `app/google-services.json` *before* `Application.onCreate()`. If the file is missing or has a malformed `mobilesdk_app_id` / `current_key`, the app crashes silently before showing any UI.

A "fake but valid" placeholder lives at **`app/google-services.json.example`** — not at `app/google-services.json`. The real file is gitignored. To run with a real Firebase project, copy the example to `app/google-services.json` and edit the values.

**Don't replace the example with `your-firebase-key-here`** — the API key regex `A[\w-]{38}` rejects that string. The placeholder uses `AIzaSyDUMMY0000000000000000000000000000`, which passes the regex and keeps the app bootable on a fresh clone. See [`../KNOWN-ISSUES.md` BTL6](../KNOWN-ISSUES.md#btl6--a-placeholder-google-servicesjson-with-a-non-format-api-key-crashes-the-app-at-boot-resolved-2026-08-27) for the full crash-chain writeup.

### 4. Point the API at your backend

The base URL is injected per build type as `BuildConfig.API_URL` — no source edits needed:

| Build type | Default base URL | When |
|---|---|---|
| `debug` | `http://10.0.2.2:8000/api/` | Emulator hitting the host machine |
| `release` | `https://nurses-task-system.wasmer.app/api/` | HTTPS staging backend |

`app/src/main/java/com/example/enursejobs/api/RemoteDataSource.java` reads the injected constant:

```java
String API_URL = BuildConfig.API_URL;   // filled in from app/build.gradle
```

Override per machine **without touching source** by adding to `gradle.properties` (or `~/.gradle/gradle.properties`):

```properties
# Physical device on your LAN, backend served from the dev box:
apiUrlDebug=http://192.168.1.50:8000/api/

# Your own production deployment:
apiUrlRelease=https://your-production.example.com/api/
```

Cleartext HTTP is allowed for **debug builds only** via `app/src/debug/AndroidManifest.xml` (`android:usesCleartextTraffic="true"`). Release builds keep the platform default — HTTPS only.

### 5. Build and run

```bash
cd android
./gradlew assembleDebug          # produces app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug           # to a connected device / emulator
```

For a release APK, `release` builds are signed with the debug keystore (`signingConfig signingConfigs.debug`) so CI-produced release APKs are installable for testing. Swap in a real keystore before shipping to any store.

---

## Source layout

The package layout mirrors MVVM with a flat repository layer:

```
com.example.enursejobs/
├── api/
│   ├── EntityRemote/        # Retrofit DTOs (NurseEntityRemote, CaringEntityRemote, etc.)
│   └── ServiceRetrofit/     # Retrofit Service interfaces + RemoteDataSource + Interceptors + Authenticator
├── db/
│   ├── Converter/           # Room TypeConverters
│   ├── Dao/                 # Room DAOs (NurseFtsEntity, CaringFtsEntity, …)
│   ├── EntityLocal/         # Room @Entity classes
│   └── model/               # In-memory model wrappers
├── paging/                  # Paging 3 PagingSources
├── ui/
│   ├── activity/            # MainActivity + auth activities
│   ├── adapter/             # RecyclerView / PagingDataAdapter
│   └── fragment/            # Login, Register, Profile, Caring list, Caring detail
└── viewmodel/               # AndroidX ViewModels — typically one per Fragment
```

The single point of data-flow coordination is `DataRepository` (`data/DataRepository.java` — ~700 lines). All UI/ViewModel code talks to the repository; the repository fans out to the Retrofit services and the Room DAOs. **This is a god-class — see "Known issues" below.**

---

## Key flows

### Login → token storage

`LoginFragment` → `LoginViewModel` → `AuthRepository` → `RemoteDataSource.login()` (Retrofit POST `/api/auth/login`).

Server returns `{ token, … }`. The token is stored in Room (`db/EntityLocal/AccessTokenEntity` / `t_accesstoken`) via the access-token DAO.

### Authenticated requests

Every authenticated request goes through:

1. `AccessTokenInterceptor.intercept()` — reads the token from Room, attaches it as `Authorization: Bearer …`.
2. If the server returns 401, `Authenticator.authenticate()` is invoked to refresh the token via `/api/auth/refresh`.

Both interceptors are **broken** in a subtle, well-documented way: see [Known issues](#known-issues-in-this-folder) below and [`../LEARNING.md` §3](../LEARNING.md).

### Local cache

The Room database holds nurses, carings, caringtypes, patients, and the access token. The `paging/` package wires the DAOs into Paging 3 sources. ViewModels observe PagingData streams, and Fragments bind to PagingDataAdapters.

---

## Known issues (in this folder)

Full writeup in [`../KNOWN-ISSUES.md`](../KNOWN-ISSUES.md), specifically the *High — Android* section. The short list:

* **AccessTokenInterceptor async/sync mismatch.** Uses `Futures.addCallback` to read the token from Room, then uses the variable in the *same synchronous expression* that follows. The callback hasn't run yet, so the token is `null` and the request goes out unauthenticated. **See [`../LEARNING.md` §3](../LEARNING.md#lesson-3--dont-read-async-results-synchronously).**
* **Authenticator async/sync mismatch.** Same pattern, same fix — block on the future from a background thread, or use a synchronized token cache.
* **`t_accesstoken` PK conflict.** The access-token table uses `WHERE seq=0` as a "singleton" row; `INSERT OR REPLACE INTO t_accesstoken (seq, value) VALUES (0, ?)` ends up clobbering the *existing* row at seq=0 with the new value, but the autoincrement id collides and `null` ends up in the access-token column. The user is logged out on every cold start. Fix: use a stable non-zero primary key.
* **`Thread.sleep(4000)` on first install.** The database-creation callback blocks the IO thread for 4 seconds — leftover from a tutorial. Delete it.
* **Test credentials seeded on first install.** The `RoomDatabase.Callback.onCreate()` inserts `testname` / `test@mail.com` / `testpass` — PII-shaped strings that have no business in a public-repo seed. Replace with a comment.
* **`NurseEntityRemote` includes `password` field.** Used as both request and response DTO — the password round-trips on every read. Fix: split into `NurseRequest` and `NurseResponse`. **See [`../LEARNING.md` §7](../LEARNING.md#lesson-7--separate-request-and-response-dtos-never-serialize-hashes-on-the-wire).**
* **`CaringService.deleteCaringRemote` returns `CaringtypeEntityRemote`.** Wrong DTO type for the operation. Same for `pruneCaringRemote`.
* **`fetchCaringsByCaringtypeIdRemote` has `@Path("nurse_id")` but the placeholder in the URL is `{caringtype_id}`.** Server is happy to bind the parameter, but the path template on the server won't match. Fix: align the `@Path` value with the placeholder.
* **Migration case mismatch.** `nurse_Fts` migration table name vs `NurseFtsEntity` class — case difference can cause migration failures on some Android filesystem configurations. Normalize to `nurse_fts`.
* **`DataRepository` is a ~700-line god-class.** Method bodies are near-identical: "fan out to remote, fan out to local, merge." Refactor toward a per-domain repository (`NurseRepository`, `CaringRepository`, etc.) or move toward the official *Google Architecture Blueprints* sample patterns.
* **API URL hard-coded (resolved 2026-09-10).** `RemoteDataSource.API_URL` was a hard-coded constant. Now `BuildConfig.API_URL` is injected per build type from `app/build.gradle` — debug `http://10.0.2.2:8000/api/`, release `https://nurses-task-system.wasmer.app/api/` — overridable via `apiUrlDebug` / `apiUrlRelease` in `gradle.properties` ([§4](#4-point-the-api-at-your-backend)).
* **PATCH without `{id}` (mirror of backend B18).** Some Android calls hit `PATCH /nurses` — same path as the broken backend route. Once B18 is fixed server-side, the Android client matches.
* **Two launcher PNGs were corrupted** in the copied tree (BTL5). Fixed during the repo-restructuring: replaced with the originals.
* **Placeholder `google-services.json` non-format API key** would crash the app at boot (BTL6). Replaced with a format-valid dummy, and the file is now committed as **`google-services.json.example`** (the real `google-services.json` is gitignored).

---

## Tests

Unit tests live under `app/src/test/java/`. Instrumented tests under `app/src/androidTest/java/`. To run:

```bash
./gradlew test                   # JVM unit tests
./gradlew connectedAndroidTest   # requires a device / emulator
```

There is no `ProfileTest`-style scaffolded-but-broken test in this folder — the broken one was in `../backend/tests/Feature/ProfileTest.php` (B20).

## See also

* [`../LEARNING.md`](../LEARNING.md) — transferable principles (Lessons 2, 3, 7 are Android-focused).
* [`../KNOWN-ISSUES.md`](../KNOWN-ISSUES.md) — bug-level writeup with file:line references.
* [`../backend/README.md`](../backend/README.md) — the Laravel API the client consumes.
