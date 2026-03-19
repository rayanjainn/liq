# CHANGES.md — CollabNex Backend Refactoring Log

> **Date:** 2026-03-19
> **Scope:** Remove S3, add local file storage, implement role-based access (CLIENT, FREELANCER, ADMIN), add Job/Application/Shortlist domain.

---

## Files Deleted

| File | Reason |
|------|--------|
| `config/S3Config.java` | AWS S3 configuration removed — replaced with `FileStorageConfig.java` for local disk storage. |
| `service/S3Service.java` | S3 upload/delete operations removed — replaced with `FileStorageServiceImpl.java`. |
| `service/S3FolderService.java` | S3 folder creation removed — no longer needed with local storage. |
| `controller/S3FolderController.java` | S3 folder API endpoint removed — no replacement needed. |
| `config/CorsConfig.java` | Redundant CORS config — consolidated into `WebConfig.java` which now handles both CORS and static resource serving. |
| `resources/application.properties` | Removed — all configuration unified into `application.yml` to avoid conflicts. Contained AWS-specific properties (`aws.region`, `aws.s3.bucket-name`). |

---

## pom.xml

**Before:** Contained `com.amazonaws:aws-java-sdk-s3:1.12.640` dependency.
**After:** AWS SDK dependency removed. All other dependencies (spring-boot-starter-security, spring-boot-starter-validation, jjwt-api/impl/jackson) confirmed present.
**Reason:** S3 is no longer used; local file storage requires only `java.nio.file` (built-in).

---

## application.yml

**Before:** Had `app.security.jwt.secret/issuer/expiration-minutes` structure, hardcoded datasource credentials, `show-sql: true`.
**After:** Restructured to:
- `app.jwt.secret` / `app.jwt.expiration-ms` (milliseconds, env-backed)
- `app.admin.email` / `app.admin.password` (env-backed admin credentials)
- `app.storage.upload-dir` (local file storage directory)
- `app.cors.allowed-origins` (env-backed CORS origins)
- `spring.servlet.multipart.max-file-size/max-request-size` (configurable)
- `spring.datasource.*` env-backed with defaults
- `show-sql: false` for production safety
**Reason:** Centralize all config, support 12-factor env-var overrides, remove hardcoded secrets.

---

## domain/user/UserRole.java

**Before:** `ADMIN, CLIENT, VENDOR`
**After:** `CLIENT, FREELANCER, ADMIN`
**Reason:** VENDOR role replaced with FREELANCER per new business model. Admin is env-only (not stored in DB).

---

## domain/user/User.java

**Before:** Had `id, email, passwordHash, role, status, createdAt, updatedAt`. Authorities returned via lambda.
**After:** Added fields: `name`, `isPaidMember`, `phoneNumber`, `resumeUrl`. Authorities now use `SimpleGrantedAuthority("ROLE_" + role.name())`. Full Javadoc added.
**Reason:** New fields required for job system (name display, resume for freelancers, paid member prioritization). `SimpleGrantedAuthority` integrates properly with `hasRole()` in SecurityConfig.

---

## domain/job/ (NEW package)

**Before:** Did not exist.
**After:** Contains:
- `Job.java` — Job posting entity with `title, description, client, isPaidClient, createdAt`
- `JobApplication.java` — Freelancer application with unique constraint on `(job_id, freelancer_id)`
- `ShortlistedCandidate.java` — Admin-selected candidates with unique constraint on `(job_id, freelancer_id)`
- `JobRepository.java` — Query methods for client jobs and freelancer feed ordering
- `JobApplicationRepository.java` — Duplicate check, sorted queries for admin view
- `ShortlistedCandidateRepository.java` — Find/delete by job ID
**Reason:** Core new feature — job posting, application, and admin shortlisting system.

---

## common/dto/ (NEW DTOs)

**Before:** Had vendor/project DTOs, `ApiResponse`, `UserProfileDto`.
**After:** Added:
- `AuthRequest.java` — Login request with `@Valid` annotations
- `RegisterRequest.java` — Registration with role, phoneNumber fields
- `JobDto.java` — Job listing response
- `JobApplicationDto.java` — Applicant info for admin view
- `ShortlistRequest.java` — Admin shortlist payload with `@Size(min=3, max=4)` validation
- `ShortlistedCandidateDto.java` — Candidate contact info for client view
- `UserSummaryDto.java` — Auth response user info
**Reason:** DTOs for all new endpoints; `@Valid` annotations for input validation.

---

## config/FileStorageConfig.java (NEW)

**Before:** Did not exist (`S3Config.java` existed instead).
**After:** Creates upload directory at startup. Exposes a `Path` bean for injection into `FileStorageServiceImpl`.
**Reason:** Replaces S3 configuration with local disk storage setup.

---

## config/WebConfig.java

**Before:** Only had CORS mappings with hardcoded origins.
**After:** Added `addResourceHandlers` to serve `/uploads/**` from local `file:uploads/` directory. CORS origins now read from `app.cors.allowed-origins` env property.
**Reason:** Serve uploaded files statically from disk; make CORS configurable.

---

## config/CustomUserDetails.java

**Before:** Wrapped `User` entity, returned empty authorities (roles intentionally disabled).
**After:** Now includes `name`, `role` fields. Has a second constructor for virtual admin user (not in DB). Returns proper `SimpleGrantedAuthority` with ROLE_ prefix. Full Javadoc.
**Reason:** Roles are now enforced; admin user needs a non-DB-backed principal.

---

## security/SecurityConfig.java

**Before:** Simple config — CSRF disabled, permitAll on `/api/upload/**` and `/api/s3/**`, all others authenticated. No JWT filter integration, no role rules, BCrypt without strength parameter.
**After:** Complete rewrite:
- `@EnableMethodSecurity(prePostEnabled = true)` for `@PreAuthorize` support
- Stateless session management (`SessionCreationPolicy.STATELESS`)
- Route-level role rules: `/auth/**` public, `/uploads/**` public, `/freelancer/**` FREELANCER, `/client/**` CLIENT, `/admin/**` ADMIN, everything else authenticated
- `JwtAuthFilter` inserted before `UsernamePasswordAuthenticationFilter`
- BCrypt encoder with strength 12
**Reason:** Full role-based access control as specified.

---

## security/JwtService.java

**Before:** Read from `app.security.jwt.secret/issuer/expiration-minutes`. Included issuer in token.
**After:** Reads from `app.jwt.secret` and `app.jwt.expiration-ms` (milliseconds). Issuer removed. Full Javadoc on all methods.
**Reason:** Simplified config path, switched to millisecond-based expiration for consistency.

---

## security/JwtAuthFilter.java

**Before:** Loaded user from DB via `UserService.loadUserByUsername()`, created auth token with the `User` entity directly.
**After:** Extracts `role` and `uid` from JWT claims. For admin tokens (`uid == -1`), constructs a virtual `CustomUserDetails` without DB lookup. For regular users, loads from DB and wraps in `CustomUserDetails`. Full Javadoc.
**Reason:** Admin doesn't exist in DB; `CustomUserDetails` provides proper authority mapping.

---

## security/AesEncryptionService.java

**Before:** No documentation.
**After:** Full Javadoc on all methods.
**Reason:** Documentation requirement.

---

## service/AuthService.java + impl/AuthServiceImpl.java (NEW)

**Before:** Auth logic was inline in `AuthController` (no separate service).
**After:** Dedicated service with:
- `register()` — Creates CLIENT or FREELANCER, blocks ADMIN registration, handles resume upload for freelancers
- `login()` — Checks admin env credentials (BCrypt-hashed at `@PostConstruct`), then falls back to DB user lookup with password verification
**Reason:** Proper separation of concerns; admin env-auth logic belongs in a service.

---

## service/JobService.java + impl/JobServiceImpl.java (NEW)

**Before:** Did not exist.
**After:** Full business logic for:
- `createJob()` — Client posts job, copies `isPaidMember` to `isPaidClient`
- `getJobsByClient()` — Client's own jobs
- `getAllJobsForFreelancer()` — All jobs sorted paid-first
- `getAllJobsForAdmin()` — All jobs with client info
- `applyForJob()` — Duplicate prevention via service check + DB constraint
- `getApplicationsForJob()` — Sorted paid-member-first for admin
- `shortlistCandidates()` — Validates 3-4 IDs, verifies all have applied, replaces existing shortlist
- `getShortlistedCandidates()` — Ownership check before returning contact info
**Reason:** Core new feature.

---

## service/FileStorageService.java + impl/FileStorageServiceImpl.java (NEW)

**Before:** Did not exist (`S3Service.java` existed instead).
**After:** Local disk storage service:
- `storeFile()` — Content-type validation (PDF, JPEG, PNG, WebP), filename sanitization, timestamp-based naming
- `deleteFile()` — Silent deletion
- `resolveFilePath()` — URL-to-path resolution
**Reason:** Replaces S3 storage entirely.

---

## service/UserService.java + impl/UserServiceImpl.java

**Before:** `registerLocal(fullName, email, rawPassword)` — hardcoded CLIENT role.
**After:** `register(name, email, rawPassword, role, phoneNumber, resumeUrl)` — accepts role, phone, resume. Creates both User and UserProfile. Full Javadoc.
**Reason:** Registration now supports role selection and additional fields.

---

## service/FileUploadService.java

