# Issue: Story 1.2: Create Product with Validation and Unique SKU

## Status: OPEN

### Description
As an Admin,
I want to create products with required commercial and stock fields,
So that products are ready for sale and stock monitoring.

### Acceptance Criteria:
- Given valid required fields including unique SKU
- When Admin submits product creation
- Then the product is persisted with status ACTIVE by default
- And it appears in product listing results.

- Given a duplicate SKU
- When Admin submits product creation
- Then the request is rejected
- And the response explains that SKU must be unique.
