# KNOWN-ISSUES — the bug catalog

## Entry format

Every entry must include these headings in this order:

- **Severity:** impact level.
- **Files:** affected paths (plural, even for one file).
- **Symptom:** observable failure or risk.
- **Status:** `Status as of <date>: <state>` (for example, `Status as of 2026-09-10: documented; fix intentionally not applied`).
- **Original code:** relevant defective implementation.
- **Ref:** link to the relevant source, issue, or commit.
- **Fix:** recommended or applied remediation.
- **Root cause:** why the defect occurred.

> **Test status as of 2026-08-27 — all suites green:**
> - Backend: `php artisan test` → 7 passed, 0 failed (23 assertions). Required fixes B20 (ProfileTest → Nurse) and the `ProfileUpdateRequest` unique-ignore bug.
> - Android unit: `./gradlew test` → 2/2 passed (debug + release `ExampleUnitTest`). Required fix BTL5 (corrupted launcher PNGs).
> - Android instrumented: `./gradlew connectedDebugAndroidTest` → 1/1 passed (`ExampleInstrumentedTest`) on the `Huawei_Mate_10_lite_VM` AVD. Required fix BTL6 (format-valid dummy Firebase API key).

---

## Critical

### AB1 — `AccessTokenInterceptor` reads the JWT asynchronously, then uses it synchronously

- **Severity:** Critical (every first request after login is unauthenticated)
- **Files:** `android/app/src/main/java/com/example/enursejobs/api/ServiceRetrofit/AccessTokenInterceptor.java`
- **Symptom:** The user logs in successfully, but every subsequent API call returns 401 until the app is killed and restarted.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```java
  Futures.addCallback(database.AccessTokenDao().selectToken(), new FutureCallback<String>() {
      @Override public void onSuccess(String result) { accessToken = result; }
  }, new AppExecutors().diskIO());
  Request request = newRequestWithAccessToken(chain.request(), accessToken); // still null!
  return chain.proceed(request);
  ```
- **Ref:** `android/app/src/main/java/com/example/enursejobs/api/ServiceRetrofit/AccessTokenInterceptor.java`
- **Fix:** Block on the future inside the interceptor so the value is available before `chain.proceed()`:
  ```java
  String accessToken = database.AccessTokenDao().selectToken().get(); // blocks interceptor thread
  return chain.proceed(newRequestWithAccessToken(chain.request(), accessToken));
  ```
- **Root cause:** Room's `selectToken()` returns a `ListenableFuture`. `Futures.addCallback(...)` queues the callback on the disk executor and returns immediately. The next line reads `accessToken` before the callback has executed, so `accessToken` is still `null`. OkHttp then sends the request with no `Authorization` header, and the JWT guard rejects it.

---

### AB2 — `NurseEntity` stores the password in plaintext on the client

- **Severity:** Critical (credential stored in local SQLite)
- **Files:** `android/app/src/main/java/com/example/enursejobs/db/EntityLocal/NurseEntity.java`
- **Symptom:** After login, the nurse's plaintext password is persisted to Room. Any process or backup that can read the app's SQLite file can read the password.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```java
  @Entity(tableName = "t_nurse")
  public class NurseEntity {
      @PrimaryKey public int id;
      public String name;
      public String email;
      public String password; // ❌ plaintext password stored locally
      // ...
  }
  ```
- **Ref:** `android/app/src/main/java/com/example/enursejobs/db/EntityLocal/NurseEntity.java`
- **Fix:** Remove `password` from `NurseEntity`, `NurseEntityRemote`, and `AuthEntity`. The mobile app should never see it.
- **Root cause:** The `NurseEntity` class has a `password` column populated from the server's `NurseEntityRemote`. Nothing strips it before `@Insert`.

---

### AB3 — `NurseEntityRemote` includes `password` in the JSON payload

- **Severity:** Critical (credential round-trips over the wire on every nurse API call)
- **Files:** `android/app/src/main/java/com/example/enursejobs/api/EntityRemote/NurseEntityRemote.java`, `backend/app/Http/Resources/NurseResource.php`
- **Symptom:** Every `GET /api/nurses`, `PATCH /api/nurses`, etc. includes the password hash. On create, the plaintext password the admin typed is sent in the request body.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  public function toArray($request)
  {
      return [
          'id' => $this->id,
          'name' => $this->name,
          'email' => $this->email,
          'password' => $this->password, // ❌ credential exported in JSON
      ];
  }
  ```
- **Ref:** `backend/app/Http/Resources/NurseResource.php`
- **Fix:** Remove `password` from the network model entirely.
- **Root cause:** The network DTO was written as a mirror of the database row instead of as an API contract. Fields that should never leave the server are included by default.

---

### AB4 — FCM token insert (PK=0) clobbers the JWT in the same row

- **Severity:** Critical (every Firebase token refresh logs the user out)
- **Files:** `android/app/src/main/java/com/example/enursejobs/fcm/FcmService.java`
- **Symptom:** After login, everything works — until Firebase refreshes the device token. The next API call returns 401 and the user is forced to log in again.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```java
  database.AccessTokenDao().insertAccessToken(
      new AccessTokenEntity(0, null /* token! */, "bearer", 3600, token));
  ```
- **Ref:** `android/app/src/main/java/com/example/enursejobs/fcm/FcmService.java`
- **Fix:** Split the storage into two tables (one for the JWT, one for the FCM token), or use a proper composite key and an explicit `UPDATE … WHERE seq=0` for the JWT.
- **Root cause:** The entity's primary key is hard-coded to `0`, and `@Insert(onConflict = OnConflictStrategy.REPLACE)` replaces the existing row. The existing row at PK=0 stores the JWT. When FCM updates its token using PK=0, it writes `null` for the JWT, destroying active session authentication.

---

## High — Android

### AB5 — `SplashActivity` always routes to `LoginActivity`, even with a saved token

- **Severity:** Medium (forced re-login on every cold start)
- **Files:** `android/app/src/main/java/com/example/enursejobs/ui/activity/SplashActivity.java`
- **Symptom:** Even when a JWT is persisted in Room, the app always starts at `LoginActivity`.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```java
  Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
  startActivity(intent);
  finish();
  ```
- **Ref:** `android/app/src/main/java/com/example/enursejobs/ui/activity/SplashActivity.java`
- **Fix:** Read the saved token from `AccessTokenDao` (or a `SessionManager` wrapper) and route to `MainActivity` when it is non-null.
- **Root cause:** The splash screen is implemented as a static one-way launchpad and does not query the token store before choosing the next Activity target.

---

### AB6 — `CaringService.deleteCaringRemote` and `pruneCaringRemote` return the wrong generic type

- **Severity:** Medium (compiles, but the response body is deserialized into the wrong class)
- **Files:** `android/app/src/main/java/com/example/enursejobs/api/ServiceRetrofit/CaringService.java`
- **Symptom:** Both methods return `Call<CaringtypeEntityRemote>` instead of `Call<CaringEntityRemote>`.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```java
  @DELETE("carings/{id}")
  Call<CaringtypeEntityRemote> deleteCaringRemote(@Path("id") int id);

  @DELETE("carings/prune/{id}")
  Call<CaringtypeEntityRemote> pruneCaringRemote(@Path("id") int id);
  ```
- **Ref:** `android/app/src/main/java/com/example/enursejobs/api/ServiceRetrofit/CaringService.java`
- **Fix:** Change both return types to `Call<CaringEntityRemote>`.
- **Root cause:** Code copy-pasted from the `CaringtypeService` interface where the generic response type parameter was never updated.

---

### AB7 — `fetchCaringsByCaringtypeIdRemote` has a mismatched `@Path` annotation

- **Severity:** Medium (Retrofit throws at runtime before the request is sent)
- **Files:** `android/app/src/main/java/com/example/enursejobs/api/ServiceRetrofit/CaringService.java`
- **Symptom:** Calling this method throws `IllegalArgumentException: URL query string "caringtype_id={caringtype_id}" …` because the path placeholder and argument name disagree.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```java
  @GET("carings/caringtype/{caringtype_id}")
  Call<List<CaringEntityRemote>> fetchCaringsByCaringtypeIdRemote(@Path("nurse_id") int caringtypeId);
  ```
- **Ref:** `android/app/src/main/java/com/example/enursejobs/api/ServiceRetrofit/CaringService.java`
- **Fix:** Change `@Path("nurse_id")` to `@Path("caringtype_id")`.
- **Root cause:** Parameter annotation mistyped during API declaration, creating a mismatch between Retrofit's URL path variable placeholder (`caringtype_id`) and parameter binder variable (`nurse_id`).

---

### AB8 — `AuthService.refreshAccessTokenRemote` sends the old token in the request body

- **Severity:** Medium (the refresh endpoint never reads the body)
- **Files:** `android/app/src/main/java/com/example/enursejobs/api/ServiceRetrofit/AuthService.java`
- **Symptom:** The client attempts to send the current token in the body; the backend route expects it in the `Authorization` header. On a fresh install it 401s.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```java
  @POST("auth/refresh")
  Call<AccessTokenEntityRemote> refreshAccessTokenRemote(@Body String accessToken);
  ```
- **Ref:** `android/app/src/main/java/com/example/enursejobs/api/ServiceRetrofit/AuthService.java`
- **Fix:** Add an `@Header("Authorization") String authorization` parameter to the Retrofit interface and populate it from the saved JWT.
- **Root cause:** Interface design mismatch between client and backend spec. The client treats the refresh mechanism as a JSON body payload, while the JWT middleware expects standard bearer authorization headers.

---

### AB9 — Login flow mixes async token/FCM writes and can lose the JWT between steps

- **Severity:** Medium (fragile async chain; most users see a stale UI or 401 from the next API call)
- **Files:** `android/app/src/main/java/com/example/enursejobs/DataRepository.java`, `android/app/src/main/java/com/example/enursejobs/viewmodel/LoginViewModel.java`
- **Symptom:** After login, navigating to `MainActivity` → `HomeFragment` may return a 401 because access-token write and FCM-token write race, allowing AB4 to clobber the JWT.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```java
  public void login(String email, String password) {
      authService.login(email, password).enqueue(new Callback<>() {
          @Override public void onResponse(...) {
              tokenDao.insertAccessToken(token); // async callback 1
              fcmService.updateToken(fcmToken);   // async callback 2
          }
      });
  }
  ```
- **Ref:** `android/app/src/main/java/com/example/enursejobs/DataRepository.java`
- **Fix:** Collapse the two writes into one explicit dependency chain, or pre-load the token into memory before navigation and refresh from DB only on cold start.
- **Root cause:** `login()` nests two un-synchronized `Futures.addCallback(...)` chains on Room DAOs — one updates the JWT, the next updates FCM token on the server. Neither chain waits for completion.

---

### AB10 — `CaringEntity.setCreatedAt(String)` and `setDeletedAt(String)` ignore their parameter

- **Severity:** Low (trivial type/name confusion, but it will bite a future maintainer)
- **Files:** `android/app/src/main/java/com/example/enursejobs/db/EntityLocal/CaringEntity.java`
- **Symptom:** The setters compile and run, but the passed `time` argument is discarded and the field is assigned to itself (`this.createdAt = createdAt`).
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```java
  @Override
  public void setCreatedAt(String time) { this.createdAt = createdAt; }
  @Override
  public void setDeletedAt(String time) { this.deletedAt = deletedAt; }
  ```
- **Ref:** `android/app/src/main/java/com/example/enursejobs/db/EntityLocal/CaringEntity.java`
- **Fix:** Update signatures to `this.createdAt = time;` and `this.deletedAt = time;`.
- **Root cause:** Setter parameter name mismatch where incoming method argument (`time`) differs from target class field variable (`createdAt` / `deletedAt`), assigning member variables to themselves.

---

### AB11 — `CaringDao.selectTrashedCarings()` filters by `status='Finished'` instead of soft-delete

- **Severity:** Medium (the trashed-caring flow is dead code — it never returns rows)
- **Files:** `android/app/src/main/java/com/example/enursejobs/db/Dao/CaringDao.java`
- **Symptom:** The `getTrashedCarings()` flow in `DataRepository` is dead code. The query filters on `status='Finished'`, but the server marks trash via soft deletes (`deleted_at IS NOT NULL`).
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```java
  @Query("SELECT * FROM t_caring WHERE status='Finished'")
  LiveData<List<CaringEntity>> selectTrashedCarings();
  ```
- **Ref:** `android/app/src/main/java/com/example/enursejobs/db/Dao/CaringDao.java`
- **Fix:** Update SQL query to `SELECT * FROM t_caring WHERE deleted_at IS NOT NULL`.
- **Root cause:** Misunderstanding of backend soft-deletion semantics. The local DAO query was implemented around state status values rather than timestamp availability.

---

### AB12 — FTS4 migration and `@Entity` table names disagree on casing

- **Severity:** Low (maintenance hazard)
- **Files:** `android/app/src/main/java/com/example/enursejobs/db/AppDatabase.java`, `android/app/src/main/java/com/example/enursejobs/db/EntityLocal/CaringtypeFtsEntity.java`
- **Symptom:** Migration creates `Caringtype_Fts`, but the entity declares `@Entity(tableName = "caringtype_Fts")`.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```java
  // AppDatabase.java
  database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `Caringtype_Fts` USING FTS4...");

  // CaringtypeFtsEntity.java
  @Entity(tableName = "caringtype_Fts")
  public class CaringtypeFtsEntity {}
  ```
- **Ref:** `android/app/src/main/java/com/example/enursejobs/db/AppDatabase.java`
- **Fix:** Standardize casing across FTS tables to `snake_case` (`caringtype_fts`) in both migration SQL and entity annotations.
- **Root cause:** Hand-written migration SQL strings and hand-written entity annotations were written independently without cross-validation.

---

## High — Backend

### B1 — `NurseController::show()` references non-existent relations

- **Severity:** Medium (the `show()` action always renders an empty table)
- **Files:** `backend/app/Http/Controllers/NurseController.php` (lines 60–78, 95–113)
- **Symptom:** The `show()` and `showById()` methods iterate `$caring->caringtypes` and `$caring->patients` (plural), but `Caring` model defines singular relations `caringtype()` and `patient()`.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  foreach ($carings as $caring) {
      foreach ($caring->caringtypes as $caringtype) {   // ❌ plural
          foreach ($caring->patients as $patient) {       // ❌ plural
  ```
- **Ref:** `backend/app/Http/Controllers/NurseController.php`
- **Fix:** Update properties to `$caring->caringtype` and `$caring->patient`.
- **Root cause:** Pluralization assumptions placed on Singular Eloquent Model relationship associations (`hasOne` / `belongsTo`).

---

### B2 — `CaringController::store()` dispatches FCM notification with a fragile `pluck()` on a single model

- **Severity:** Low (works by accident, but intent is unclear)
- **Files:** `backend/app/Http/Controllers/CaringController.php` (lines 49–53)
- **Symptom:** Code uses `$nurse->pluck('fcm_token')` on a single model instance, returning a one-element Eloquent collection.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  $nurse = Nurse::where('id', $request->nurse_id)->whereNotNull('fcm_token')->first();
  $fcmTokens = $nurse->pluck('fcm_token')->toArray();
  NotificationJob::dispatch($nurse, $fcmTokens);
  ```
- **Ref:** `backend/app/Http/Controllers/CaringController.php`
- **Fix:** Replace line with `$fcmTokens = [$nurse->fcm_token];`.
- **Root cause:** Developer confused Eloquent Builder query collection retrieval with single Model instance attribute resolution.

---

### B3 — `checkCaring()` mutates state on a `GET` endpoint, violating REST

- **Severity:** Medium (a GET route that deletes data is a footgun for crawlers and pre-fetching)
- **Files:** `backend/app/Http/Controllers/Api/CaringController.php`
- **Symptom:** `GET /api/carings/check/{id}` sets `is_finished = true`, saves, and soft-deletes the caring. Pre-fetching HTTP clients will silently alter data.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  /** @OA\Get(path="/api/carings/check/{id}", ...) */
  public function checkCaring(int $id){
      $caring = Caring::withTrashed()->find($id);
      $caring->is_finished = true;
      $caring->save();
      $caring->delete();
  ```
- **Ref:** `backend/app/Http/Controllers/Api/CaringController.php`
- **Fix:** Change route verb and Swagger annotation to `PATCH /api/carings/check/{id}`.
- **Root cause:** Violation of RESTful idempotency principles by attaching database mutations to HTTP `GET` handlers.

---

### B4 — `CaringSeeder` writes time-only strings into a `timestamp UNIQUE` column

- **Severity:** Medium (works on MySQL by implicit coercion; produces semantically wrong demo data)
- **Files:** `backend/database/seeders/CaringSeeder.php` (lines 21–95)
- **Symptom:** `Caring::create([...])` passes `now('Asia/Damascus')->format('g:i A')` (e.g. `"3:28 PM"`) into `timestamp NOT NULL UNIQUE` column.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  'time' => now('Asia/Damascus')->format('g:i A'),  // "3:28 PM"
  ```
- **Ref:** `backend/database/seeders/CaringSeeder.php`
- **Fix:** Insert full timestamps with historical offsets: `now('Asia/Damascus')->subDays(rand(1, 30))->format('Y-m-d H:i:s')`.
- **Root cause:** MySQL non-strict default casting coerces time-of-day strings into full timestamps by prepending today's date, obscuring type mismatch errors.

---

### B5 — `ApiResponseTrait` is an empty wrapper around `response($data, $status)`

- **Severity:** Low (adds indirection with no envelope or formatting)
- **Files:** `backend/app/Traits/ApiResponseTrait.php`
- **Symptom:** `apiResponse($data, $status)` is called across API controllers, but the implementation is an un-enveloped `return response($data, $status);`.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  public function apiResponse($data=null,$status=null,$message=null): Response|Application|ResponseFactory
  {
      $array=[
  //        'key'=>$data,
  //        'status'=>$status,
  //        'message'=>$message,
      ];
      return response($data,$status);
  }
  ```
