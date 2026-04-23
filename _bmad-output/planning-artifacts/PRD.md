# PRD v1.1 - Simple Sales & Inventory Management System

## Document Control
- Version: 1.1
- Status: Draft for Execution
- Product Owner: Store Owner (Single Store)
- Prepared by: Manager Agent (PM)
- Date: 2026-04-21

## 1) Product Vision
Build a lightweight sales and inventory system for a single store that reduces stock errors, speeds up checkout, and gives daily visibility into revenue and gross profit.

## 2) Business Goals and Success Metrics
### Goals
- Digitize sales and stock operations for one store.
- Prevent stock mismatch caused by manual tracking.
- Provide basic profitability insights by tracking purchase cost.

### KPI Targets (MVP)
- Stock variance (physical count vs system stock) under 3% after 30 days.
- Average order creation time under 60 seconds.
- Successful order completion rate above 98%.
- 100% inventory-changing actions are auditable.

## 3) Scope
### In Scope (MVP)
- Product catalog management with per-product low-stock threshold.
- Sales order creation and completion workflow.
- Automatic stock deduction on completed sales.
- Stock-in recording (purchase/receiving).
- Stock adjustment with mandatory reason and audit trail.
- Basic reporting: daily revenue, gross profit, top selling products, low stock list.
- User roles: Admin and Sales Staff.
- Order detail API payload sufficient for UI invoice rendering.

### Out of Scope (This Phase)
- Multi-branch operations.
- Barcode scanner integration.
- Advanced backend printing module.
- Advanced promotions/CRM/accounting integrations.

## 4) Users and Jobs-To-Be-Done
### Primary Personas
- Admin (Store Owner/Manager)
  - Needs trusted stock numbers and business-level visibility.
  - Needs gross profit trend at a basic operational level.
- Sales Staff
  - Needs fast order flow and confidence in available stock.

### JTBD
- "When I sell items, I want stock to update immediately so I do not oversell."
- "When I receive goods, I want stock and cost basis updated so profit is meaningful."
- "When I close the day, I want to see revenue and gross profit quickly."

## 5) Functional Requirements
### FR-01 Product Management
- Create, update, deactivate product.
- Required fields: SKU (unique), name, unit, selling_price, purchase_price, stock_qty, low_stock_threshold, status.
- Prevent duplicate SKU creation.

### FR-02 Sales Order Management
- Create order with multiple line items.
- Support line quantity edits and item removal before completion.
- Support order-level discount (percent or fixed amount).
- Validate stock availability before order completion.

### FR-03 Stock Deduction on Sale
- On order status = COMPLETED, deduct stock atomically.
- On canceled order, no deduction; if already deducted, restore stock with inventory transaction log.

### FR-04 Stock-In / Receiving
- Create stock-in entry with date, optional supplier note, line items, quantity, optional unit cost.
- On confirmation, increase stock and update purchase price basis used for gross profit (MVP uses latest purchase price strategy).

### FR-05 Stock Adjustment
- Manual stock adjustment with positive or negative delta.
- Mandatory reason and actor capture.
- Full before/after quantity audit.

### FR-06 Reporting
- Daily revenue report.
- Daily/period gross profit report (Revenue - COGS based on latest purchase price at sale time for MVP baseline).
- Top-selling products by quantity and revenue.
- Low-stock report using per-product threshold.

### FR-07 Access Control
- Admin: full permissions.
- Sales Staff: create/manage own sales flow, view stock, cannot perform sensitive admin actions (e.g., user management, destructive product actions, critical stock adjustment without permission).

### FR-08 Order Detail API for UI Invoice
- Provide endpoint payload returning:
  - order header (order_no, datetime, cashier, subtotal, discount, total)
  - line details (sku, product_name, qty, unit_price, line_total)
  - computed summary fields required by UI
- No dedicated print queue/module in backend for MVP.

## 6) Non-Functional Requirements
- P95 API response time under 2 seconds for core flows at MVP load (<=10 concurrent users).
- Transaction safety for inventory-affecting actions (sale completion, stock-in confirmation, adjustment).
- Auditability for all inventory mutations.
- Daily backup capability.
- Validation and error handling must return clear business messages.

## 7) User Stories and Acceptance Criteria
### US-01 Create Product
As an Admin, I want to create products with pricing and threshold so I can sell and monitor stock.

Acceptance Criteria:
- Given valid required fields, when Admin saves a product, then product is persisted and visible in catalog.
- Given duplicate SKU, when Admin attempts to save, then system rejects with clear error.
- Given low_stock_threshold set, when stock is below threshold, then product appears in low-stock report.

### US-02 Complete Sale with Stock Validation
As Sales Staff, I want to complete an order quickly and accurately so customer checkout is smooth.

Acceptance Criteria:
- Given sufficient stock, when user completes order, then order status is COMPLETED and stock is deducted.
- Given insufficient stock for any line, when user completes order, then order is rejected and stock remains unchanged.
- Given discount input, when order total is calculated, then total reflects configured discount logic.

### US-03 Record Stock-In
As an Admin, I want to record incoming goods so stock and cost basis stay up to date.

Acceptance Criteria:
- Given valid stock-in lines, when Admin confirms, then stock increases correctly per product.
- Given unit cost input, when stock-in is confirmed, then purchase price basis updates using MVP rule (latest purchase price).
- Inventory transaction log is created for each affected product.

### US-04 Adjust Stock with Audit
As an Admin, I want controlled adjustments so stock corrections are traceable.

Acceptance Criteria:
- Given adjustment with reason, when saved, then stock changes and audit log stores actor, reason, before_qty, after_qty.
- Given missing reason, when submit, then system rejects adjustment.

### US-05 View Daily Revenue and Gross Profit
As an Admin, I want daily financial visibility so I can make inventory and pricing decisions.

Acceptance Criteria:
- Given completed sales in date range, when report is generated, then revenue totals are accurate.
- Given purchase cost basis available, when gross profit report is generated, then gross profit = revenue - COGS (per MVP method).
- Given no data in range, system returns empty report with zero totals (not error).

### US-06 Fetch Order Details for Invoice UI
As Sales Staff, I want API order detail response so the frontend can render customer invoice.

Acceptance Criteria:
- Given a valid order id/order number, when API is called, then response returns all required header and line detail fields.
- Given non-existing order, API returns not found with clear message.

## 8) Data Model (MVP)
### Product
- id (UUID)
- sku (string, unique, indexed)
- name (string)
- unit (string)
- selling_price (decimal)
- purchase_price (decimal)  # latest purchase price for MVP
- stock_qty (decimal)
- low_stock_threshold (decimal)
- status (ACTIVE/INACTIVE)
- created_at, updated_at

### User
- id (UUID)
- full_name (string)
- role (ADMIN/SALES)
- status (ACTIVE/INACTIVE)
- created_at, updated_at

### SaleOrder
- id (UUID)
- order_no (string, unique)
- status (DRAFT/COMPLETED/CANCELED)
- subtotal (decimal)
- discount_type (PERCENT/FIXED/null)
- discount_value (decimal)
- discount_amount (decimal)
- total (decimal)
- created_by (User.id)
- completed_at (datetime, nullable)
- created_at, updated_at

### SaleOrderItem
- id (UUID)
- sale_order_id (FK -> SaleOrder.id)
- product_id (FK -> Product.id)
- sku_snapshot (string)
- product_name_snapshot (string)
- qty (decimal)
- unit_price (decimal)
- purchase_price_snapshot (decimal)  # cost captured at sale completion for gross profit
- line_total (decimal)

### StockIn
- id (UUID)
- reference_no (string, unique)
- status (DRAFT/CONFIRMED/CANCELED)
- note (string, nullable)
- created_by (User.id)
- confirmed_at (datetime, nullable)
- created_at, updated_at

### StockInItem
- id (UUID)
- stock_in_id (FK -> StockIn.id)
- product_id (FK -> Product.id)
- qty (decimal)
- unit_cost (decimal, nullable)

### InventoryTransaction
- id (UUID)
- product_id (FK -> Product.id)
- transaction_type (SALE_OUT/STOCK_IN/ADJUSTMENT/SALE_RESTORE)
- qty_delta (decimal)  # negative for out, positive for in
- qty_before (decimal)
- qty_after (decimal)
- reference_type (SALE_ORDER/STOCK_IN/ADJUSTMENT)
- reference_id (UUID/string)
- reason (string, nullable)
- actor_user_id (User.id)
- created_at

## 9) Business Rules
- BR-01 Stock cannot go below zero at sale completion.
- BR-02 SKU must be unique.
- BR-03 Every stock mutation must generate `InventoryTransaction`.
- BR-04 Gross profit (MVP) uses `purchase_price_snapshot` captured at order completion; snapshot defaults to product.latest purchase price at that time.
- BR-05 Low-stock status is determined per product using `stock_qty <= low_stock_threshold`.

## 10) API Surface (MVP-Level)
- `POST /products`, `PATCH /products/{id}`, `GET /products`
- `POST /sales-orders`, `POST /sales-orders/{id}/complete`, `POST /sales-orders/{id}/cancel`, `GET /sales-orders/{id}`
- `POST /stock-ins`, `POST /stock-ins/{id}/confirm`
- `POST /stock-adjustments`
- `GET /reports/revenue-daily`
- `GET /reports/gross-profit`
- `GET /reports/low-stock`

## 11) Assumptions and Constraints
- Single-store only.
- Small team operational load.
- UI handles invoice rendering from API response.
- No barcode flow in MVP.
- Costing method is intentionally simplified for speed-to-market and can be upgraded later (e.g., moving average/FIFO).

## 12) Risks and Mitigation
- Risk: Wrong stock under concurrent operations.
  - Mitigation: DB transaction + row-level lock/versioning on product stock.
- Risk: Profit mismatch due to simplistic costing.
  - Mitigation: clearly label report method and plan phase-2 costing upgrade.
- Risk: Operational misuse of stock adjustments.
  - Mitigation: role-based restriction + mandatory reason + audit export.

## 13) Release Readiness Checklist (MVP)
- Core flows tested: product, sale, stock-in, adjustment, reports.
- Access control validated for both roles.
- Audit logs verifiable for stock-changing actions.
- Backup/restore dry run completed.
- UAT sign-off from store owner.

