# CollabNex — Backend

![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot%203.3.4-brightgreen)
![Java](https://img.shields.io/badge/Language-Java%2021-orange)
![MySQL](https://img.shields.io/badge/Database-MySQL%208-blue)
![Security](https://img.shields.io/badge/Security-JWT%20Stateless-red)

CollabNex is a production-grade freelancer marketplace backend. It features a robust job posting system, role-based access control, and a prioritized feed for paid members.

---

## 📖 Navigation

| Document | Description |
|----------|-------------|
| [**API Documentation**](API_DOCS.md) | Full endpoint list with examples and cURL commands. |
| [**Testing Guide**](TESTING.md) | How to run integration tests and manual Postman flows. |
| [**Change Log**](CHANGES.md) | Detailed technical log of the latest refactoring and new features. |

---

## 🚀 Key Features

- **Multi-Role Support**: Specialized workflows for `CLIENT`, `FREELANCER`, and `ADMIN`.
- **Stateless Security**: JWT-based authentication with virtual admin support (no DB admin required).
- **Intelligent Job Feed**: Automatic prioritization of jobs from "Paid Clients" and applications from "Paid Freelancers".
- **Local Storage System**: Secure, disk-based file storage for resumes and project documents.
- **Validation & Handling**: Strict server-side validation and consistent global exception handling.

---

## 🛠 Tech Stack

- **Core**: Spring Boot 3.3.4, Java 21
- **Persistence**: Spring Data JPA, Hibernate 6
- **Security**: Spring Security 6, JJWT
- **Mapping**: MapStruct, Lombok
- **Database**: MySQL (Production), H2 (Local/Test)

---

## ⚙️ Getting Started

### Prerequisites

- Java 21
- Maven 3.8+
- MySQL 8.0 (Optional, can run with H2)

### Quick Start (In-Memory Database)

Run the app immediately using the H2 profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

### Production Start (MySQL)

1. Create the database:
   ```sql
   CREATE DATABASE collabnex;
   ```
2. Set your environment variables (or update `application.yml`):
   - `DB_PASSWORD=your_password`
   - `JWT_SECRET=your_secure_key`
3. Run:
   ```bash
   mvn spring-boot:run
   ```

---

## 📂 Project Structure

```text
com.collabnex
├── common          # DTOs, Exceptions, Shared Utilities
├── config          # Security, Storage, Web configurations
├── controller      # Admin, Client, Freelancer, Auth controllers
├── domain          # JPA Entities and Repositories (User, Job, etc.)
├── entity          # Utility Entities (UploadedFile)
├── security        # JWT Filter, JwtService, Encryption
└── service         # Business logic (JobService, AuthService, etc.)
```

---

## 🔒 Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `JWT_SECRET` | *(Random String)* | Secret key for JWT signing |
| `ADMIN_EMAIL` | `admin@collabnex.com` | Virtual admin login |
| `ADMIN_PASSWORD` | `changeme` | Virtual admin password |
| `UPLOAD_DIR` | `uploads` | Local directory for files |