- **Ref:** `backend/app/Traits/ApiResponseTrait.php`
- **Fix:** Re-implement standardized JSON response structure or remove trait to call response helpers directly.
- **Root cause:** Incomplete abstraction extraction where developer commented out target envelope payload mapping during early development.

---

### B6 — `JWT_SECRET` exists in `.env` but is missing from `.env.example`

- **Severity:** Medium (new clones get empty secret and 500 on auth)
- **Files:** `backend/.env`, `backend/.env.example`, `backend/config/jwt.php`
- **Symptom:** Fresh clone has no secret defined, causing JWT authentication calls to fail with 500 errors until key generation is executed.
- **Status:** Status as of 2026-08-26: `.env.example` updated in repo to include key placeholder.
- **Original code:**
  ```dotenv
  # backend/.env.example
  # Missing JWT_SECRET key definition entirely
  ```
- **Ref:** `backend/.env.example`
- **Fix:** Add `JWT_SECRET=` placeholder in `.env.example` along with setup instructions.
- **Root cause:** Configuration key omitted from repository environment template file during initial project setup.

---

### B7 — `AuthController::nurseProfile` typo in the 401 message

- **Severity:** Low (cosmetic, visible to API clients)
- **Files:** `backend/app/Http/Controllers/AuthController.php`
- **Symptom:** Unauthenticated profile requests return string `"Unuthorized"`.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  return $this->apiResponse(null,401,"Unuthorized");
  ```
- **Ref:** `backend/app/Http/Controllers/AuthController.php`
- **Fix:** Correct spelling to `"Unauthorized"`.
- **Root cause:** Typographical error in static response string literal.

---

### B8 — `CaringTrait::saveRequestData()` overwrites `description` on PATCH requests

- **Severity:** Medium (PATCH with no `description` field clears existing value)
- **Files:** `backend/app/Traits/CaringTrait.php`
- **Symptom:** Every other field uses a null-safe ternary, but `description` is assigned unconditionally.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  $caring->nurse_id = ($request->nurse_id != null) ? $request->nurse_id : $caring->nurse_id;
  $caring->description = $request->description;  // ❌ no null guard
  ```
- **Ref:** `backend/app/Traits/CaringTrait.php`
- **Fix:** Change line to `$caring->description = ($request->description != null) ? $request->description : $caring->description;`.
- **Root cause:** Missing guard condition on property assignment during partial HTTP resource state updates.

---

### B9 — `NurseResource::toArray` copy-pastes `created_at` into `updated_at`

- **Severity:** Low (API consumers see identical timestamps for creation and modification)
- **Files:** `backend/app/Http/Resources/NurseResource.php`
- **Symptom:** `updated_at` field is formatted from `$this->created_at`.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  'updated_at'=>$this->created_at->format('Y-m-d h:i:s A')
  ```
- **Ref:** `backend/app/Http/Resources/NurseResource.php`
- **Fix:** Update binding reference to `$this->updated_at`.
- **Root cause:** Copy-paste error during API Resource mapping creation.

---

### B10 — `NurseController::updateNurse` updates by email, not by id

- **Severity:** High (API update flow is broken end-to-end)
- **Files:** `backend/app/Http/Controllers/Api/NurseController.php`, `backend/routes/api.php`
- **Symptom:** Route defined as `Route::patch('/nurses','updateNurse')`, and controller queries nurse record by `email` instead of record primary key.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  Route::patch('/nurses','updateNurse');
  // ...
  $nurse = Nurse::where('email', $request->email)->first();
  ```
- **Ref:** `backend/app/Http/Controllers/Api/NurseController.php`
- **Fix:** Change route definition to `Route::patch('/nurses/{id}', 'updateNurse')` and fetch record using `Nurse::findOrFail($id)`.
- **Root cause:** API endpoint mapped without REST route parameters, forcing reliance on optional body params for record location.

---

### B21 — `DashboardController::redirectToApiDocs()` hard-codes `localhost:8000`

- **Severity:** High on deployed app; Low in local dev
- **Files:** `backend/app/Http/Controllers/DashboardController.php`, `backend/routes/web.php`
- **Symptom:** `GET /api-docs` issues `302` redirect to `http://localhost:8000/api/documentation`, breaking docs on deployed environments.
- **Status:** Status as of 2026-09-09: fixed.
- **Original code:**
  ```php
  public function redirectToApiDocs()
  {
      $url='http://localhost:8000/api/documentation';
      return Redirect::to($url);
  }
  ```
- **Ref:** `backend/app/Http/Controllers/DashboardController.php`
- **Fix:** Use relative redirect path:
  ```php
  public function redirectToApiDocs()
  {
      return Redirect::to('/api/documentation');
  }
  ```
- **Root cause:** Hardcoded developer environment origin host string inside redirect helper controller.

---

### B22 — Swagger UI assets and `api-docs.json` 404 on deployed hosts

- **Severity:** High on deployed app (Swagger UI loads unstyled and unusable)
- **Files:** `backend/config/l5-swagger.php`, `backend/public/docs/`
- **Symptom:** Asset and spec requests (`/docs/asset/swagger-ui.css`, `/docs/api-docs.json`) return 404 because files are referenced in gitignored vendor/storage folders.
- **Status:** Status as of 2026-09-10: fixed.
- **Original code:**
  ```php
  // backend/config/l5-swagger.php
  'use_absolute_path' => true,
  'docs' => storage_path('api-docs'),
  'swagger_ui_assets_path' => 'vendor/swagger-api/swagger-ui/dist/',
  ```
- **Ref:** `backend/config/l5-swagger.php`
- **Fix:** Update paths to point to public assets and use relative URLs:
  ```php
  'use_absolute_path' => env('L5_SWAGGER_USE_ABSOLUTE_PATH', false),
  'docs' => public_path('docs'),
  'swagger_ui_assets_path' => env('L5_SWAGGER_UI_ASSETS_PATH', 'public/docs/asset/'),
  ```
- **Root cause:** L5-Swagger default configurations reference paths located inside ignored `vendor/` and `storage/` directories that are absent on source-only platform deployments.

---

### B11 — Seeders use raw MySQL statements, breaking SQLite execution

- **Severity:** High for multi-driver support; Low on primary MySQL targets
- **Files:** `backend/database/seeders/NurseSeeder.php`, `CaringtypeSeeder.php`, `PatientSeeder.php`, `CaringSeeder.php`
- **Symptom:** Running `php artisan db:seed` against SQLite throws `SQLSTATE[HY000]: General error: 1 near "SET": syntax error`.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  DB::statement('SET FOREIGN_KEY_CHECKS = 0');
  Nurse::truncate();
  DB::statement('SET FOREIGN_KEY_CHECKS = 1');
  ```
- **Ref:** `backend/database/seeders/NurseSeeder.php`
- **Fix:** Replace raw foreign key queries with portable facade helpers: `Schema::disableForeignKeyConstraints()` and `Schema::enableForeignKeyConstraints()`.
- **Root cause:** Developer bypassed Laravel's database abstraction layer to write driver-specific MySQL queries directly.

---

### B12 — `CaringSeeder` inserts time strings into full `timestamp` column

- **Severity:** Medium (works on MySQL by coercion; generates wrong demo dates)
- **Files:** `backend/database/seeders/CaringSeeder.php` (lines 21–95)
- **Symptom:** Every seeded record derives date value from insert time, resetting historical context to current execution date.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  Caring::create([
      'time' => now('Asia/Damascus')->format('g:i A'),
      // ...
  ]);
  ```
- **Ref:** `backend/database/seeders/CaringSeeder.php`
- **Fix:** Format string to include explicit dates: `now('Asia/Damascus')->subDays(rand(1, 30))->format('Y-m-d H:i:s')`.
- **Root cause:** Seeder relied on implicit MySQL behavior to cast time values into complete timestamps, resulting in lose of date granularity.

---

### B13 — `NurseStoreRequest` rules omit required DB columns, triggering 500 errors

- **Severity:** High (omitting non-validated fields triggers SQL integrity exceptions)
- **Files:** `backend/app/Http/Requests/NurseStoreRequest.php`
- **Symptom:** Requests valid under `NurseStoreRequest` trigger `SQLSTATE[23000]: Integrity constraint violation: 1048 Column 'gender' cannot be null`.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  public function rules()
  {
      return [
          'name'=> 'required|max:50',
          'email'=>'required|email|unique:nurses',
          'password'=> 'required|min:8'
      ];
  }
  ```
- **Ref:** `backend/app/Http/Requests/NurseStoreRequest.php`
- **Fix:** Expand request rules to cover all required non-nullable database columns (`gender`, `phone`, `is_resigned`, `is_admin`).
- **Root cause:** FormRequest rules were created for a subset of model attributes, while `$request->validated()` was passed directly to model creation calls expecting complete payloads.

---

### B14 — Unhandled 500 responses leak database queries and password hashes

- **Severity:** High (Security risk: leaks internal structure and bcrypt hashes)
- **Files:** `backend/app/Exceptions/Handler.php`, `backend/.env`
- **Symptom:** Failed database queries return full SQL text, connection info, and parameter bindings containing password hashes.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```dotenv
  APP_DEBUG=true
  ```
- **Ref:** `backend/.env`
- **Fix:** Set `APP_DEBUG=false` in production environments and override Exception Handler rendering to sanitize error payloads.
- **Root cause:** Debug environment flag left enabled outside local development bounds, exposing framework exception details to clients.

---

### B15 — `NurseStoreRequest::failedValidation()` returns HTTP 200 on failure

- **Severity:** Low (breaks HTTP protocol contract)
- **Files:** `backend/app/Http/Requests/NurseStoreRequest.php`
- **Symptom:** Validation failure returns HTTP 200 OK containing JSON payload `{"success": false, "message": "Validation errors"}`.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  public function failedValidation(Validator $validator)
  {
      throw new HttpResponseException(response()->json([
          'success'   => false,
          'message'   => 'Validation errors',
          'data'      => $validator->errors()
      ]));
  }
  ```
- **Ref:** `backend/app/Http/Requests/NurseStoreRequest.php`
- **Fix:** Pass status code 422 as second argument to response helper: `response()->json(..., 422)`.
- **Root cause:** Custom validation failure handler omitted explicit HTTP status code, falling back to default 200 response status.

---

### B18 — Resource PATCH routes missing `{id}` parameter matchers

- **Severity:** Medium (all single-resource PATCH updates return HTTP 405)
- **Files:** `backend/routes/api.php` (lines 42, 51, 60, 80)
- **Symptom:** Requests to `PATCH /api/nurses/1` return 405 Method Not Allowed.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  Route::patch('/nurses','updateNurse');
  Route::patch('/caringtypes','updateCaringType');
  Route::patch('/patients','updatePatient');
  Route::patch('/carings','updateCaring');
  ```
- **Ref:** `backend/routes/api.php`
- **Fix:** Add dynamic path parameters to route declarations: `Route::patch('/nurses/{id}', 'updateNurse');`.
- **Root cause:** Routes registered against base resource URI path strings without specifying identity parameters required for model updates.

---

### B19 — `/api/nurse` uses Sanctum guard instead of project JWT guard

- **Severity:** Low (dead route returning redirects instead of JSON)
- **Files:** `backend/routes/api.php` (lines 22–24)
- **Symptom:** Querying `GET /api/nurse` yields HTTP 302 Redirect to `/login` instead of HTTP 401 JSON.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  Route::middleware('auth:sanctum')->get('/nurse', function (Request $request) {
      return $request->nurse();
  });
  ```
- **Ref:** `backend/routes/api.php`
- **Fix:** Change middleware to `auth:api` or remove legacy endpoint.
- **Root cause:** Unused sample route generated by initial framework installer remaining after authentication layer was replaced with JWT.

---

### B20 — `ProfileTest` targets deleted `User` model and uses invalid unique rule ignore logic

- **Severity:** High (entire test suite failing and profile updates broken)
- **Files:** `backend/tests/Feature/ProfileTest.php`, `backend/app/Http/Requests/ProfileUpdateRequest.php`
- **Symptom:** Backend tests fail with `Class "App\Models\User" not found`. Profile update requests fail with validation errors when keeping existing email addresses.
- **Status:** Status as of 2026-08-27: fixed.
- **Original code:**
  ```php
  // ProfileUpdateRequest.php
  'email' => ['email', 'max:255', Rule::unique(Nurse::class)->ignore($this->nurse?->id)]
  ```
- **Ref:** `backend/app/Http/Requests/ProfileUpdateRequest.php`
- **Fix:** Update test suite references from `User` to `Nurse` model, and change ignore call in request validation to `->ignore($this->user()->id)`.
- **Root cause:** Laravel Breeze authentication scaffolding was not updated to reflect model renaming from `User` to `Nurse`. `$this->nurse` evaluated to `null`, breaking unique validation ignore logic.

---

## Medium

### M1 — Hard-coded deployment values scattered across config and controllers

- **Severity:** Low (breaks portability and environment independence)
- **Files:** `backend/app/Http/Controllers/DashboardController.php`, `backend/config/splade-seo.php`, `backend/config/l5-swagger.php`
- **Symptom:** Hardcoded instances of `localhost:8000`, `example.com`, or local machine directories break application links in production environments.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  $url = 'http://localhost:8000/api/documentation';
  ```
- **Ref:** `backend/app/Http/Controllers/DashboardController.php`
- **Fix:** Replace static string references with dynamic environment helpers: `config('app.url')` / `url()`.
- **Root cause:** Hardcoded developer environment variables written during initial feature implementation.

---

### M2 — Dead / commented-out code blocks left in controllers

- **Severity:** Low (code noise)
- **Files:** `backend/app/Http/Controllers/NurseController.php`, `backend/app/Http/Controllers/CaringController.php`
- **Symptom:** Unused execution paths and obsolete code remain inline as comments.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  // $nurse = Nurse::create([
  //     'name' => $request->name,
  // ]);
  ```
- **Ref:** `backend/app/Http/Controllers/NurseController.php`
- **Fix:** Remove dead commented code blocks from source files.
- **Root cause:** Abandoned code refactoring remnants left uncleaned prior to source control commits.

---

### M3 — Lowercase model class instantiation (`new caring()`, `new nurse()`)

- **Severity:** Low (violates PSR-12 coding standard)
- **Files:** `backend/app/Http/Controllers/CaringController.php` (line 48), `backend/app/Http/Controllers/NurseController.php` (line 41)
- **Symptom:** Models are instantiated using uncapitalized identifiers (`new caring()`).
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  $caring = new caring();
  ```
- **Ref:** `backend/app/Http/Controllers/CaringController.php`
- **Fix:** Capitalize class names to conform to standards: `new Caring()`, `new Nurse()`.
- **Root cause:** Typographical inconsistency relying on PHP's case-insensitive class name resolution.

---

### M4 — Duplicated business logic between Web and API controllers

- **Severity:** Architectural (doubles maintenance footprint for features and fixes)
- **Files:** `backend/app/Http/Controllers/CaringController.php` & `Api/CaringController.php`
- **Symptom:** Logic for operations, notifications, and updates is duplicated across web and API controllers.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  // Web CaringController.php and Api/CaringController.php contain independent duplicates of the same query/store logic.
  ```
- **Ref:** `backend/app/Http/Controllers/CaringController.php`
- **Fix:** Extract business domain actions into specialized Service classes.
- **Root cause:** Rapid feature prototyping without architectural separation between HTTP entry points and core domain services.

---

### M5 — Wildcard dependencies and pre-release packages in manifest files

- **Severity:** Low (build instability risk over time)
- **Files:** `backend/composer.json`, `android/app/build.gradle`
- **Symptom:** Composer dependencies set to `*` (`tymon/jwt-auth: *`), and Android project relies on alpha/beta library releases.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```json
  "tymon/jwt-auth": "*"
  ```
- **Ref:** `backend/composer.json`
- **Fix:** Pin Composer package ranges explicitly and update Android gradle dependencies to stable versions.
- **Root cause:** Loose dependency specification during initial project configuration.

---

## Low

### L1 — General code quality and environment issues

- **Severity:** Low
- **Files:** Various files across backend and mobile components
- **Symptom:** Casing inconsistencies in model instantiations, magic strings (`'Finished'`), missing project `CHANGELOG.md`.
- **Status:** Status as of 2026-08-26: documented only. Not fixed.
- **Original code:**
  ```php
  if ($caring->status == 'Finished') { ... }
  ```
- **Ref:** Various backend controllers and seeders
- **Fix:** Refactor magic constants into Class Enums/Constants, apply standard coding style guides.
- **Root cause:** Accumulation of minor tech debt during implementation.

---

## Won't fix (intentional, for the lessons)

- **700-line `DataRepository` god-class** — Demonstrates why single classes shouldn't combine state, cache, remote sync, and auth.
- **Duplicate web/API controllers per resource** — Demonstrates costs of copy-paste architecture; see M4.
- **Seeded weak passwords (`123456789`)** — Retained so trainees can easily log in after DB seeding operations.
- **Hard-coded `example.com` branding** — Retained as artifact of original learning project structure.
- **`ApiResponseTrait` empty wrapper** — Retained to highlight premature abstraction anti-patterns; see B5.
- **`Thread.sleep(4000)` in `AppDatabase` creation callback** — Retained as instance of cargo-cult blocking code in database initialization.
- **Seeded `testname` row in `AppDatabase` callback** — Retained to show silent initial data mutation side-effects.

---

## Build & Toolchain

### BTL1 — `composer install` fails due to security advisory policy blocking Laravel 10

- **Severity:** High (blocks repository setup)
- **Files:** `backend/composer.json`
- **Symptom:** Running `composer install` fails with security advisory warnings (`PKSA-...`) and refuses package installations.
- **Status:** Status as of 2026-08-26: fixed.
- **Original code:**
  ```json
  "config": {
      // Missing audit advisories configuration
  }
  ```
- **Ref:** `backend/composer.json`
- **Fix:** Add an explicit policy block to `backend/composer.json`:
  ```json
  "config": {
      "policy": {
          "advisories": {
              "block": false
          }
      }
  }
  ```
- **Root cause:** Composer 2.7+ introduced automated security advisory blocking rules that reject unpatched dependency trees.

---

### BTL2 — `composer.lock` and `package-lock.json` disabled by `.bak` extension

- **Severity:** High (blocks reproducible dependency setup)
- **Files:** `backend/composer.lock`, `backend/package-lock.json`
- **Symptom:** Package managers report no lock file available, installing non-deterministic dependency versions.
- **Status:** Status as of 2026-08-26: fixed.
- **Original code:**
  ```
  backend/composer.lock.bak
  backend/package-lock.json.bak
  ```
- **Ref:** `backend/composer.lock.bak`
- **Fix:** Rename backup extensions back to standard lock file targets (`mv composer.lock.bak composer.lock`).
- **Root cause:** Lock files were manually renamed during prior environment debugging, severing package pinning.

---

### BTL3 — `composer install` fails under PHP 8.4 due to rigid package constraints

- **Severity:** High (blocks installation on PHP 8.4+)
- **Files:** `backend/composer.json`
- **Symptom:** Installation fails with errors stating dependencies require `php ~8.1.0 || ~8.2.0`.
- **Status:** Status as of 2026-08-26: fixed via installation flag.
- **Original code:**
  ```bash
  composer install
  ```
- **Ref:** `backend/composer.json`
- **Fix:** Run setup using the platform-requirement bypass flag:
  ```bash
  composer install --no-interaction --ignore-platform-req=php
  ```
- **Root cause:** Dependencies pinned in the 2023 lock file enforce strict PHP upper bounds that predate PHP 8.4 releases.

---

### BTL4 — PHP 8.4 implicit nullable parameter deprecation notices

- **Severity:** Low (log warnings only)
- **Files:** `vendor/guzzlehttp/promises/src/functions.php`, `vendor/monolog/monolog/src/Monolog/Logger.php`
- **Symptom:** Execution logs deprecation notices regarding parameter type definitions: `Implicitly marking parameter $param as nullable is deprecated`.
- **Status:** Status as of 2026-08-26: documented only.
- **Original code:**
  ```php
  function queue($assign = null) // ❌ implicit nullable syntax in vendor code
  ```
- **Ref:** `vendor/guzzlehttp/promises/src/functions.php`
- **Fix:** Upgrade locked vendor package versions to modern releases supporting PHP 8.4 syntax rules.
- **Root cause:** Deprecation of implicit nullable parameters introduced in PHP 8.4 runtime engine.

---

### BTL5 — Corrupted launcher PNG assets breaking Android AAPT resource compilation

- **Severity:** High (blocks Android build process)
- **Files:** `android/app/src/main/res/mipmap-hdpi/ic_launcher.png`, `android/app/src/main/res/mipmap-mdpi/ic_launcher_round.png`
- **Symptom:** AAPT compilation fails with `AAPT: error: file failed to compile`.
- **Status:** Status as of 2026-08-27: fixed.
- **Original code:**
  ```
  Corrupted binary contents lacking standard PNG header signatures.
  ```
- **Ref:** `android/app/src/main/res/mipmap-hdpi/ic_launcher.png`
- **Fix:** Regenerate valid binary PNG launcher icons replacing corrupted files.
- **Root cause:** Binary file corruption occurring during file copy operations across file systems or archive extraction.

---

### BTL6 — Invalid Firebase API key format in placeholder config causes boot crashes

- **Severity:** High (app crashes immediately upon execution)
- **Files:** `android/app/google-services.json.example`
- **Symptom:** App process throws `IllegalArgumentException: Please set a valid API key` inside `FirebaseInitProvider`.
- **Status:** Status as of 2026-08-27: fixed.
- **Original code:**
  ```json
  "current_key": "YOUR-FIREBASE-API-KEY-HERE"
  ```
- **Ref:** `android/app/google-services.json.example`
- **Fix:** Replace placeholder with format-valid dummy key string: `AIzaSyDUMMY0000000000000000000000000000`.
- **Root cause:** Firebase SDK executes internal regular expression format checks against configuration keys during content provider initialization.

---

### BTL7 — Incomplete pre-commit sanitization leaking sensitive tokens and personal data

- **Severity:** High (security and privacy compliance leak)
- **Files:** `scripts/sanitize.sh`, `backend/database/seeders/*.php`, `android/app/google-services.json.example`
- **Symptom:** Personal emails, author paths, and service provider API credentials remain hardcoded in committed repository paths.
- **Status:** Status as of 2026-08-27: fixed via automated sanitization script.
- **Original code:**
  ```php
  // Hardcoded real email addresses, local paths ("D:\..."), and production secrets in code.
  ```
- **Ref:** `scripts/sanitize.sh`
- **Fix:** Execute `scripts/sanitize.sh` to apply single-source mapping table substitutions prior to committing code.
- **Root cause:** Lack of automated repository credential scanning and scrubbing workflows prior to public source publishing.

---

### BTL8 — CI/CD testing and deployment automation missing from project

- **Severity:** Medium (manual deployments increase regression risks)
- **Files:** `.github/workflows/backend.yml`
- **Symptom:** Lack of automated continuous integration checks when pushing changes to primary branches.
- **Status:** Status as of 2026-08-27: fixed.
- **Original code:**
  ```yaml
  # No workflow definitions present in .github/workflows/
  ```
- **Ref:** `.github/workflows/backend.yml`
- **Fix:** Add GitHub Actions workflow configuration to run tests and deploy changes automatically on push.
- **Root cause:** Project setup lacked pipeline workflow definitions.

---

### BTL9 — Serverless database configuration values hardcoded in environment manifests

- **Severity:** Medium (credential leak risk in cloud deployments)
- **Files:** `backend/wasmer.toml`, `backend/app.yaml`
- **Symptom:** Database access parameters exposed directly inside committed deployment manifests.
- **Status:** Status as of 2026-09-08: fixed.
- **Original code:**
  ```yaml
  # DB credentials stored inline in deployment descriptor configuration
  ```
- **Ref:** `backend/wasmer.toml`
- **Fix:** Inject database connection parameters using deployment target secrets managers (`wasmer app secret create`).
- **Root cause:** Platform template configuration defaults included embedded connection strings.

---

### BTL10 — Application environment variables shadowing platform injected database configuration

- **Severity:** Critical (production requests fail with database connection errors)
- **Files:** `backend/.env`, `backend/app.yaml`
- **Symptom:** Login returns HTTP 500 with `SQLSTATE[HY000] [2002] Operation not permitted` due to empty database connection parameters.
- **Status:** Status as of 2026-09-11: fixed.
- **Original code:**
  ```dotenv
  DB_HOST="${DB_HOST}" # Uploaded directly, resolving to empty values over platform defaults
  ```
- **Ref:** `backend/.env`
- **Fix:** Strip self-referential variable placeholders from production `.env` files uploaded to target platforms.
- **Root cause:** Application level environment variables take precedence over platform runtime capabilities, overriding host bindings with empty values.

---

### BTL11 — Pre-deployment migration job failures deadlocking release pipeline

- **Severity:** Critical (deployments fail while database migrations are blocked)
- **Files:** `backend/app.yaml`, `.github/workflows/backend.yml`
- **Symptom:** Application reports successful deployments while target database schema fails to execute pending migrations.
- **Status:** Status as of 2026-09-11: fixed.
- **Original code:**
  ```yaml
  # app.yaml using invalid pre-deployment execution command references
  ```
- **Ref:** `backend/app.yaml`
- **Fix:** Move migration steps into explicit CI post-deployment workflow jobs.
- **Root cause:** Pre-deployment execution jobs failing under WASI runtimes prevent new deployment code containing fixes from being published.

---

### BTL12 — Wasmer CLI silently drops job declarations during spec compilation

- **Severity:** Critical (database schema updates never execute)
- **Files:** `backend/app.yaml`, `.github/workflows/backend.yml`
- **Symptom:** Declared deployment jobs exist in local YAML manifests but disappear from compiled application specs.
- **Status:** Status as of 2026-09-11: fixed.
- **Original code:**
  ```yaml
  jobs:
    - name: migrate-on-deploy
      # Dropped silently by CLI compiler
  ```
- **Ref:** `backend/app.yaml`
- **Fix:** Run migration and seed tasks directly from external CI runners via remote database connections.
- **Root cause:** Platform CLI tooling drops job configuration blocks during conversion to binary specification formats.

---

### BTL13 — Managed MySQL rejects table creation due to strict primary key rules

- **Severity:** Critical (migrations fail on table creation)
- **Files:** `backend/database/migrations/2014_10_12_100000_create_password_reset_tokens_table.php`
- **Symptom:** Migration fails with `General error: 3750 Unable to create or change a table without a primary key`.
- **Status:** Status as of 2026-09-09: fixed.
- **Original code:**
  ```php
  Schema::create('password_reset_tokens', function (Blueprint $table) {
      $table->string('email');
      $table->string('token');
      $table->timestamp('created_at')->nullable();
      $table->primary('email'); // ❌ Compiled as separate ALTER TABLE statement
  });
  ```
- **Ref:** `backend/database/migrations/2014_10_12_100000_create_password_reset_tokens_table.php`
- **Fix:** Use driver-aware raw SQL statements to define primary keys inline during table creation for MySQL targets.
- **Root cause:** Laravel query grammar splits primary key additions into secondary `ALTER TABLE` statements, violating `sql_require_primary_key` rules.

---

### BTL14 — Declaring multiple entrypoint commands in `wasmer.toml` causes worker boot crashes

- **Severity:** Critical (entire application returns HTTP 500 errors)
- **Files:** `backend/wasmer.toml`
- **Symptom:** Platform returns generic HTTP 500 pages with header `x-edge-request-outcome: workload_failure`.
- **Status:** Status as of 2026-09-09: fixed.
- **Original code:**
  ```toml
  [[command]]
  name = "run"
  # ...
  [[command]]
  name = "artisan"
  # ❌ Multiple commands create ambiguous entrypoints
  ```
- **Ref:** `backend/wasmer.toml`
- **Fix:** Restrict `wasmer.toml` to a single default `[[command]]` entrypoint block.
- **Root cause:** Wasm runtime engine cannot determine default execution commands when multiple entrypoints are configured without explicit selectors.
