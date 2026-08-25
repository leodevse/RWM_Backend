# RWM Backend Constitution — Spring Boot

# Version: 1.1.0
# Status: APPROVED — Requires RFC Process to Modify
# Owner: Tech Lead & Architecture Board (@arch-board)
# Target Stack: Java 21, Spring Boot, Maven, Spring Security, Spring Data JPA, Flyway, PostgreSQL

---

## RULE PRECEDENCE & CONFLICT HANDLING

Khi các tài liệu hoặc yêu cầu mâu thuẫn, Agent SHALL áp dụng thứ tự ưu tiên sau:

1. User request và constraints an toàn/pháp lý bắt buộc.
2. `CONSTITUTION.md`.
3. `AGENTS.md` và `CLAUDE.md` của repository.
4. Feature `SPEC.md`, `PLAN.md` và `TASKS.md`.
5. Convention đang tồn tại trong codebase.
6. Assumption của Agent.

Nếu xung đột không thể giải quyết bằng thứ tự trên, Agent SHALL dừng thay đổi liên quan và báo cáo rõ conflict trước khi implementation.

---

## 🏛️ LAYER 1: HARD RULES (Bất Biến — Automated Blocking Gates)
*Các quy tắc bảo mật và toàn vẹn dữ liệu tối cao. Vi phạm layer này sẽ làm CI/CD build fail và Agent bị từ chối submit code.*

### SEC-01: Zero Hardcoded Secrets & PII Exposure
- **Rule**: System SHALL NOT lưu trữ credentials, API key (`sk-ant-*`, `sk-proj-*`), private key, JWT secret, connection string hoặc password dưới dạng plaintext trong source code, config files, tests, migration files hay logs.
- **Configuration**: Secrets SHALL được đọc qua environment variables hoặc secret manager; `application.yml` chỉ được chứa placeholder/default an toàn cho local development.
- **Enforcement**: Run secret scanning bằng `git-secrets`, `trufflehog` hoặc công cụ tương đương trong pre-commit/CI.
- **Sanitization**: Email, phone, token, password, JWT, payment data và PII khác SHALL được mask trước khi ghi application log.

### SEC-02: Mandatory Authentication & Authorization
- **Rule**: System SHALL bảo vệ tất cả protected endpoints bằng Spring Security và JWT/OAuth2.
- **Rule**: Mọi endpoint thay đổi trạng thái (`POST`, `PUT`, `PATCH`, `DELETE`) SHALL có authentication và authorization rõ ràng. Ngoại lệ public như login, register, refresh token, health check hoặc webhook SHALL được liệt kê rõ trong security policy/spec và security configuration.
- **Rule**: Role/permission checks SHALL dùng enum/authority tập trung, không hardcode chuỗi role rải rác trong controller/service.

### API-01: Stable API Contract
- **Rule**: Controller SHALL nhận/trả DTO, không expose trực tiếp JPA entity ra API.
- **Rule**: API SHALL dùng HTTP status code, naming, validation error và pagination nhất quán với convention hiện tại của backend.
- **Rule**: Collection endpoints SHALL có pagination, sorting/filtering phù hợp và giới hạn page size tối đa.
- **Rule**: Thay đổi request/response, status code hoặc security contract SHALL cập nhật DTO, OpenAPI documentation và integration tests trong cùng thay đổi.
- **Rule**: Breaking changes SHALL được version hóa hoặc có migration/deprecation plan được approve.

### DATA-01: Controlled Deletion & Auditability
- **Rule**: Feature spec SHALL xác định lifecycle dữ liệu là hard-delete, soft-delete, archive hoặc anonymization. Core business entities SHALL dùng soft-delete (`deleted_at` hoặc trạng thái tương đương) khi cần giữ lịch sử nghiệp vụ.
- **Rule**: Soft-delete SHALL có default filtering, uniqueness strategy và restore behavior được định nghĩa; không được coi là chỉ thêm một cột `deleted_at`.
- **Exception**: Audit/log tables, security token/session tables, join tables thuần kỹ thuật, và dữ liệu test có thể hard-delete nếu được nêu rõ trong plan/spec.
- **Rule**: Production schema changes SHALL đi qua Flyway migration. Không sửa database schema thủ công ngoài migration đã review.

### DATA-02: JPA Persistence Safety
- **Rule**: JPA relationships SHALL có ownership rõ ràng; `FetchType.EAGER` SHALL NOT được dùng nếu không có justification.
- **Rule**: Collection queries SHALL kiểm soát N+1 bằng fetch join, `EntityGraph`, projection hoặc giải pháp được approve.
- **Rule**: Collection không giới hạn SHALL dùng pagination; API SHALL không serialize entity trực tiếp.
- **Rule**: Business entities có khả năng cập nhật đồng thời SHALL dùng optimistic locking, database constraint hoặc strategy được ghi rõ trong plan.
- **Rule**: Không được sửa lỗi `LazyInitializationException` bằng cách chuyển toàn bộ relationship sang EAGER.

### DATA-03: Migration Safety
- **Rule**: Flyway migration đã chạy trên shared environment SHALL immutable; không sửa file migration cũ để thay đổi lịch sử production.
- **Rule**: Destructive migration SHALL có backup, rollout và rollback/recovery strategy.
- **Rule**: Data migration lớn SHOULD tách khỏi schema migration khi cần để kiểm soát thời gian deploy và lock database.
- **Rule**: Migration SHALL an toàn theo thứ tự deploy dự kiến và tương thích với code cũ trong giai đoạn chuyển tiếp.

### REL-01: Idempotency & Concurrency
- **Rule**: State-changing operation có khả năng retry SHALL có idempotency strategy.
- **Rule**: External call SHALL định nghĩa timeout, retry limit và circuit-breaker/fallback behavior khi phù hợp.
- **Rule**: Database uniqueness và integrity constraints SHALL được enforce ở database level, không chỉ bằng Java validation.

---

## 🏗️ LAYER 2: ARCHITECTURAL CONSTRAINTS (Ràng Buộc Kiến Trúc)
*Cấu trúc hệ thống và ranh giới module. Cần có RFC approved bởi Tech Lead nếu muốn tạo ngoại lệ.*

### ARCH-01: Spring Layered Architecture Boundaries
- **Rule**: Dependency flow SHALL đi theo hướng: `controller` -> `service` -> `repository` -> `entity`.
- **Controller**: Chỉ xử lý HTTP mapping, request validation, response mapping và gọi service. Controller SHALL NOT truy cập repository/entity manager trực tiếp, chứa business rule phức tạp hoặc trả JPA entity trực tiếp.
- **Service**: Chứa business rules, orchestration, transaction boundary (`@Transactional`) và security/domain decisions.
- **Repository**: Chỉ xử lý persistence bằng Spring Data JPA/query được parameterize. Repository SHALL NOT trả DTO API trực tiếp cho controller.
- **Entity**: JPA entities SHALL không phụ thuộc vào controller, DTO, security filter hoặc web layer.
- **Rule**: Mapping giữa entity và DTO SHALL nằm ở mapper/service boundary phù hợp, không để controller tự truy cập persistence concerns.

### ARCH-02: Spring Dependency Injection & Configuration
- **Rule**: Beans SHALL dùng constructor injection. Field injection (`@Autowired` trên field) bị cấm trừ khi có lý do framework đặc biệt.
- **Rule**: Configuration values SHALL được bind qua `@ConfigurationProperties` hoặc environment variables; không đọc trực tiếp secret/config bằng literal trong business code.
- **Rule**: Cross-cutting concerns như security, exception handling, CORS, OpenAPI và serialization SHALL được đặt trong package/configuration tập trung.
- **Rule**: Configuration SHALL có type, validation và safe default rõ ràng; secret SHALL không có default production.
- **Rule**: Agent SHALL reuse existing Spring configuration/conventions trước khi tạo thêm mechanism mới; exception theo module phải được ghi rõ trong plan.

### ARCH-03: Asynchronous & Long-Running Operations
- **Rule**: Operations vượt API SLA đã công bố, xử lý bulk, gọi external system hoặc có khả năng chạy lâu/không ổn định SHOULD được đưa ra khỏi request synchronous path bằng job/message queue/event mechanism hoặc cơ chế async được approve.
- **Rule**: Async operations SHALL có retry, timeout, error state, idempotency và observability strategy rõ ràng trong `plan.md`.

---

## 🛠️ LAYER 3: ENGINEERING STANDARDS (Tiêu Chuẩn Kỹ Thuật)
*Tiêu chuẩn code quality và quy trình bảo trì. Agent được phép tự điều chỉnh nếu có lý do giải thích rõ ràng.*

### ENG-01: Spec-to-Code Traceability (EARS Tagging)
- **Rule**: Mỗi business decision, invariant, authorization rule hoặc behavior không hiển nhiên trong `service/`, domain helper, validator hoặc security decision SHALL có trace tới requirement trong `.sdd/features/{slug}/SPEC.md`.
- **Convention**: Dùng Java comment ngắn ngay tại business rule hoặc Javadoc trên method:
  ```java
  /**
   * @ears .sdd/features/{slug}/SPEC.md#REQ-XXX
   */
  ```
  hoặc:
  ```java
  // EARS[Unwanted behavior]: IF credentials are invalid THEN THE SYSTEM SHALL reject login
  ```
- **Rule**: Không gắn EARS tag cho boilerplate thuần kỹ thuật, getter/setter, mapping đơn giản hoặc method không chứa business decision.

### ENG-02: Unified Error Response Standard
- **Rule**: System SHALL không trả stack trace hoặc raw unhandled exception cho client.
- **Rule**: Mọi API error SHALL đi qua `GlobalExceptionHandler`, Spring Security error handler, hoặc handler tập trung tương đương.
- **Schema**: Error response SHALL thống nhất theo schema hiện hành của backend: `{ error: { code, message, timestamp } }`.
- **Evolution**: Nếu cần thêm `request_id`, phải cập nhật cả `ApiErrorResponse`, handlers, tests và API documentation trong cùng thay đổi.

