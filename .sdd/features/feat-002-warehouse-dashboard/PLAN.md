# Feature Plan: Warehouse Dashboard APIs

## Status

DRAFT - BLOCKED UNTIL SPEC APPROVAL

## Proposed Spring Boot Boundary

```text
controller -> service -> repository -> entity
```

- Controllers will expose approved DTOs and Bean Validation.
- Services will own dashboard aggregation and authorization decisions.
- Repositories will provide parameterized JPA queries and pagination.
- Entities and Flyway migrations will be created only after the domain contract is approved.
- Integration tests will verify security, response contract, empty results and persistence behavior.

## Planned Work Sequence

1. Approve `CONTEXT.md` business definitions.
2. Finalize `SPEC.md` endpoint, DTO, role, unit, date and error contracts.
3. Update this plan with concrete components, migrations, query strategy and risks.
4. Decompose approved work into executable `TASKS.md` items.
5. Implement and verify with Maven tests and OpenAPI checks.

## Risks

- Incorrect metric definitions can produce misleading inventory or capacity data.
- Aggregation queries may cause performance or N+1 issues if not designed against PostgreSQL.
- Report timezone and date-boundary mistakes can produce inconsistent totals.

## AI Agent Recommendation

- **Status**: PENDING HUMAN REVIEW
- **Scope**: Warehouse dashboard implementation approach
- **Recommendation**: Do not execute implementation tasks until the feature SPEC is concrete and approved.
- **Evidence**: Unresolved business questions in `CONTEXT.md`.
- **Risks and assumptions**: No schema or API shape is assumed by this draft.
- **Alternatives considered**: Starting from frontend placeholder fields was rejected.
- **Required human decision**: Approve the boundary and business contract before implementation planning is locked.

## Human Final Review

- **Status**: PENDING
- **Decision**:
- **Reviewer**:
- **Reviewed at**:
- **Follow-up**:
