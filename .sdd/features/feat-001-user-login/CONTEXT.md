# Feature Context: User Login & Authentication Integration

## Scope

- Backend login endpoint is implemented at `POST /api/v1/auth/login`.
- Authenticated user lookup is implemented at `GET /api/v1/auth/me`.
- JWT sessions are stateless access tokens.
- CORS is restricted to approved frontend origins via configuration.

## Decisions Needed

- Logout behavior
- Refresh-token support

## Current Direction

- Logout is client-side token removal only.
- Refresh tokens are out of scope for this feature.
