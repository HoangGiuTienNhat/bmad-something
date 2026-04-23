# BMAD Task List

- [Done] Create PRD v1.1 for simple sales + inventory MVP.
  - Output: `_bmad-output/planning-artifacts/PRD.md`
  - Scope locked: single store, purchase price + gross profit basic, API-only invoice detail, no barcode in phase 1, product-level low stock threshold.

- [Done] Run CE (Create Epics and Stories) from PRD v1.1.
  - Output: `_bmad-output/planning-artifacts/epics.md`
  - Includes: Epic list, FR coverage map, detailed user stories, acceptance criteria.

- [Done] Create full Architecture document for MVP implementation.
  - Output: `docs/bmad/Architecture.md`
  - Includes: Spring Boot clean architecture modules, Story 2.3 sequence with concurrency control, PostgreSQL DDL with PK/FK/indexes, API endpoint list, and standard API response/error contract.

- [Done] Foundation scaffolding from Architecture.md.
  - Output: Spring Boot 3.x Java 21 skeleton with Clean Architecture packages, OpenAPI spec, Flyway V1 schema migration, PostgreSQL docker-compose and application config.

- [Done] Story 1.2 implemented: Create Product with Validation and Unique SKU.
  - Output: Product JPA entity mapped to `products`, repository, create product use case, API POST endpoint, unit tests for unique SKU validation.

- [Done] Story 1.1 implemented: Configure Role-Based Access for Admin and Sales Staff.
  - Output: Security endpoint role matrix for Admin/Sales, admin-only protection on product creation endpoint, and security authorization tests.
