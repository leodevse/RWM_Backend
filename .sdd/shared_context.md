# MULTI-AGENT SHARED CONTEXT & API CONTRACTS

# Version: 1.0.0
# Last-Updated: 2026-08-24 12:52 UTC
# Lead Agent: Orchestrator (@main-agent)

---

## 1. Active Agents & Bounded Context Ownership

| Agent Name | Role / Specialist | Ownership Boundary |
| :--- | :--- | :--- |
| **`@lead-architect`** | Plan & Contract Governance | `.sdd/`, `CONSTITUTION.md`, `CLAUDE.md` |
| **`@backend-agent`** | Usecase & Domain Developer | `src/domain/`, `src/usecase/` |
| **`@infra-agent`** | DB, Redis & Integration Dev | `src/infra/`, `tests/integration/` |
| **`@tester-agent`** | Verification & E2E Tester | `tests/unit/`, `tests/e2e/` |

---

## 2. Frozen API Contracts (Single Source of Truth)

### Authentication (verified from current backend)

| Method | Endpoint | Request | Success response | Auth |
|---|---|---|---|---|
| POST | `/api/v1/auth/login` | `{ loginIdentifier, password }` | `{ accessToken, tokenType, expiresAt, user }` | Public |
| GET | `/api/v1/auth/me` | none | `{ id, loginIdentifier, fullName, role }` | Bearer token |

`user` contains `id`, `loginIdentifier`, `fullName`, and `role`. Supported roles are `ADMIN` and `STAFF`. Protected requests use `Authorization: Bearer <accessToken>`.
JWTs are stateless access tokens. The backend validates signature, issuer, expiration, and the current active user record on authenticated requests. Accounts disabled after token issuance are rejected on authenticated requests.

Approved frontend origins are configured with `RWM_CORS_ALLOWED_ORIGINS`; local defaults are `http://localhost:5173` and `http://localhost:3000`. Production must provide `RWM_DB_URL`, `RWM_DB_USERNAME`, `RWM_DB_PASSWORD`, and `RWM_JWT_SECRET`.

### Pending contracts

- Logout and refresh-token contracts require a product/security decision before implementation.
- Warehouse, inventory, movement, and report endpoints require separate feature specifications.