### ENG-03: Validation, Transactions & Persistence
- **Rule**: Request DTOs SHALL dùng Bean Validation (`jakarta.validation`) cho input constraints quan sát được ở API boundary.
- **Rule**: Write operations SHALL khai báo transaction ở service layer bằng `@Transactional`; read-only operations SHOULD dùng `@Transactional(readOnly = true)`.
- **Rule**: Không build SQL bằng string concatenation với user input. Dùng Spring Data derived query, JPQL/native query có parameter binding, hoặc Criteria API.
- **Rule**: Schema thay đổi SHALL có Flyway migration tương ứng và test/evidence phù hợp.

### ENG-04: Verification
- **Rule**: Thay đổi backend SHALL được verify bằng Maven trên Windows:
  ```powershell
  .\mvnw.cmd test
  ```
- **Rule**: Trên Linux/macOS dùng:
  ```bash
  ./mvnw test
  ```
- **Rule**: Unit tests SHALL bao phủ business rules; controller/security behavior SHALL có integration tests; custom repository queries SHALL có persistence tests.
- **Rule**: Bug fix SHALL có regression test, trừ khi agent ghi rõ lý do không thể test.
- **Rule**: Khi không thể chạy verification, agent SHALL ghi rõ command, nguyên nhân và phần chưa được xác nhận.

### TEST-01: Testing Standards
- **Rule**: Tests SHALL kiểm tra cả success path và failure path phù hợp với feature.
- **Rule**: Tests SHALL không phụ thuộc execution order, shared mutable state hoặc dữ liệu môi trường không kiểm soát.
- **Rule**: Database integration tests SHOULD dùng Testcontainers hoặc database tương thích production khi test repository/migration behavior.

### OBS-01: Observability & Audit
- **Rule**: Application logs SHALL dùng structured logging và correlation/request ID khi có thể.
- **Rule**: Secrets, credentials, tokens và sensitive personal data SHALL không xuất hiện trong logs.
- **Rule**: External calls SHALL ghi nhận duration, outcome và safe identifier, không log raw sensitive payload.
- **Rule**: Business-critical state changes SHALL tạo audit record khi feature/spec yêu cầu.

### ENG-05: Dependency & Quality Gates
- **Rule**: Dependency mới SHALL có justification và tương thích với Spring Boot version được approve.
- **Rule**: Agent SHALL kiểm tra known security vulnerabilities khi thêm hoặc nâng cấp dependency.
- **Rule**: Compilation, formatting, static analysis và test checks hiện có SHALL pass trước khi báo hoàn thành.
- **Rule**: Không thêm deprecated API nếu không có justification được ghi rõ.

---

## 📜 RFC PROCESS (REQUEST FOR COMMENTS)
Muốn sửa đổi bất kỳ quy tắc nào trong `CONSTITUTION.md` thuộc Layer 1 hoặc Layer 2:

1. Tạo file đề xuất tại `.sdd/rfcs/RFC-XXX-<title>.md`.
2. Mô tả Motivation, Proposed Change, Risk Assessment, Migration Plan và Rollback Plan.
3. Cần approval của Tech Lead / Human Director trước khi bump version `CONSTITUTION.md`.
4. Nếu thay đổi ảnh hưởng code đang tồn tại, phải cập nhật spec/plan/tasks/tests liên quan trong cùng RFC hoặc issue theo dõi.

---

## 🤖 AI AGENT SELF-CHECK PROTOCOL
Trước khi báo cáo hoàn thành bất kỳ task nào, Agent **BẮT BUỘC** tự chạy checklist sau:

1. [ ] Code/config/test/migration có chứa plaintext secret/key/token/password không? (`SEC-01`)
2. [ ] Endpoint có public/protected policy, authentication và authorization đúng chưa? (`SEC-02`)
3. [ ] API có dùng DTO, status code, pagination và cập nhật contract tests/docs chưa? (`API-01`)
4. [ ] Thay đổi có tuân thủ dependency flow `controller -> service -> repository -> entity` không? (`ARCH-01`)
5. [ ] Có nguy cơ JPA EAGER, N+1, entity exposure hoặc thiếu pagination không? (`DATA-02`)
6. [ ] Business decisions quan trọng có trace `@ears` hoặc `EARS[...]` phù hợp chưa? (`ENG-01`)
7. [ ] Error response có đi qua handler tập trung và không lộ stack trace không? (`ENG-02`)
8. [ ] DTO input có Bean Validation cần thiết chưa? (`ENG-03`)
9. [ ] Transaction, idempotency, concurrency và database constraints đã được xem xét chưa? (`ENG-03`, `REL-01`)
10. [ ] Schema change có migration an toàn, immutable history và test/evidence chưa? (`DATA-01`, `DATA-03`, `ENG-03`)
11. [ ] Logs có structured format, correlation ID và không lộ dữ liệu nhạy cảm không? (`OBS-01`)
12. [ ] Đã chạy `mvnw test` theo OS hoặc ghi rõ lý do không thể chạy chưa? (`ENG-04`)
13. [ ] Dependency mới và quality checks đã được kiểm tra chưa? (`ENG-05`)
