# Story 1.3: Update and Deactivate Product

## Story

As an Admin,
I want to update and deactivate products without deleting history,
So that catalog changes do not break reporting and transaction traceability.

## Status

in-progress

## Acceptance Criteria

**AC1:** Given an existing product, When Admin updates fields such as name, prices, unit, or low_stock_threshold, Then changes are saved And updated values are returned in subsequent reads.

**AC2:** Given a product set to INACTIVE, When Sales Staff searches selectable products for new orders, Then inactive products are excluded from sellable selection And historical orders remain readable with product snapshots.

## Tasks / Subtasks

- [ ] Task 1: Extend ProductRepositoryPort with findById, findAll, and findByStatus methods
  - [ ] 1.1 Add findById(UUID id) returning Optional<Product> to port interface
  - [ ] 1.2 Add findAllByStatus(ProductStatus status) returning List<Product> to port interface
  - [ ] 1.3 Implement these methods in ProductRepositoryAdapter with JPA mapping
  - [ ] 1.4 Add findById to JPA ProductRepository (standard Spring Data)
  - [ ] 1.5 Add findByStatus to JPA ProductRepository
- [ ] Task 2: Create GetProductUseCase and ListProductsUseCase
  - [ ] 2.1 Create GetProductUseCase with findById logic and not-found handling
  - [ ] 2.2 Create ListProductsUseCase with optional status filter
  - [ ] 2.3 Write unit tests for GetProductUseCase
  - [ ] 2.4 Write unit tests for ListProductsUseCase
- [ ] Task 3: Create UpdateProductUseCase
  - [ ] 3.1 Create UpdateProductRequest DTO at application layer (Command)
  - [ ] 3.2 Implement UpdateProductUseCase with partial update logic for name, unit, sellingPrice, purchasePrice, lowStockThreshold
  - [ ] 3.3 Validate product exists (throw not-found if missing)
  - [ ] 3.4 Write unit tests for UpdateProductUseCase
- [ ] Task 4: Create UpdateProductStatusUseCase (deactivate/reactivate)
  - [ ] 4.1 Implement UpdateProductStatusUseCase accepting productId and new status
  - [ ] 4.2 Validate product exists (throw not-found if missing)
  - [ ] 4.3 Write unit tests for UpdateProductStatusUseCase
- [ ] Task 5: Create API DTOs for new endpoints
  - [ ] 5.1 Create UpdateProductRequest API DTO with validation
  - [ ] 5.2 Create UpdateProductStatusRequest API DTO
  - [ ] 5.3 Create ProductResponse DTO (shared response for get/update)
- [ ] Task 6: Add controller endpoints to ProductController
  - [ ] 6.1 GET /api/v1/products/{id} - get single product
  - [ ] 6.2 GET /api/v1/products - list products with optional status filter
  - [ ] 6.3 PATCH /api/v1/products/{id} - update product fields
  - [ ] 6.4 PATCH /api/v1/products/{id}/status - update product status
  - [ ] 6.5 Write WebMvcTest for all new endpoints
- [ ] Task 7: Security and final validation
  - [ ] 7.1 Verify all product endpoints require ADMIN role (already configured in SecurityConfig)
  - [ ] 7.2 Add security tests for new endpoints
  - [ ] 7.3 Run full test suite and verify no regressions

## Dev Notes

### Architecture
- Follow hexagonal/clean architecture: domain → application → api/infrastructure
- Use existing patterns from CreateProductUseCase for consistency
- ProductRepositoryPort is the output port, ProductRepositoryAdapter is the JPA adapter
- SKU is NOT updatable (it's a unique business identifier)
- Architecture specifies `PATCH /api/v1/products/{id}` for update and `PATCH /api/v1/products/{id}/status` for status changes
- All product endpoints are Admin-only per SecurityConfig
- Use @Version for optimistic locking on non-critical updates (architecture §6.3)

### API Contract
- Architecture §10.1 defines success envelope: { success, data, meta }
- Architecture §10.2 defines error envelope: { success, error: { code, message, details }, meta }
- HTTP status codes: 200 OK for update, 404 for not found, 422 for validation errors

### Error Codes
- PRODUCT_NOT_FOUND when product ID doesn't exist
- VALIDATION_ERROR for invalid input

## Dev Agent Record

### Implementation Plan
(To be filled during implementation)

### Debug Log
(To be filled if issues arise)

### Completion Notes
(To be filled on completion)

## File List

(To be updated after each task)

## Change Log

(To be updated after implementation)
