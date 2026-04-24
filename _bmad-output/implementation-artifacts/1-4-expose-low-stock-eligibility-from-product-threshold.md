# Story 1.4: Expose Low-Stock Eligibility from Product Threshold

## Story

As an Admin,
I want each product to carry its own low-stock threshold,
So that stock warning logic is tailored per item.

## Status

ready-for-dev

## Acceptance Criteria

**AC1:** Given products with different thresholds, When stock quantities change, Then each product low-stock status is evaluated by `stock_qty <= low_stock_threshold` And status is available for reporting queries.

## Tasks / Subtasks

- [ ] Task 1: Verify domain entity logic for low stock
  - [ ] 1.1 Ensure `Product` domain entity has `isLowStock()` method implementing `stockQty <= lowStockThreshold`
  - [ ] 1.2 Add unit test for `isLowStock()` in `ProductTest`
- [ ] Task 2: Expose low stock status in API response
  - [ ] 2.1 Update `ProductResponse` DTO to include `isLowStock` boolean field
  - [ ] 2.2 Update `ProductController.mapToResponse` to populate `isLowStock` from domain entity
- [ ] Task 3: Verify integration
  - [ ] 3.1 Write integration test to verify `isLowStock` is correctly returned in `GET /api/v1/products/{id}`
  - [ ] 3.2 Write integration test to verify `isLowStock` is correctly returned in `GET /api/v1/products`

## Dev Notes

### Architecture
- Domain logic should reside in the `Product` entity.
- The `isLowStock` property is a derived state based on `stockQty` and `lowStockThreshold`.

### API Contract
- `ProductResponse` should have `isLowStock` field.

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
