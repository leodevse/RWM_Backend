# PHASE 3: BACKEND PREPARATION TASKS

# Feature: Warehouse Dashboard APIs
# Status: PREPARATION

This backlog is based on the current frontend dashboard placeholder. It is not execution-ready until a business-approved `CONTEXT.md`, `SPEC.md`, and `PLAN.md` exist.

## Task Breakdown

- [ ] **T001**: Define the warehouse summary contract for dashboard capacity and utilization.
  - **Files**: `.sdd/shared_context.md`, `src/main/java/**/controller/`, `dto/`, `service/`
  - **Dependency**: User authorization scope must be approved.
  - **Verifiable**: Contract defines fields, units, pagination/filter rules, and role access before implementation.

- [ ] **T002**: Implement the warehouse list and capacity summary API.
  - **Files**: `src/main/java/**/controller/`, `dto/`, `service/`, `repository/`, persistence migration if required
  - **Dependency**: T001
  - **Verifiable**: Integration tests cover authorized access, empty results, and capacity values.

- [ ] **T003**: Define and implement the inventory summary API used by the dashboard.
  - **Files**: `src/main/java/**/controller/`, `dto/`, `service/`, `repository/`
  - **Dependency**: T001
  - **Verifiable**: Tests cover total stock, unit consistency, warehouse filtering, and role access.

- [ ] **T004**: Define and implement the recent inbound/outbound movements API.
  - **Files**: `src/main/java/**/controller/`, `dto/`, `service/`, `repository/`
  - **Dependency**: T001
  - **Verifiable**: Tests cover ordering, pagination/limit, movement type, and empty results.

- [ ] **T005**: Define and implement the report metrics API required by the dashboard.
  - **Files**: `src/main/java/**/controller/`, `dto/`, `service/`, `repository/`
  - **Dependency**: T001
  - **Verifiable**: Tests cover date range, aggregation rules, and authorization.

- [ ] **T006**: Add OpenAPI documentation and end-to-end tests for all dashboard contracts.
  - **Files**: OpenAPI annotations, `src/test/java/**/controller/`, `.sdd/shared_context.md`
  - **Dependency**: T002, T003, T004, T005
  - **Verifiable**: OpenAPI and integration tests pass against the approved response schemas.

## Definition of Done

- [ ] Business definitions for warehouse, inventory, movements, and reports are approved.
- [ ] API contracts include units, filters, pagination, empty states, errors, and role access.
- [ ] Integration tests pass with realistic persisted data.
- [ ] Frontend can replace dashboard placeholder data without guessing response fields.

## AI Agent Recommendation

- **Status**: PENDING HUMAN REVIEW
- **Recommendation**: Approve domain contracts before implementation; keep each dashboard API independently testable.
- **Risks**: Current frontend values are placeholders and must not be treated as backend business rules.
- **Required human decision**: Confirm dashboard metrics, units, date ranges, and access rules.

## Human Final Review

- **Status**: PENDING
- **Decision**:
- **Reviewer**:
- **Reviewed at**:
- **Follow-up**: