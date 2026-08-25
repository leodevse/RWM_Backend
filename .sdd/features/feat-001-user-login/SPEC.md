# Feature Specification: User Login & Authentication Integration

## Authentication Model

### REQ-AUTH-001: Stateless access token

WHEN a user submits valid login credentials,
THE SYSTEM SHALL issue a signed stateless JWT access token containing the authenticated user identity and role.

### REQ-AUTH-002: Token validation

WHEN an authenticated request contains a bearer token,
THE SYSTEM SHALL validate its signature, issuer, expiration and current user status before allowing protected access.

## Endpoints

### `POST /api/v1/auth/login`

### REQ-AUTH-003: Login contract

WHEN `POST /api/v1/auth/login` receives `loginIdentifier` and `password`,
THE SYSTEM SHALL return `accessToken`, `tokenType`, `expiresAt` and `user` for valid credentials.

### REQ-AUTH-004: Invalid login

IF the login identifier or password is invalid,
THEN THE SYSTEM SHALL return the standard authentication error envelope without revealing which credential failed.

### `GET /api/v1/auth/me`

### REQ-AUTH-005: Current user

WHEN `GET /api/v1/auth/me` receives a valid `Authorization: Bearer <accessToken>` header,
THE SYSTEM SHALL return the current active user profile.

### REQ-AUTH-006: Rejected authenticated request

IF the token is missing, invalid, expired, has an unexpected issuer, or belongs to a disabled user,
THEN THE SYSTEM SHALL return the standard `UNAUTHORIZED` error envelope.

## Logout

- Logout is client-side token removal only.
- The backend does not maintain a token denylist or revocation store for this feature.
- The frontend should clear the token from storage and end the local session state.

## Refresh Tokens

- Refresh token support is out of scope for this feature.
- The system uses short-lived access tokens only.

## Security Constraints

- JWT secret and database credentials must come from environment variables in production.
- Approved frontend origins must be explicitly configured for CORS.
- Tokens issued by an unexpected issuer must be rejected.

## Out of Scope

- Server-side logout/revocation denylist.
- Refresh token issuance, rotation and revocation.

## AI Agent Recommendation

- **Status**: PENDING HUMAN REVIEW
- **Scope**: Authentication contract and security behavior
- **Recommendation**: Approve the stateless access-token contract and the explicit out-of-scope refresh/revocation decisions.
- **Evidence**: `AuthController`, `SecurityConfig`, JWT services, integration tests and `PLAN.md`.
- **Risks and assumptions**: Client-side logout cannot revoke a stolen access token before expiry; short token lifetime and account-status checks remain required.
- **Alternatives considered**: Server-side denylist and refresh-token rotation are deferred to a future feature.
- **Required human decision**: Approve the contract and security assumptions.

## Human Final Review

- **Status**: PENDING
- **Decision**:
- **Reviewer**:
- **Reviewed at**:
- **Follow-up**:
