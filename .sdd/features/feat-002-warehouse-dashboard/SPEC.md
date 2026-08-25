# Feature Specification: Warehouse Dashboard APIs

## Status

DRAFT - NOT EXECUTION-READY

## Requirements

The following requirements are placeholders until the open business questions in `CONTEXT.md` are answered:

### REQ-WH-001: Warehouse summary

WHEN an authorized user requests warehouse summary data,
THE SYSTEM SHALL return approved capacity and utilization fields with explicit units and empty-state behavior.

### REQ-WH-002: Inventory summary

WHEN an authorized user requests inventory summary data,
THE SYSTEM SHALL return approved stock totals using the approved unit and warehouse filter semantics.

### REQ-WH-003: Movement summary

WHEN an authorized user requests recent movements,
THE SYSTEM SHALL return approved inbound and outbound records ordered and paginated according to the approved contract.

### REQ-WH-004: Report metrics

WHEN an authorized user requests report metrics for an approved date range,
THE SYSTEM SHALL return metrics using the approved timezone, aggregation and authorization rules.

## Out of Scope Until Approved

- Persistence model and schema design.
- Role access assumptions.
- Exact response fields, units, date ranges and endpoint paths.

## AI Agent Recommendation

- **Status**: PENDING HUMAN REVIEW
- **Scope**: Warehouse dashboard API requirements
- **Recommendation**: Treat these requirements as draft placeholders and approve the business contract before implementation.
- **Evidence**: `CONTEXT.md` open questions and current `TASKS.md` preparation status.
- **Risks and assumptions**: Exact contracts are intentionally unspecified.
- **Alternatives considered**: Guessing fields from frontend placeholders was rejected.
- **Required human decision**: Approve the requirements and replace placeholders with concrete fields and acceptance cases.

## Human Final Review

- **Status**: PENDING
- **Decision**:
- **Reviewer**:
- **Reviewed at**:
- **Follow-up**:
