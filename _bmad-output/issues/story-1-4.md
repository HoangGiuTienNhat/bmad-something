# Issue: Story 1.4: Expose Low-Stock Eligibility from Product Threshold

## Status: CLOSED

### Description
As an Admin,
I want each product to carry its own low-stock threshold,
So that stock warning logic is tailored per item.

### Acceptance Criteria:
- Given products with different thresholds
- When stock quantities change
- Then each product low-stock status is evaluated by `stock_qty <= low_stock_threshold`
- And status is available for reporting queries.
