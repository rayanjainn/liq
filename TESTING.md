# TESTING.md — Manual & Automated Testing Guide

> This guide covers how to test every endpoint in the CollabNex backend, both
> manually (via Postman / cURL) and via automated JUnit integration tests.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Start the Server](#2-start-the-server)
3. [Postman Collection (Recommended)](#3-postman-collection)
4. [Manual Testing — Step-by-Step (cURL)](#4-manual-testing)
5. [Automated Tests (JUnit)](#5-automated-tests)
6. [Test Matrix](#6-test-matrix)

---

## 1. Prerequisites

| Requirement | Version | Notes |
|-------------|---------|-------|
| Java | 21+ | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| MySQL | 8.0+ | Running on `localhost:3306` |
| Postman (optional) | Any | For the importable collection |

### Database Setup

```sql
CREATE DATABASE IF NOT EXISTS collabnex;
```

The app uses `ddl-auto: update` so tables are created automatically on first boot.

### Environment Variables (optional overrides)

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:mysql://localhost:3306/collabnex?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` | Database URL |
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | `root` | MySQL password |
| `JWT_SECRET` | (hardcoded default) | HMAC-SHA256 signing key |
| `JWT_EXPIRATION_MS` | `604800000` (7 days) | Token lifetime |
| `ADMIN_EMAIL` | `admin@collabnex.com` | Admin login email |
| `ADMIN_PASSWORD` | `changeme` | Admin login password |
| `UPLOAD_DIR` | `uploads` | Local file storage directory |

---

## 2. Start the Server

```bash
cd /Users/rayanjain/work/liq
mvn clean compile spring-boot:run
```

Wait until you see:
```
Started CollabNexApplication in X.XXX seconds
```

Server runs on `http://localhost:8080`.

---

## 3. Postman Collection

### Import

1. Open Postman → **Import** → **File**
2. Select: `postman/CollabNex_API.postman_collection.json`
3. The collection creates all variables automatically

### Run

- **Individual:** Click each request in order (01.1 → 01.2 → ... → 06.4)
- **Automated:** Click the collection name → **Run** → **Run CollabNex API**
  - This runs all 25+ requests sequentially, auto-extracting tokens/IDs
  - Each request has built-in test assertions

### Collection Structure

```
01 — Auth
  ├── 01.1  Register CLIENT           → 201 → saves client_token
  ├── 01.2  Register FREELANCER #1    → 201 → saves freelancer1_token
  ├── 01.3  Register FREELANCER #2    → 201
  ├── 01.4  Register FREELANCER #3    → 201
  ├── 01.5  Register FREELANCER #4    → 201
  ├── 01.6  Register ADMIN → FAIL    → 400 (blocked)
  ├── 01.7  Register DUPLICATE → FAIL → 409
  ├── 01.8  Login as CLIENT           → 200 → refreshes client_token
  ├── 01.9  Login as FREELANCER       → 200
  ├── 01.10 Login as ADMIN            → 200 → saves admin_token
  └── 01.11 Login WRONG password      → 400

02 — Client: Post Jobs
  ├── 02.1  Client posts Job #1       → 201 → saves job1_id
  ├── 02.2  Client posts Job #2       → 201 → saves job2_id
  └── 02.3  Client lists own jobs     → 200 → [2 jobs]

03 — Freelancer: View & Apply
  ├── 03.1  Freelancer views all jobs  → 200 → [2+ jobs]
  ├── 03.2  Freelancer #1 applies      → 200
  ├── 03.3  Freelancer #1 applies AGAIN → 409 (duplicate)
  ├── 03.4  Freelancer #2 applies      → 200
  ├── 03.5  Freelancer #3 applies      → 200
  └── 03.6  Freelancer #4 applies      → 200

04 — Admin: View & Shortlist
  ├── 04.1  Admin views all jobs       → 200 → [2 jobs]
  ├── 04.2  Admin views applications   → 200 → [4 applicants]
  ├── 04.3  Shortlist 2 IDs → FAIL     → 400 (too few)
  └── 04.4  Shortlist 3 IDs → SUCCESS  → 200

05 — Client: View Shortlist
  └── 05.1  Client views shortlisted   → 200 → [3 candidates]

06 — Role Guards (403 tests)
  ├── 06.1  Freelancer → /client/jobs   → 403
  ├── 06.2  Client → /admin/jobs        → 403
  ├── 06.3  Client → /freelancer/jobs   → 403
  └── 06.4  No JWT → any protected     → 401/403
```

---

## 4. Manual Testing — Step-by-Step (cURL)

> Run these commands **in order**. Each step depends on the output of previous steps.
> Replace `<TOKEN>` and `<ID>` placeholders with actual values from responses.

### Phase 1: Registration

#### 1.1 Register a Client
```bash
curl -s -X POST http://localhost:8080/auth/register \
  -F "name=Acme Corp" \
  -F "email=client@test.com" \
  -F "password=Password123!" \
  -F "role=CLIENT" \
  -F "phoneNumber=9876543210" | jq .
```
**Expected:** `201` — Save the `token` as `CLIENT_TOKEN` and `user.id` as `CLIENT_ID`.

#### 1.2 Register Freelancer #1
```bash
curl -s -X POST http://localhost:8080/auth/register \
  -F "name=Jane Developer" \
  -F "email=freelancer1@test.com" \
  -F "password=Password123!" \
  -F "role=FREELANCER" \
  -F "phoneNumber=1111111111" | jq .
```
**Expected:** `201` — Save the `token` as `F1_TOKEN` and `user.id` as `F1_ID`.

#### 1.3 Register Freelancer #2
```bash
curl -s -X POST http://localhost:8080/auth/register \
  -F "name=Bob Builder" \
  -F "email=freelancer2@test.com" \
  -F "password=Password123!" \
  -F "role=FREELANCER" \
  -F "phoneNumber=2222222222" | jq .
```
Save `token` as `F2_TOKEN`, `user.id` as `F2_ID`.

#### 1.4 Register Freelancer #3
```bash
curl -s -X POST http://localhost:8080/auth/register \
  -F "name=Carol Coder" \
  -F "email=freelancer3@test.com" \
  -F "password=Password123!" \
  -F "role=FREELANCER" \
  -F "phoneNumber=3333333333" | jq .
```
Save `F3_TOKEN`, `F3_ID`.

#### 1.5 Register Freelancer #4
```bash
curl -s -X POST http://localhost:8080/auth/register \
  -F "name=Dave Designer" \
  -F "email=freelancer4@test.com" \
  -F "password=Password123!" \
  -F "role=FREELANCER" \
  -F "phoneNumber=4444444444" | jq .
```
Save `F4_TOKEN`, `F4_ID`.

#### 1.6 ❌ Register as ADMIN — Should FAIL
```bash
curl -s -X POST http://localhost:8080/auth/register \
  -F "name=Hacker" \
  -F "email=hacker@test.com" \
  -F "password=Password123!" \
  -F "role=ADMIN" | jq .
```
**Expected:** `400` with `"Admin registration is not allowed"`.

#### 1.7 ❌ Register with duplicate email — Should FAIL
```bash
curl -s -X POST http://localhost:8080/auth/register \
  -F "name=Duplicate" \
  -F "email=client@test.com" \
  -F "password=Password123!" \
  -F "role=CLIENT" | jq .
```
**Expected:** `409` with `"Email already registered"`.

### Phase 2: Login

#### 2.1 Login as Client
```bash
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"client@test.com","password":"Password123!"}' | jq .
```
**Expected:** `200` — `role: "CLIENT"`. Save token as `CLIENT_TOKEN`.

#### 2.2 Login as Admin (default env credentials)
```bash
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@collabnex.com","password":"changeme"}' | jq .
```
**Expected:** `200` — `role: "ADMIN"`, `id: -1`. Save token as `ADMIN_TOKEN`.

#### 2.3 ❌ Login with wrong password
```bash
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"client@test.com","password":"wrongpassword"}' | jq .
```
**Expected:** `400` with `"Invalid credentials"`.

### Phase 3: Client Posts Jobs

#### 3.1 Create Job #1
```bash
curl -s -X POST http://localhost:8080/client/jobs \
  -H "Authorization: Bearer <CLIENT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"title":"Build a Landing Page","description":"Need a responsive React landing page"}' | jq .
```
**Expected:** `201` — Save `data.id` as `JOB1_ID`.

#### 3.2 Create Job #2
```bash
curl -s -X POST http://localhost:8080/client/jobs \
  -H "Authorization: Bearer <CLIENT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"title":"Mobile App Backend","description":"Spring Boot REST API for a fitness app"}' | jq .
```
Save `data.id` as `JOB2_ID`.

#### 3.3 List Client's Jobs
```bash
curl -s http://localhost:8080/client/jobs \
  -H "Authorization: Bearer <CLIENT_TOKEN>" | jq .
```
**Expected:** Array of 2 jobs.

### Phase 4: Freelancers Apply

#### 4.1 Freelancer #1 → Job #1
```bash
curl -s -X POST http://localhost:8080/freelancer/jobs/<JOB1_ID>/apply \
  -H "Authorization: Bearer <F1_TOKEN>" | jq .
```
**Expected:** `200`.

#### 4.2 ❌ Freelancer #1 → Job #1 AGAIN (duplicate)
```bash
curl -s -X POST http://localhost:8080/freelancer/jobs/<JOB1_ID>/apply \
  -H "Authorization: Bearer <F1_TOKEN>" | jq .
```
**Expected:** `409` — `"You have already applied for this job"`.

#### 4.3-4.5 Freelancers #2, #3, #4 → Job #1
```bash
curl -s -X POST http://localhost:8080/freelancer/jobs/<JOB1_ID>/apply \
  -H "Authorization: Bearer <F2_TOKEN>" | jq .

curl -s -X POST http://localhost:8080/freelancer/jobs/<JOB1_ID>/apply \
  -H "Authorization: Bearer <F3_TOKEN>" | jq .

curl -s -X POST http://localhost:8080/freelancer/jobs/<JOB1_ID>/apply \
  -H "Authorization: Bearer <F4_TOKEN>" | jq .
```

### Phase 5: Admin Reviews & Shortlists

#### 5.1 Admin views all jobs
```bash
curl -s http://localhost:8080/admin/jobs \
  -H "Authorization: Bearer <ADMIN_TOKEN>" | jq .
```
**Expected:** Array of 2 jobs.

#### 5.2 Admin views applications for Job #1
```bash
curl -s http://localhost:8080/admin/jobs/<JOB1_ID>/applications \
  -H "Authorization: Bearer <ADMIN_TOKEN>" | jq .
```
**Expected:** Array of 4 applicants.

#### 5.3 ❌ Admin shortlists only 2 (too few)
```bash
curl -s -X POST http://localhost:8080/admin/jobs/<JOB1_ID>/shortlist \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"freelancerIds":[<F1_ID>,<F2_ID>]}' | jq .
```
**Expected:** `400` — `"Must shortlist between 3 and 4 freelancers"`.

#### 5.4 ✅ Admin shortlists 3 freelancers
```bash
curl -s -X POST http://localhost:8080/admin/jobs/<JOB1_ID>/shortlist \
  -H "Authorization: Bearer <ADMIN_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"freelancerIds":[<F1_ID>,<F2_ID>,<F3_ID>]}' | jq .
```
**Expected:** `200` — `"Shortlist saved successfully."`.

### Phase 6: Client Views Shortlist

#### 6.1 Client views shortlisted candidates for Job #1
```bash
curl -s http://localhost:8080/client/jobs/<JOB1_ID>/shortlisted \
  -H "Authorization: Bearer <CLIENT_TOKEN>" | jq .
```
**Expected:** Array of 3 candidates with `name`, `email`, `phoneNumber`, `resumeUrl`.

### Phase 7: Role Guard Tests (all should get 403)

```bash
# Freelancer trying to access Client endpoint
curl -s -w "\nHTTP %{http_code}\n" -X POST http://localhost:8080/client/jobs \
  -H "Authorization: Bearer <F1_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"title":"Hacked!"}'

# Client trying to access Admin endpoint
curl -s -w "\nHTTP %{http_code}\n" http://localhost:8080/admin/jobs \
  -H "Authorization: Bearer <CLIENT_TOKEN>"

# Client trying to access Freelancer endpoint
curl -s -w "\nHTTP %{http_code}\n" http://localhost:8080/freelancer/jobs \
  -H "Authorization: Bearer <CLIENT_TOKEN>"

# No token at all
curl -s -w "\nHTTP %{http_code}\n" http://localhost:8080/client/jobs
```
**Expected:** All return `403 Forbidden` (or `401` for missing token).

### Phase 8: File Upload

```bash
# Upload a file
curl -s -X POST http://localhost:8080/api/upload/file \
  -H "Authorization: Bearer <CLIENT_TOKEN>" \
  -F "file=@/path/to/test.pdf" | jq .

# List files
curl -s http://localhost:8080/api/upload/files \
  -H "Authorization: Bearer <CLIENT_TOKEN>" | jq .
```

---

## 5. Automated Tests (JUnit)

### Run All Tests

```bash
mvn test
```

### Test Files

| File | What It Tests |
|------|---------------|
| `AuthControllerTest.java` | Register, login, admin login, duplicate email, wrong password, ADMIN self-register blocked |
| `JobFlowIntegrationTest.java` | Full end-to-end flow: register → post job → apply → shortlist → view | 
| `RoleGuardTest.java` | Cross-role access attempts (freelancer→client, client→admin, etc.) |

### Location

```
src/test/java/com/collabnex/
├── CollabNexApplicationTests.java     (context load)
├── AuthControllerTest.java            (auth endpoints)
├── JobFlowIntegrationTest.java        (full happy path)
└── RoleGuardTest.java                 (403 tests)
```

---

## 6. Test Matrix

| # | Test Case | Method | URL | Expected | Postman | JUnit |
|---|-----------|--------|-----|----------|---------|-------|
| 1 | Register Client | POST | /auth/register | 201 | 01.1 | ✅ |
| 2 | Register Freelancer | POST | /auth/register | 201 | 01.2-01.5 | ✅ |
| 3 | Block ADMIN register | POST | /auth/register | 400 | 01.6 | ✅ |
| 4 | Duplicate email | POST | /auth/register | 409 | 01.7 | ✅ |
| 5 | Login Client | POST | /auth/login | 200 | 01.8 | ✅ |
| 6 | Login Freelancer | POST | /auth/login | 200 | 01.9 | ✅ |
| 7 | Login Admin | POST | /auth/login | 200 | 01.10 | ✅ |
| 8 | Wrong password | POST | /auth/login | 400 | 01.11 | ✅ |
| 9 | Client posts job | POST | /client/jobs | 201 | 02.1 | ✅ |
| 10 | Client lists jobs | GET | /client/jobs | 200 | 02.3 | ✅ |
| 11 | Freelancer views jobs | GET | /freelancer/jobs | 200 | 03.1 | ✅ |
| 12 | Freelancer applies | POST | /freelancer/jobs/{id}/apply | 200 | 03.2 | ✅ |
| 13 | Duplicate application | POST | /freelancer/jobs/{id}/apply | 409 | 03.3 | ✅ |
| 14 | Admin views all jobs | GET | /admin/jobs | 200 | 04.1 | ✅ |
| 15 | Admin views applicants | GET | /admin/jobs/{id}/applications | 200 | 04.2 | ✅ |
| 16 | Shortlist too few | POST | /admin/jobs/{id}/shortlist | 400 | 04.3 | ✅ |
| 17 | Shortlist success | POST | /admin/jobs/{id}/shortlist | 200 | 04.4 | ✅ |
| 18 | Client views shortlist | GET | /client/jobs/{id}/shortlisted | 200 | 05.1 | ✅ |
| 19 | Freelancer → /client | POST | /client/jobs | 403 | 06.1 | ✅ |
| 20 | Client → /admin | GET | /admin/jobs | 403 | 06.2 | ✅ |
| 21 | Client → /freelancer | GET | /freelancer/jobs | 403 | 06.3 | ✅ |
| 22 | No JWT → protected | GET | /client/jobs | 401/403 | 06.4 | ✅ |
