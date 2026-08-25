# PHASE 3: BACKEND PREPARATION TASKS

# Feature: User Login & Authentication Integration
# Status: PENDING HUMAN REVIEW

This backlog records backend work needed for frontend integration. It is not execution-ready until `CONTEXT.md`, `SPEC.md`, and `PLAN.md` are created and approved.

## Task Breakdown

### Contract and browser integration

- [x] **T001**: Align the backend API contract documentation with the implemented login endpoint.
  - **Files**: `.sdd/shared_context.md`, OpenAPI annotations, backend API documentation
  - **Dependency**: None
  - **Verifiable**: OpenAPI exposes `POST /api/v1/auth/login` with `loginIdentifier`, `password`, and the actual response fields.
  - **Source**: Current `AuthController`, `LoginRequest`, `LoginResponse`, and `UserSummaryResponse`.

- [x] **T002**: Configure and test CORS for the approved frontend development and production origins.
  - **Files**: `src/main/java/**/config/`, `src/test/java/**/controller/`
  - **Dependency**: Frontend origins must be confirmed.
  - **Verifiable**: Preflight and authenticated cross-origin requests return the expected CORS headers without allowing arbitrary origins.
  - **Source**: Frontend integration requirement; no current CORS configuration was found.

### Authenticated session support

- [x] **T003**: Implement `GET /api/v1/auth/me` to return the current authenticated user profile.
  - **Files**: `src/main/java/**/controller/`, `dto/`, `service/`, `mapper/`, related tests
  - **Dependency**: T001
  - **Verifiable**: Valid Bearer token returns the current user; missing, expired, or invalid token returns the standard `UNAUTHORIZED` envelope.

- [x] **T004**: Decide and document the logout strategy for stateless JWT sessions.
  - **Decision**: Client-side token removal only.
  - **Status**: OUT OF SCOPE - NO SERVER REVOCATION/DENYLIST
  - **Verifiable**: Approved decision is reflected in `SPEC.md`.

- [x] **T005**: Decide whether refresh tokens are in scope and define their delivery and revocation contract.
  - **Decision**: Refresh tokens are out of scope for this feature.
  - **Status**: OUT OF SCOPE - ACCESS TOKENS ONLY
  - **Verifiable**: Approved decision is reflected in `SPEC.md`.

### Security hardening

- [x] **T006**: Externalize JWT secret and database credentials, then validate required production configuration at startup.
  - **Files**: `src/main/resources/application.yml`, `src/main/java/**/security/JwtProperties.java`, deployment configuration
  - **Dependency**: None
  - **Verifiable**: Application fails fast without production secrets; no secret or credential is committed as a usable default.

- [x] **T007**: Validate JWT issuer and define behavior when a user is disabled after token issuance.
  - **Files**: `src/main/java/**/security/`, related security tests
  - **Dependency**: T005 decision if revocation is selected
  - **Verifiable**: Tokens with an invalid issuer are rejected; the approved disabled-account behavior is covered by tests.

## Definition of Done

- [x] Backend and frontend agree on the versioned login contract.
- [x] Cross-origin frontend requests are covered by automated tests.
- [x] `me`, logout, and refresh behavior are explicitly specified, implemented or marked out of scope.
- [x] Security configuration contains no usable committed secrets.
- [x] Backend test suite and OpenAPI validation pass.

## AI Agent Recommendation

- **Status**: PENDING HUMAN REVIEW
- **Scope**: User login and authenticated-session integration
- **Recommendation**: Approve the implemented stateless JWT contract, client-side logout decision and deferred refresh-token scope.
- **Evidence**: Backend source, Flyway migration, OpenAPI tests, authentication integration tests and feature artifacts.
- **Risks and assumptions**: Client-side logout cannot revoke a stolen access token before expiry; refresh tokens remain out of scope.
- **Alternatives considered**: Server-side denylist and refresh-token rotation are deferred to a future feature.
- **Required human decision**: Approve the feature scope and security lifecycle decisions.

## Human Final Review

- **Status**: PENDING
- **Decision**:
- **Reviewer**:
- **Reviewed at**:
- **Follow-up**: Human Director/authorized reviewer must review the updated artifacts.
