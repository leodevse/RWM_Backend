# PHASE 3: BACKEND PREPARATION TASKS

# Feature: User Login & Authentication Integration
# Status: PREPARATION

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

- [ ] **T004**: Decide and document the logout strategy for stateless JWT sessions.
  - **Status**: BLOCKED - HUMAN SECURITY/PRODUCT DECISION
  - **Options**: Client-side token removal only, or server-side token revocation/denylist.
  - **Verifiable**: Approved decision is reflected in `SPEC.md` and has an integration test.

- [ ] **T005**: Decide whether refresh tokens are in scope and define their delivery and revocation contract.
  - **Status**: BLOCKED - HUMAN SECURITY/PRODUCT DECISION
  - **Verifiable**: Approved decision is reflected in `SPEC.md`; either a refresh endpoint is tested or the feature is explicitly out of scope.

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
- [ ] `me`, logout, and refresh behavior are explicitly specified, implemented or marked out of scope.
- [x] Security configuration contains no usable committed secrets.
- [x] Backend test suite and OpenAPI validation pass.

## AI Agent Recommendation

- **Status**: IMPLEMENTED WHERE UNBLOCKED
- **Recommendation**: Resolve T004-T005 before adding session lifecycle behavior.
- **Risks**: CORS origins, logout semantics, refresh-token scope, and post-issuance account disabling are not yet approved decisions.
- **Required human decision**: Approve the task scope and decide T004-T005.

## Human Final Review

- **Status**: PENDING
- **Decision**:
- **Reviewer**:
- **Reviewed at**:
- **Follow-up**:
