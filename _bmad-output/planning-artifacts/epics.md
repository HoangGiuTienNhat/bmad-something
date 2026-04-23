---
stepsCompleted:
  - step-01-validate-prerequisites
  - step-02-design-epics
  - step-03-create-stories
  - step-04-final-validation
inputDocuments:
  - /home/nhat/something/_bmad-output/planning-artifacts/PRD.md
---

# something - Epic Breakdown

## Overview

This document provides the complete epic and story breakdown for something, decomposing the requirements from the PRD, UX Design if it exists, and Architecture requirements into implementable stories.

## Requirements Inventory

### Functional Requirements

FR1: Admin can create, update, and deactivate products with required fields: SKU (unique), name, unit, selling_price, purchase_price, stock_qty, low_stock_threshold, status.
FR2: Sales Staff can create sales orders with multiple items, edit quantities/remove lines before completion, and apply order-level discount (percent or fixed).
FR3: System deducts stock atomically when order is completed; canceled orders do not deduct stock (or restore if already deducted).
FR4: Admin can create and confirm stock-in entries with item quantities and optional unit cost; stock increases and latest purchase price basis updates on confirmation.
FR5: Admin can perform manual stock adjustments (positive/negative) with mandatory reason and full before/after audit.
FR6: System provides reports for daily revenue, gross profit, top-selling products, and low-stock list using product-level threshold.
FR7: Role-based access control supports Admin full access and Sales Staff limited operational access.
FR8: System provides order detail API payload for UI invoice rendering (header, line details, computed totals).

### NonFunctional Requirements

NFR1: P95 API response time under 2 seconds for core flows at MVP load (<=10 concurrent users).
NFR2: Inventory-affecting actions use transaction safety and prevent inconsistent stock state.
NFR3: All inventory mutations are auditable with actor and timestamp.
NFR4: System supports daily data backup.
NFR5: Validation and error handling provide clear business-facing messages.

### Additional Requirements

- Architecture document was not available at planning-artifacts stage; no extra architecture-only constraints were extracted.
- Costing method for MVP is latest purchase price snapshot captured at sale completion for gross profit baseline.
- Solution remains single-store only in this release.

### UX Design Requirements

- UX specification document was not provided for this CE run.

### FR Coverage Map

FR1: Epic 1 - Product catalog setup and maintenance.
FR2: Epic 2 - Sales order workflow and pricing logic.
FR3: Epic 2 - Atomic stock deduction and cancellation restore.
FR4: Epic 3 - Stock-in receiving and cost basis update.
FR5: Epic 3 - Stock adjustment with reason and audit.
FR6: Epic 4 - Revenue, gross profit, top-seller, and low-stock reporting.
FR7: Epic 1 - Role-based permission boundaries.
FR8: Epic 2 - Order detail API contract for invoice UI.

## Epic List

### Epic 1: Operational Foundation and Product Catalog
Enable Admin and Sales Staff to securely access core operations and maintain an accurate, sellable product catalog with product-level low-stock configuration.
**FRs covered:** FR1, FR7

### Epic 2: Sales Execution and Checkout Integrity
Enable Sales Staff to create and complete orders fast while preserving stock accuracy and exposing invoice-ready order detail APIs.
**FRs covered:** FR2, FR3, FR8

### Epic 3: Inventory Control and Auditability
Enable Admin to maintain real stock truth through stock-in and adjustment workflows with complete traceability.
**FRs covered:** FR4, FR5

### Epic 4: Business Visibility and Performance Reporting
Enable Admin to monitor daily revenue, gross profit, sell-through, and low-stock risk for better operating decisions.
**FRs covered:** FR6

## Epic 1: Operational Foundation and Product Catalog

Provide secure role boundaries and reliable product master data so all downstream sales and inventory workflows operate on trusted catalog information.

### Story 1.1: Configure Role-Based Access for Admin and Sales Staff

As an Admin,
I want role-based access control configured for Admin and Sales Staff,
So that sensitive actions are restricted while daily selling can proceed safely.

**Acceptance Criteria:**

**Given** a user with Admin role
**When** the user accesses product, inventory, and reporting endpoints
**Then** access is granted for all protected operations
**And** audit-relevant endpoints return successful authorization.

**Given** a user with Sales Staff role
**When** the user attempts sensitive endpoints (user management, critical stock adjustment, destructive product actions)
**Then** access is denied with a clear permission error
**And** allowed sales operations remain accessible.

### Story 1.2: Create Product with Validation and Unique SKU

As an Admin,
I want to create products with required commercial and stock fields,
So that products are ready for sale and stock monitoring.

**Acceptance Criteria:**

**Given** valid required fields including unique SKU
**When** Admin submits product creation
**Then** the product is persisted with status ACTIVE by default
**And** it appears in product listing results.

**Given** a duplicate SKU
**When** Admin submits product creation
**Then** the request is rejected
**And** the response explains that SKU must be unique.

### Story 1.3: Update and Deactivate Product

As an Admin,
I want to update and deactivate products without deleting history,
So that catalog changes do not break reporting and transaction traceability.

**Acceptance Criteria:**

**Given** an existing product
**When** Admin updates fields such as name, prices, unit, or low_stock_threshold
**Then** changes are saved
**And** updated values are returned in subsequent reads.

**Given** a product set to INACTIVE
**When** Sales Staff searches selectable products for new orders
**Then** inactive products are excluded from sellable selection
**And** historical orders remain readable with product snapshots.

### Story 1.4: Expose Low-Stock Eligibility from Product Threshold

As an Admin,
I want each product to carry its own low-stock threshold,
So that stock warning logic is tailored per item.

**Acceptance Criteria:**

**Given** products with different thresholds
**When** stock quantities change
**Then** each product low-stock status is evaluated by `stock_qty <= low_stock_threshold`
**And** status is available for reporting queries.

## Epic 2: Sales Execution and Checkout Integrity

Deliver a fast and safe sales flow where order totals are accurate, stock integrity is preserved atomically, and invoice UI receives complete order payloads.

### Story 2.1: Build Draft Sales Order with Multiple Line Items

As Sales Staff,
I want to create a draft order with multiple products and editable lines,
So that I can prepare the final checkout accurately.

**Acceptance Criteria:**

**Given** active sellable products
**When** Sales Staff creates a draft order and adds/removes line items
**Then** the system stores the draft with recalculated subtotal
**And** each line contains product snapshot fields (SKU and name).

**Given** a draft order
**When** Sales Staff updates line quantities
**Then** line totals and subtotal are recalculated deterministically
**And** validation rejects non-positive quantities.

### Story 2.2: Apply Order-Level Discount and Final Total Calculation

As Sales Staff,
I want to apply either percentage or fixed discount at order level,
So that final payable amount matches business policy.

**Acceptance Criteria:**

**Given** a draft order with positive subtotal
**When** Sales Staff applies a percent discount
**Then** discount amount and total are computed correctly
**And** total never becomes negative.

**Given** a draft order with positive subtotal
**When** Sales Staff applies a fixed discount
**Then** discount amount and total are computed correctly
**And** validation rejects fixed discount larger than subtotal.

### Story 2.3: Complete Order with Atomic Stock Deduction

As Sales Staff,
I want order completion to deduct stock safely in one transaction,
So that the system never oversells or leaves partial updates.

**Acceptance Criteria:**

**Given** sufficient stock for every line
**When** Sales Staff completes the order
**Then** order status changes to COMPLETED
**And** all related product stock quantities are deducted atomically.

**Given** insufficient stock for at least one line
**When** Sales Staff attempts completion
**Then** completion is rejected
**And** no stock quantity changes are committed.

### Story 2.4: Cancel Completed or Draft Orders with Correct Stock Outcome

As Sales Staff,
I want cancellation behavior to preserve stock correctness,
So that order state changes do not corrupt inventory.

**Acceptance Criteria:**

**Given** a DRAFT order
**When** Sales Staff cancels the order
**Then** status updates to CANCELED
**And** stock remains unchanged.

**Given** a COMPLETED order
**When** authorized user cancels under allowed rules
**Then** stock is restored for each line through inventory transactions
**And** cancellation event is auditable.

### Story 2.5: Provide Order Detail API for Invoice Rendering

As Sales Staff,
I want an order detail API that returns all invoice display fields,
So that frontend can render invoice without backend print specialization.

**Acceptance Criteria:**

