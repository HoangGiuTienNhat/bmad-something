# Issue: Story 1.3: Update and Deactivate Product

## Status: OPEN

### Description
As an Admin,
I want to update and deactivate products without deleting history,
So that catalog changes do not break reporting and transaction traceability.

### Acceptance Criteria:
- Given an existing product
- When Admin updates fields such as name, prices, unit, or low_stock_threshold
- Then changes are saved
- And updated values are returned in subsequent reads.

- Given a product set to INACTIVE
- When Sales Staff searches selectable products for new orders
- Then inactive products are excluded from sellable selection
- And historical orders remain readable with product snapshots.
