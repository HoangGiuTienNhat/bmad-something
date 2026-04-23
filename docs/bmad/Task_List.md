# BMAD Task List

- [Done] Foundation scaffolding for Spring Boot 3.x (Java 21) created with Clean Architecture package structure.
  - Output: `src/main/java/com/something/{api,application,domain,infrastructure}`
- [Done] OpenAPI skeleton generated from architecture endpoint list.
  - Output: `docs/openapi/api-spec.yaml`
- [Done] Flyway baseline migration generated from PostgreSQL DDL.
  - Output: `src/main/resources/db/migration/V1__init_schema.sql`
- [Done] Local PostgreSQL 15 runtime and application database configuration created.
  - Output: `docker-compose.yml`, `src/main/resources/application.yml`

- [Done] Story 1.2 implemented: Create Product with Validation and Unique SKU.
  - Output: Product JPA entity, repository, create product use case, product POST controller, unit tests for duplicate SKU validation.

- [Done] Seed default Admin account for API authentication testing.
  - Output: Flyway V2 migration for `users.password_hash` and admin seed, DB-backed Spring Security `UserDetailsService`.

- [Done] Story 1.1 implemented: Role-Based Access Control for Admin and Sales Staff.
  - Output: Endpoint-level RBAC policy in `SecurityConfig`, admin-only guard on product creation, and security integration tests for Admin/Sales authorization behavior.
