# Legal Aid — User Auth Service

REST API microservice for user authentication and profile management. Part of the **Legal Aid** platform.

---

## Tech Stack

| Layer        | Technology                        |
|--------------|-----------------------------------|
| Language     | Java 21                           |
| Framework    | Spring Boot 3.2                   |
| Security     | Spring Security 6 + JWT (jjwt)    |
| Password     | BCrypt (cost factor 12)           |
| Database     | PostgreSQL (JPA / Hibernate)      |
| Build        | Maven                             |

---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+
- PostgreSQL 15+ with the `legal_aid` database and schema already applied

### Environment Variables

| Variable                  | Default                        | Description                     |
|---------------------------|--------------------------------|---------------------------------|
| `DB_USERNAME`             | `postgres`                     | PostgreSQL username              |
| `DB_PASSWORD`             | `postgres`                     | PostgreSQL password              |
| `JWT_SECRET`              | *(long default in yml)*        | HMAC-SHA256 signing key (≥256 b)|

> **Production**: always set `JWT_SECRET` to a securely generated random value.
> Generate one with: `openssl rand -base64 64`

### Run locally

```bash
# 1 — clone / enter directory
cd legal-aid-user-auth

# 2 — set env vars (or export them in your shell)
export DB_USERNAME=postgres
export DB_PASSWORD=yourpassword
export JWT_SECRET=$(openssl rand -base64 64)

# 3 — build & run
mvn spring-boot:run
```

The service starts on **`http://localhost:8081/api/v1`**.

---

## API Reference

All paths are relative to `/api/v1`.

### Public Endpoints

#### `POST /auth/register`
Register a new user. Returns a token pair so the user is immediately logged in.

**Request body**
```json
{
  "fullName": "Jane Doe",
  "username": "janedoe",
  "email": "jane@example.com",
  "password": "SecurePass1!",
  "phone": "+880123456789",
  "dateOfBirth": "1990-06-15",
  "preferredLanguage": "en",
  "gender": "Male",
  "roles": ["CLIENT"]
}
```
> `roles` is optional — defaults to `["CLIENT"]` if omitted.

**Response `201 Created`**
```json
{
  "accessToken": "eyJ...",
  "refreshToken": "550e8400-e29b-41d4-...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": { ... }
}
```

---

#### `POST /auth/login`
Authenticate with email + password.

**Request body**
```json
{
  "email": "jane@example.com",
  "password": "SecurePass1!"
}
```

**Response `200 OK`** — same shape as register.

---

#### `POST /auth/refresh`
Exchange a refresh token for a **new** token pair. The used token is immediately revoked (rotation). Reuse of a revoked token triggers revocation of **all** sessions (reuse-detection).

**Request body**
```json
{
  "refreshToken": "550e8400-e29b-41d4-..."
}
```

**Response `200 OK`** — same shape as register.

---

### Protected Endpoints
Include the header: `Authorization: Bearer <accessToken>`

#### `GET /auth/me`
Fetch the authenticated user's own profile.

**Response `200 OK`**
```json
{
  "id": "uuid",
  "fullName": "Jane Doe",
  "username": "janedoe",
  "email": "jane@example.com",
  "phone": "+880123456789",
  "profilePicUrl": null,
  "dateOfBirth": "1990-06-15",
  "preferredLanguage": "en",
  "isVisible": false,
  "isActive": true,
  "roles": ["CLIENT"],
  "createdAt": "2025-01-01T10:00:00Z",
  "updatedAt": "2025-01-01T10:00:00Z"
}
```

---

#### `PATCH /auth/me`
Update the authenticated user's own profile. All fields are optional (PATCH semantics — only non-null fields are applied).

**Request body**
```json
{
  "fullName": "Jane Smith",
  "phone": "+880999999999",
  "profilePicUrl": "https://cdn.example.com/avatar.jpg",
  "preferredLanguage": "bn",
  "isVisible": true
}
```

**Response `200 OK`** — returns updated `UserResponse`.

---

### Client Endpoints (`CLIENT` role)
Include the header: `Authorization: Bearer <accessToken>`

#### `POST /auth/client/profile`
Create a client profile for the authenticated client user.

**Request body**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "emergencyContactName": "John Doe",
  "emergencyContactPhone": "+8801712345678",
  "notes": "Allergic to penicillin"
}
```

> `userId` is validated in the DTO. In the current controller implementation, backend resolves the authenticated user and sets `userId` server-side before service execution.

**Response `201 Created`**
```json
{
  "emergencyContactName": "John Doe",
  "emergencyContactPhone": "+8801712345678",
  "notes": "Allergic to penicillin",
  "createdAt": "2026-03-23T10:30:00Z"
}
```

---

#### `GET /auth/client/profile`
Fetch the authenticated client's profile.

**Response `200 OK`**
```json
{
  "emergencyContactName": "John Doe",
  "emergencyContactPhone": "+8801712345678",
  "notes": "Allergic to penicillin",
  "createdAt": "2026-03-23T10:30:00Z"
}
```

---

#### `PATCH /auth/client/profile`
Update authenticated client's profile fields. PATCH semantics apply (only non-null fields are updated).

**Request body**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "emergencyContactName": "Updated Name",
  "notes": "Updated private notes"
}
```

**Response `200 OK`**
```json
{
  "emergencyContactName": "Updated Name",
  "emergencyContactPhone": "+8801712345678",
  "notes": "Updated private notes",
  "createdAt": "2026-03-23T10:30:00Z"
}
```

---

## RBAC Summary

| Endpoint              | Roles Required          |
|-----------------------|-------------------------|
| `POST /auth/register` | Public                  |
| `POST /auth/login`    | Public                  |
| `POST /auth/refresh`  | Public                  |
| `GET  /auth/me`       | Any authenticated user  |
| `PATCH /auth/me`      | Any authenticated user  |
| `POST /auth/client/profile`     | `CLIENT` only           |
| `GET  /auth/client/profile`     | `CLIENT` only           |
| `PATCH /auth/client/profile`    | `CLIENT` only           |
| `GET  /admin/**`      | `ADMIN` only            |
| `GET  /lawyers/**`    | `LAWYER` or `ADMIN`     |

Use `@PreAuthorize("hasRole('ADMIN')")` on any controller method for fine-grained control.

---

## Token Lifecycle

```
Register / Login
      │
      ▼
  accessToken  ──── valid 15 min ────► use for all API calls
  refreshToken ──── valid 30 days ───► call POST /auth/refresh before expiry
                                              │
                                    old token revoked (rotation)
                                    new token pair issued
```

---

## Security Notes

- Passwords are hashed with **BCrypt cost 12** — never stored in plain text.
- Access tokens are **signed HS256 JWTs** containing the user's email and roles.
- Refresh tokens are **opaque UUIDs** stored in the database.
- On login, all existing refresh tokens for that user are revoked (single-session).
- **Token reuse detection**: presenting a revoked refresh token triggers revocation of all active sessions for that user.
- `is_visible = FALSE` by default (privacy-by-default).

---

## Project Structure

```
src/main/java/com/legalaid/userauth/
├── UserAuthApplication.java
├── config/
│   ├── JwtProperties.java          # Binds app.jwt.* from application.yml
│   └── SecurityConfig.java         # Spring Security + RBAC filter chain
├── controller/
│   ├── AuthController.java         # Auth endpoints
│   └── ClientController.java       # Client profile endpoints
├── dto/
│   ├── request/
│   │   ├── AuthRequests.java
│   │   └── client/ClientProfileRequest.java
│   └── response/
│       ├── AuthResponses.java
│       └── client/ClientProfileResponse.java
├── entity/
│   ├── User.java
│   ├── Role.java                   # CLIENT | LAWYER | ADMIN
│   ├── RefreshToken.java
│   └── client/
│       ├── ClientProfile.java
│       ├── ClientAddress.java
│       ├── ClientAddressId.java
│       └── LocationType.java
├── exception/
│   ├── AuthExceptions.java         # Domain exception classes
│   └── GlobalExceptionHandler.java # RFC 9457 ProblemDetail responses
├── repository/
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── RefreshTokenRepository.java
│   └── client/
│       ├── ClientProfileRepository.java
│       └── ClientAddressRepository.java
├── security/
│   ├── JwtService.java             # Token generation & validation
│   ├── JwtAuthenticationFilter.java
│   └── UserDetailsServiceImpl.java
└── service/
    ├── AuthService.java
    ├── impl/AuthServiceImpl.java
    └── client/
        ├── ClientService.java
        └── impl/ClientServiceImpl.java
```
