# API Documentation — CollabNex Backend

**Base URL:** `http://localhost:8080`
**Auth Header:** `Authorization: Bearer <token>`
**Content-Type:** `application/json` (unless noted as `multipart/form-data`)

---

## 1. Authentication — `/auth` (Public)

### POST `/auth/register`

> Register a new CLIENT or FREELANCER user. ADMIN self-registration is forbidden.

**Content-Type:** `multipart/form-data`
**Access:** Public

**Request Fields:**

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `name` | string | ✅ | Display name |
| `email` | string | ✅ | Must be unique, valid email |
| `password` | string | ✅ | Plain text, hashed with BCrypt (strength 12) |
| `role` | string | ✅ | `CLIENT` or `FREELANCER` only |
| `phoneNumber` | string | ❌ | Optional phone number |
| `resume` | file | ❌ | PDF only, freelancers only |

**Success Response (201):**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "name": "Jane Doe",
      "email": "jane@example.com",
      "role": "FREELANCER"
    }
  }
}
```

**Error Responses:**

| Code | Condition |
|------|-----------|
| 400 | Missing required fields, invalid role, or role is ADMIN |
| 409 | Email already registered |

---

### POST `/auth/login`

> Authenticate by email and password. Checks admin env credentials first, then database.

**Access:** Public

**Request Body:**
```json
{
  "email": "jane@example.com",
  "password": "mypassword"
}
```

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "name": "Jane Doe",
      "email": "jane@example.com",
      "role": "FREELANCER"
    }
  }
}
```

**Admin Login:** Use the credentials configured in `ADMIN_EMAIL` / `ADMIN_PASSWORD` environment variables. Admin token will have `uid: -1` and `role: ADMIN`.

**Error Responses:**

| Code | Condition |
|------|-----------|
| 400 | Invalid credentials |
| 404 | User not found |

---

## 2. Freelancer — `/freelancer` (Role: FREELANCER)

### GET `/freelancer/jobs`

> View all available jobs. Sorted by paid-client status (paid first), then by newest.

**Access:** FREELANCER

**Success Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "Build a landing page",
      "description": "Need a responsive landing page...",
      "isPaidClient": true,
      "clientName": "Acme Corp",
      "createdAt": "2025-01-15T10:30:00"
    }
  ]
}
```

---

### POST `/freelancer/jobs/{jobId}/apply`

> Apply for a specific job. Each freelancer can only apply once per job.

**Access:** FREELANCER
**Path Variable:** `jobId` — the job ID

**Success Response (200):**
```json
{
  "success": true,
  "message": "Application submitted successfully."
}
```

**Error Responses:**

| Code | Condition |
|------|-----------|
| 404 | Job not found |
| 409 | Already applied to this job |

---

## 3. Client — `/client` (Role: CLIENT)

### POST `/client/jobs`

> Post a new job. Client ID is extracted from JWT. `isPaidClient` is auto-copied from the client's `isPaidMember` flag.

**Access:** CLIENT

**Request Body:**
```json
{
  "title": "Build a landing page",
  "description": "Need a responsive landing page with modern design..."
}
```

**Success Response (201):**
```json
{
  "success": true,
  "data": {
    "id": 5,
    "title": "Build a landing page",
    "description": "Need a responsive landing page with modern design...",
    "isPaidClient": false,
    "clientName": "John Smith",
    "createdAt": "2025-01-20T14:00:00"
  }
}
```

---

### GET `/client/jobs`

> List all jobs posted by the authenticated client. Ordered by newest first.

**Access:** CLIENT

**Success Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "id": 5,
      "title": "Build a landing page",
      "description": "...",
      "isPaidClient": false,
      "clientName": "John Smith",
      "createdAt": "2025-01-20T14:00:00"
    }
  ]
}
```

---

### GET `/client/jobs/{jobId}/shortlisted`

> View shortlisted candidates for a specific job. Only the job's owner can view this.

**Access:** CLIENT (must own the job)
**Path Variable:** `jobId` — the job ID

**Success Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "freelancerId": 7,
      "name": "Jane Doe",
      "email": "jane@example.com",
      "phoneNumber": "9876543210",
      "resumeUrl": "/uploads/resumes/7_1700000000_cv.pdf"
    }
  ]
}
```

**Error Responses:**

| Code | Condition |
|------|-----------|
| 400 | Client doesn't own this job |
| 404 | Job not found |

---

## 4. Admin — `/admin` (Role: ADMIN)

### GET `/admin/jobs`

> View all jobs from all clients. Includes client name. Sorted by paid-client first, then newest.

**Access:** ADMIN

**Success Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "Build a landing page",
      "description": "...",
      "isPaidClient": true,
      "clientName": "Acme Corp",
      "createdAt": "2025-01-15T10:30:00"
    }
  ]
}
```

---

### GET `/admin/jobs/{jobId}/applications`

> View all applicants for a specific job. Sorted by paid-member status (paid first), then by earliest application.

**Access:** ADMIN
**Path Variable:** `jobId` — the job ID

**Success Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "freelancerId": 7,
      "name": "Jane Doe",
      "email": "jane@example.com",
      "isPaidMember": true,
      "appliedAt": "2025-01-15T10:30:00"
    },
    {
      "freelancerId": 12,
      "name": "Bob Builder",
      "email": "bob@example.com",
      "isPaidMember": false,
      "appliedAt": "2025-01-16T09:00:00"
    }
  ]
}
```

---

### POST `/admin/jobs/{jobId}/shortlist`

> Save 3-4 shortlisted freelancer IDs for a job. **Replaces** any existing shortlist.

**Access:** ADMIN
**Path Variable:** `jobId` — the job ID

**Request Body:**
```json
{
  "freelancerIds": [3, 7, 12]
}
```

**Validation Rules:**
- Array length must be between 3 and 4
- All IDs must have an existing `JobApplication` for this job
- Returns `400` with descriptive message if validation fails

**Success Response (200):**
```json
{
  "success": true,
  "message": "Shortlist saved successfully."
}
```

**Error Responses:**

| Code | Condition |
|------|-----------|
| 400 | Too few/many IDs, or freelancer hasn't applied |
| 404 | Job not found |

---

## 5. File Upload — `/api/upload` (Authenticated)

### POST `/api/upload/file`

> Upload a file to local storage.

**Content-Type:** `multipart/form-data`
**Access:** Authenticated

**Allowed Types:** PDF, JPEG, PNG, WebP
**Max Size:** 10MB (configurable)

**Request:** `file` (multipart part)

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "fileName": "document.pdf",
    "fileType": "application/pdf",
    "fileUrl": "/uploads/files/1_1700000000_document.pdf"
  }
}
```

---

### GET `/api/upload/files`

> List all uploaded file metadata.

**Access:** Authenticated

---

### DELETE `/api/upload/files/{id}`

> Delete a file by database ID. Removes both file and metadata.

**Access:** Authenticated

---

## 6. Uploaded Files (Static)

### GET `/uploads/{path}`

> Serve uploaded files statically.

**Access:** Public (no auth required)

Example: `GET /uploads/resumes/7_1700000000_cv.pdf`

---

## 7. Existing Endpoints (Authenticated)

The following existing endpoints remain functional and require authentication:

| Prefix | Controller | Description |
|--------|------------|-------------|
| `/api/users` | UserController | User profile CRUD |
| `/api/projects` | ProjectController | Project management |
| `/api/project/files` | ProjectFileController | Project file management |
| `/api/project-vendor-mappings` | ProjectVendorMappingController | Project-vendor assignments |
| `/api/vendor` | VendorController | Vendor profile & capabilities |
| `/api/vendor/documents` | VendorDocumentController | Vendor document management |
| `/api/vendor/machineries` | VendorMachineryController | Vendor machinery management |
| `/api/vendor/metrics` | VendorMetricsController | Vendor KPIs/metrics |
| `/api/vendor/team` | VendorTeamController | Vendor team management |

---

## Error Response Format

All errors follow this consistent format:

```json
{
  "success": false,
  "message": "Descriptive error message",
  "data": null
}
```

For validation errors:
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Must be a valid email address",
    "password": "Password is required"
  }
}
```

---

## JWT Token Structure

**Header:** `Authorization: Bearer <token>`

**Payload Claims:**
```json
{
  "sub": "user@example.com",
  "role": "FREELANCER",
  "uid": 7,
  "iat": 1700000000,
  "exp": 1700604800
}
```

Admin tokens have `uid: -1` and `role: "ADMIN"`.

**Expiry:** Configurable via `JWT_EXPIRATION_MS` env var (default: 7 days / 604800000ms).
