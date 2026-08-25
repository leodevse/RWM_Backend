# Feature Plan: User Login & Authentication Integration

## Implementation Boundary

The feature follows the existing Spring layered architecture:

```text
AuthController -> AuthService -> UserRepository/LoginAuditRepository -> UserEntity/LoginAuditEntity
       |                 |
    DTOs/Mapper      Security/JWT services
```

- `controller/`: HTTP mapping, validation and response DTOs.
- `service/`: login and current-user business rules and transaction boundaries.
- `security/`: JWT creation, parsing, issuer/expiry validation and authentication filter.
- `repository/` and `entity/`: persistence only.
- `common/exception/`: centralized API and security error mapping.
- `config/`: Spring Security, CORS and OpenAPI configuration.

## Data and Security Flow

1. The controller validates `LoginRequest` and delegates to the service.
2. The service loads the user, verifies the encoded password and active status, records login audit data, and creates the response through the mapper.
3. The JWT service signs tokens with externally supplied configuration.
4. The authentication filter validates bearer tokens and loads the current user before protected requests reach the controller.
5. The `/me` service returns a DTO and never exposes a JPA entity directly.

## Completed

- Implement login contract and OpenAPI alignment.
- Configure CORS for approved frontend origins.
- Implement `GET /api/v1/auth/me`.
- Externalize secrets and validate JWT issuer.

## Session Lifecycle Decision

- Logout: client-side token removal only.
- Refresh tokens: out of scope.

## Verification

- Backend tests cover login, `me`, CORS, issuer validation, and disabled-user handling.
- OpenAPI documents the implemented endpoints.

## Verification and Risks

- Verification command: `./mvnw test` or `.\mvnw.cmd test` on Windows.
- API/security changes require controller and security integration tests.
- Login audit persistence requires repository/integration coverage.
- Stateless logout cannot revoke a stolen access token before expiry; refresh tokens and server-side revocation remain out of scope.

## AI Agent Recommendation

- **Status**: PENDING HUMAN REVIEW
- **Scope**: Spring Boot implementation plan for authentication
- **Recommendation**: Approve the existing layered implementation and stateless token lifecycle.
- **Evidence**: Current package structure, security configuration, JWT services, Flyway migration and test classes.
- **Risks and assumptions**: Token lifetime, issuer and allowed CORS origins remain deployment configuration; client-side logout is not server revocation.
- **Alternatives considered**: A separate domain/infra Clean Architecture layout was rejected because it does not match the current repository.
- **Required human decision**: Approve the implementation boundary and deferred refresh/revocation scope.

## Human Final Review

- **Status**: PENDING
- **Decision**:
- **Reviewer**:
- **Reviewed at**:
- **Follow-up**:
