# CLAUDE.md - RWM Backend Project Memory

# Version: 1.1.0
# Project: RWM Backend
# Stack: Java 21, Spring Boot, Maven, Spring Security, Spring Data JPA, Flyway, PostgreSQL

---

## 1. Project Identity

RWM Backend is a Java 21 Spring Boot REST API. The project uses Maven Wrapper for builds, Spring Security for authentication and authorization, Spring Data JPA for persistence, Flyway for schema versioning, and PostgreSQL as the production database.

The implementation SHALL follow the actual repository structure and existing conventions before introducing a new abstraction.

---

## 2. Source-of-Truth Hierarchy

The following documents have distinct responsibilities:

1. `CONSTITUTION.md` defines governance, security, architecture constraints and quality gates.
2. `AGENTS.md` defines agent identity, operating boundaries, permissions and human review rules.
3. `CLAUDE.md` defines this project's architecture, conventions, commands and implementation memory.
4. `.sdd/features/{slug}/SPEC.md` defines feature behavior and acceptance requirements.
5. `.sdd/features/{slug}/PLAN.md` defines the approved implementation approach.
6. `.sdd/features/{slug}/TASKS.md` defines executable work items and their status.
7. Existing source code defines local conventions where the documents above are silent.

If two sources conflict, the higher-priority source wins. If the conflict cannot be resolved safely, the Agent SHALL stop the affected work and report the conflict before implementation.

---

## 3. Repository Structure

```text
.
|-- AGENTS.md
|-- CLAUDE.md
|-- CONSTITUTION.md
|-- pom.xml
|-- mvnw
|-- mvnw.cmd
|-- .sdd/features/{slug}/
|   |-- CONTEXT.md
|   |-- SPEC.md
|   |-- PLAN.md
|   `-- TASKS.md
|-- docs/
`-- src/
    |-- main/java/fu/rwm_backend/
    |   |-- config/
    |   |-- common/
    |   |-- controller/
    |   |-- dto/
    |   |-- entity/
    |   |-- mapper/
    |   |-- repository/
    |   |-- security/
    |   `-- service/
    |   `-- resources/db/migration/
    `-- test/java/fu/rwm_backend/
```

New code SHALL follow this layout unless an approved plan documents a different module boundary.

---

## 4. Spring Boot Architecture

The normal dependency direction is:

```text
controller -> service -> repository -> entity
```

- Controllers handle HTTP mapping, request validation, response mapping and service invocation.
- Services own business rules, orchestration, authorization decisions and transaction boundaries.
- Repositories own persistence queries and database access only.
- Entities represent persistence state and SHALL NOT depend on web, controller, DTO or security filter classes.
- Mappers convert between entities and DTOs at the service/mapper boundary.
- Controllers SHALL NOT access repositories or `EntityManager` directly.
- Controllers SHALL NOT return JPA entities directly.
- Beans SHALL use constructor injection.
- Configuration values SHALL use `@ConfigurationProperties` or environment variables with validation and safe defaults.
- Cross-cutting configuration such as security, exception handling, CORS, OpenAPI and serialization SHALL remain centralized.

The Agent SHALL reuse existing services, repositories, DTOs, mappers and configuration before creating duplicates.

---

## 5. Java and Naming Conventions

- Java classes SHALL use `PascalCase`.
- Methods, parameters and local variables SHALL use `camelCase`.
- Constants SHALL use `UPPER_SNAKE_CASE`.
- Package names SHALL use lowercase.
- Existing suffix conventions SHALL be preserved: `Controller`, `Service`, `ServiceImpl`, `Repository`, `Entity`, `Mapper`, `Request`, `Response`, `Exception` and `Config`.
- Public APIs SHALL use explicit DTOs such as `LoginRequest` and `LoginResponse`.
- Lombok MAY be used only where it matches existing project conventions and does not hide important business behavior.
- Magic values in business logic SHALL be named constants or typed configuration.

---

## 6. API Conventions

- Every endpoint SHALL have an explicit authentication policy: public, authenticated or authority-restricted.
- Public exceptions such as login, registration, token refresh, health checks or approved webhooks SHALL be documented in the feature spec and security configuration.
- Request DTOs SHALL use Jakarta Bean Validation at the API boundary.
- Response DTOs SHALL not expose passwords, tokens not intended for the client, internal identifiers or persistence-only fields.
- Collection endpoints SHALL define pagination, sorting/filtering behavior and a maximum page size.
- API changes SHALL update DTOs, OpenAPI documentation, error behavior and integration tests together.
- Breaking changes SHALL use versioning or an approved deprecation and migration plan.
- Error responses SHALL follow the schema defined by `CONSTITUTION.md` and centralized exception/security handlers.

---

## 7. Persistence and Database Conventions

- Production schema changes SHALL be implemented through Flyway migrations under `src/main/resources/db/migration/`.
- Applied Flyway migrations SHALL be treated as immutable.
- Existing migration files SHALL NOT be edited to repair a shared or production database history.
- Destructive migrations SHALL document backup, rollout and recovery strategy.
- JPA relationships SHALL have explicit ownership and appropriate fetch behavior.
- `FetchType.EAGER` SHALL NOT be introduced without justification.
- Custom queries SHALL consider N+1 behavior and use fetch join, `EntityGraph`, projection or another documented solution where appropriate.
- Unbounded collections SHALL use pagination.
- Business-critical concurrent updates SHALL use optimistic locking, database constraints or another documented strategy.
- Database uniqueness and integrity constraints SHALL be enforced at database level as well as at the API validation layer.
- Soft-delete behavior SHALL follow `DATA-01` in `CONSTITUTION.md`, including filtering, uniqueness and restore behavior.

---

## 8. Security and Configuration

- Secrets, passwords, private keys, JWT secrets, API keys and connection credentials SHALL never be committed or logged.
- Secrets SHALL be supplied through environment variables or an approved secret manager.
- JWT authentication and authorization SHALL use the existing Spring Security configuration and centralized authorities.
- Passwords SHALL be encoded with the approved password encoder and SHALL never be returned in a response.
- Logs SHALL mask tokens, credentials, passwords and sensitive personal data.
- CORS, CSRF, session policy, authentication entry points and access-denied handling SHALL be changed only with explicit security reasoning and tests.
- Configuration changes SHALL preserve safe local behavior without introducing production secrets or insecure defaults.

---

## 9. Transactions, Async Work and External Calls

- Write use cases SHALL define transaction boundaries in the service layer.
- Read-only use cases SHOULD use `@Transactional(readOnly = true)` where applicable.
- The Agent SHALL NOT rely on self-invocation to trigger Spring transactional behavior.
- Operations exceeding the documented API SLA, processing bulk data, or depending on unreliable external systems SHOULD use an approved asynchronous mechanism.
- Async work SHALL define timeout, retry limit, failure state, idempotency and observability behavior.
- Retryable state-changing operations SHALL have an idempotency strategy.
- External calls SHALL use explicit timeout and safe error handling.

---

## 10. SDD/ADD Workflow

For a feature or non-trivial change, the Agent SHALL inspect the applicable `CONTEXT.md`, `SPEC.md`, `PLAN.md` and `TASKS.md` before editing implementation code.

- Functional requirements SHALL be expressed using EARS where required by the feature process.
- Business decisions, invariants, authorization rules and non-obvious behavior SHALL trace to the relevant requirement using `@ears` or `EARS[...]`.
- Boilerplate, simple mapping and accessors do not require EARS tags.
- If requirements are ambiguous or incomplete, the Agent SHALL update or escalate the SPEC before implementation.
- If the SPEC is clear and the failure is an implementation defect, the Agent SHALL fix the code and add a regression test.
- Only work listed in the approved `TASKS.md` should be implemented without explicit human direction.
- Task status SHALL be updated only after the corresponding implementation and verification evidence exists.
- A failed test SHALL be analyzed before code changes; repeated speculative patching is prohibited.

---

## 11. Testing and Verification

Windows:

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw test
./mvnw clean verify
./mvnw spring-boot:run
```

Testing expectations:

- Business rules SHALL have unit tests.
- Controller, validation and security behavior SHALL have integration tests.
- Custom repository queries and migration behavior SHALL have persistence tests where applicable.
- Bug fixes SHALL include a regression test unless the Agent records why testing is not feasible.
- Tests SHALL cover relevant success and failure paths.
- Tests SHALL not depend on execution order or uncontrolled shared state.
- Testcontainers or an equivalent production-compatible database SHOULD be used for database behavior that H2 cannot faithfully represent.

Before reporting completion, the Agent SHALL state the exact verification commands run, their result, and any checks that could not be executed.

---

## 12. Agent Completion Protocol

Before reporting a task as complete, the Agent SHALL:

1. Inspect changed files and confirm they are within the approved scope.
2. Check security, API contract, transaction and persistence impacts.
3. Run the narrowest relevant tests, then broader Maven verification when practical.
4. Confirm migrations, OpenAPI documentation and feature artifacts are updated when required.
5. Update `TASKS.md` only when implementation and verification evidence is available.
6. Report changed files, verification results, known limitations and unresolved risks.

The Agent SHALL NOT approve its own recommendation or record a human review decision on behalf of the Human Director.

---

## 13. Project-Specific Decisions

- Authentication is implemented with Spring Security and JWT-related classes under `security/`.
- API error handling is centralized under `common/exception/`.
- Login persistence and audit behavior use repositories and Flyway migrations under the current project structure.
- OpenAPI configuration is maintained under `config/` and must remain consistent with public API behavior.
- When an existing project convention conflicts with this document, follow the source-of-truth hierarchy in Section 2 and report unresolved conflicts.