**Given** an existing order id or order number
**When** client calls order detail endpoint
**Then** response includes header (order_no, datetime, cashier, subtotal, discount, total)
**And** line details include sku, product_name, qty, unit_price, and line_total.

**Given** a non-existing order reference
**When** client calls the endpoint
**Then** API returns not found
**And** error message is clear and non-ambiguous.

## Epic 3: Inventory Control and Auditability

Ensure inventory operations outside sales remain accurate and fully traceable through explicit receiving and adjustment workflows.

### Story 3.1: Create and Confirm Stock-In Transaction

As an Admin,
I want to record incoming stock and confirm receipt,
So that on-hand quantity is accurate and operations can continue selling.

**Acceptance Criteria:**

**Given** a stock-in document with valid line quantities
**When** Admin confirms stock-in
**Then** product stock increases by item quantities
**And** stock-in status becomes CONFIRMED.

**Given** a stock-in line includes unit_cost
**When** Admin confirms stock-in
**Then** product purchase_price is updated using MVP latest-cost rule
**And** this value becomes available for future gross profit snapshots.

### Story 3.2: Create Inventory Transaction Log for Every Stock Mutation

As an Admin,
I want each stock-changing action to generate an inventory transaction entry,
So that every quantity change can be audited later.

**Acceptance Criteria:**

**Given** sale completion, sale restore, stock-in confirmation, or stock adjustment
**When** stock quantity changes
**Then** an InventoryTransaction record is created
**And** record contains transaction_type, qty_delta, qty_before, qty_after, actor_user_id, reference_type, and reference_id.

**Given** an inventory transaction record
**When** Admin queries audit history
**Then** entries are returned in chronological order
**And** each entry can be traced back to originating business action.

### Story 3.3: Adjust Stock with Mandatory Reason and Permission Guard

As an Admin,
I want controlled stock adjustments with required reason,
So that correction operations are secure and accountable.

**Acceptance Criteria:**

**Given** Admin submits adjustment with qty_delta and reason
**When** request is valid
**Then** stock is updated correctly
**And** adjustment plus inventory transaction are both persisted.

**Given** missing reason or unauthorized role
**When** adjustment is submitted
**Then** system rejects the request
**And** response explains validation or permission failure.

## Epic 4: Business Visibility and Performance Reporting

Provide daily decision support metrics from transactional data so Admin can act on revenue, margin, product movement, and replenishment risk.

### Story 4.1: Generate Daily Revenue Report

As an Admin,
I want daily revenue totals for a selected date range,
So that I can monitor sales performance quickly.

**Acceptance Criteria:**

**Given** completed orders in selected range
**When** Admin requests revenue report
**Then** API returns accurate aggregated totals
**And** canceled orders are excluded from recognized revenue.

**Given** no orders in selected range
**When** Admin requests revenue report
**Then** API returns zero totals
**And** response format remains consistent.

### Story 4.2: Generate Gross Profit Report Using Cost Snapshot

As an Admin,
I want gross profit reports based on captured purchase cost snapshots,
So that margin visibility is available in MVP without advanced costing.

**Acceptance Criteria:**

**Given** completed order items with purchase_price_snapshot
**When** Admin requests gross profit report
**Then** gross profit is calculated as revenue minus COGS
**And** report documents that costing method is latest purchase price snapshot.

**Given** mixed sales data quality scenarios
**When** report is generated
**Then** incomplete cost data is handled with explicit fallback/error policy
**And** output remains understandable to business users.

### Story 4.3: Provide Top-Selling Products and Low-Stock Report

As an Admin,
I want to view top-selling items and low-stock products,
So that I can optimize replenishment and merchandising decisions.

**Acceptance Criteria:**

**Given** completed sales data
**When** Admin requests top-selling products
**Then** API returns ranked products by quantity and revenue
**And** ranking logic is deterministic for ties.

**Given** current stock and per-product thresholds
**When** Admin requests low-stock report
**Then** API returns products where `stock_qty <= low_stock_threshold`
**And** output includes fields needed for replenishment action.

## Final Validation Summary

- All FRs (FR1-FR8) are mapped to at least one epic and one or more stories.
- Story sequence avoids forward dependency inside each epic.
- Epics are organized by user value (not technical layers).
- Acceptance criteria are testable using Given/When/Then format.
- CE artifact is ready for sprint-level story implementation planning.
