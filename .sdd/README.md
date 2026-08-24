# SDD Lifecycle & Multi-Feature Registry

Trạng thái các Features & Specifications trong hệ thống:

## 1. Global Governance (Layer 1)
- [`CONSTITUTION.md`](../CONSTITUTION.md) — Hard Quality Gates & Security Rules (Lock)
- [`AGENTS.md`](../AGENTS.md) — Agent Constitution, Scope & Tool Permissions
- [`CLAUDE.md`](../CLAUDE.md) — Project Memory & Architecture DNA
- [`shared_context.md`](./shared_context.md) — Active API Contracts & State Synchronization

---

## 2. Feature Registry (Layer 3)

| Feature Slug | Feature Name | Owner | Status | Paths |
| :--- | :--- | :--- | :--- | :--- |
| `feat-001-user-login` | User Login & Authentication Integration | Backend Team | PREPARATION | [Feature](./features/feat-001-user-login/TASKS.md) |
| `feat-002-warehouse-dashboard` | Warehouse Dashboard APIs | Backend Team | PREPARATION | [Feature](./features/feat-002-warehouse-dashboard/TASKS.md) |

---

## 3. Standard Feature Structure
Mỗi feature mới khi tạo sẽ nằm trong thư mục `.sdd/features/{feature-slug}/` gồm 4 file:
- `CONTEXT.md` (Pha 0 - Context Discovery)
- `SPEC.md` (Pha 1 - Executable Specification)
- `PLAN.md` (Pha 2 - Architecture Plan)
- `TASKS.md` (Pha 3 - Atomic Tasks Breakdown)