**Before:** Depended on `S3Service` for file storage. Used `s3Key` for file deletion.
**After:** Depends on `FileStorageService` (local disk). Removed `s3Key` references. Full Javadoc.
**Reason:** S3 removal.

---

## entity/UploadedFile.java

**Before:** Had `s3Key` field, manual getters/setters.
**After:** Removed `s3Key` field (no longer needed). Added Lombok `@Getter/@Setter`, Javadoc.
**Reason:** S3 removal, code cleanup.

---

## controller/AuthController.java

**Before:** `/api/auth/*` paths, inline `RegisterRequest`/`LoginRequest` as inner classes, no password verification in login, no role in register.
**After:** `/auth/*` paths (no `/api` prefix for consistency with security rules), delegates to `AuthService`, multipart register endpoint supports resume upload, `@Valid` on login request.
**Reason:** Proper auth flow with admin support, file upload during registration.

---

## controller/FreelancerController.java (NEW)

**Before:** Did not exist.
**After:** `/freelancer/jobs` (GET — view all, sorted paid-first), `/freelancer/jobs/{jobId}/apply` (POST — apply with duplicate prevention).
**Reason:** Freelancer role-specific endpoints.

---

## controller/ClientController.java (NEW)

**Before:** Did not exist.
**After:** `/client/jobs` (POST — create, GET — list own), `/client/jobs/{jobId}/shortlisted` (GET — view shortlist with ownership check).
**Reason:** Client role-specific endpoints.

---

## controller/AdminController.java (NEW)

**Before:** Did not exist.
**After:** `/admin/jobs` (GET — all jobs), `/admin/jobs/{jobId}/applications` (GET — applicants sorted), `/admin/jobs/{jobId}/shortlist` (POST — save 3-4 candidates).
**Reason:** Admin role-specific endpoints.

---

## controller/FileUploadController.java

**Before:** Public endpoints, used `S3Service`, no auth, `@CrossOrigin` annotation.
**After:** Requires authentication, uses `FileStorageService`, returns `ApiResponse` wrapper, Javadoc on all methods.
**Reason:** Security enforcement, S3 removal.

---

## controller/VendorTeamController.java

**Before:** `@PreAuthorize("hasRole('VENDOR')")`, used `User` as `@AuthenticationPrincipal`.
**After:** Removed VENDOR role guard (role no longer exists), uses `CustomUserDetails`, Javadoc added.
**Reason:** VENDOR role replaced with FREELANCER; these endpoints now require basic authentication.

---

## controller/VendorDocumentController.java, VendorMetricsController.java

**Before:** Used `User` directly as `@AuthenticationPrincipal`.
**After:** Updated to use `CustomUserDetails`, Javadoc added to all methods.
**Reason:** Consistent principal type across all controllers.

---

## common/exception/GlobalExceptionHandler.java

**Before:** Basic handlers for `NotFoundException`, `BusinessException`, `MethodArgumentNotValidException`, and generic `Exception`.
**After:** Added `DataIntegrityViolationException` handler returning 409 Conflict (for DB unique constraint violations). `BusinessException` now returns 409 for "already"/"duplicate" messages. Validation errors return `success: false` (was incorrectly `success: true`). Full Javadoc.
**Reason:** Proper HTTP status codes for duplicate applications, consistent error format.

---

## .gitignore (NEW)

**Before:** Did not exist.
**After:** Ignores `target/`, `uploads/`, `.idea/`, `.DS_Store`, etc.
**Reason:** Standard practice; `uploads/` must not be committed.

---

## 🔒 Security Checklist Confirmation

| # | Requirement | Status |
|---|-------------|--------|
| 1 | Password hashing — BCrypt strength 12 | ✅ `SecurityConfig.passwordEncoder()` |
| 2 | JWT secret from env — `${JWT_SECRET}` | ✅ `application.yml` |
| 3 | JWT expiry configurable — `${JWT_EXPIRATION_MS}` | ✅ `application.yml` |
| 4 | Role enforcement — route rules + `@EnableMethodSecurity` | ✅ `SecurityConfig.filterChain()` |
| 5 | Input validation — `@Valid` + Bean Validation | ✅ All request DTOs |
| 6 | File type validation — server-side content-type check | ✅ `FileStorageServiceImpl.storeFile()` |
| 7 | Ownership check — client sees only own job's shortlist | ✅ `JobServiceImpl.getShortlistedCandidates()` |
| 8 | Duplicate application — UNIQUE constraint + service check → 409 | ✅ `JobApplication` entity + `JobServiceImpl.applyForJob()` |
| 9 | CORS — origins from `${ALLOWED_ORIGINS}` | ✅ `WebConfig.addCorsMappings()` |
| 10 | Global exception handling — consistent `ApiResponse` | ✅ `GlobalExceptionHandler` |
