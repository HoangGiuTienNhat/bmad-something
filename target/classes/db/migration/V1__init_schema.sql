create extension if not exists "pgcrypto";

create table users (
  id uuid primary key default gen_random_uuid(),
  full_name varchar(120) not null,
  username varchar(80) not null unique,
  role varchar(20) not null check (role in ('ADMIN', 'SALES')),
  status varchar(20) not null check (status in ('ACTIVE', 'INACTIVE')),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table products (
  id uuid primary key default gen_random_uuid(),
  sku varchar(64) not null unique,
  name varchar(200) not null,
  unit varchar(40) not null,
  selling_price numeric(18,2) not null check (selling_price >= 0),
  purchase_price numeric(18,2) not null check (purchase_price >= 0),
  stock_qty numeric(18,3) not null check (stock_qty >= 0),
  low_stock_threshold numeric(18,3) not null check (low_stock_threshold >= 0),
  status varchar(20) not null check (status in ('ACTIVE', 'INACTIVE')),
  version bigint not null default 0,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table sale_orders (
  id uuid primary key default gen_random_uuid(),
  order_no varchar(40) not null unique,
  status varchar(20) not null check (status in ('DRAFT', 'COMPLETED', 'CANCELED')),
  subtotal numeric(18,2) not null default 0 check (subtotal >= 0),
  discount_type varchar(20) null check (discount_type in ('PERCENT', 'FIXED')),
  discount_value numeric(18,2) null check (discount_value >= 0),
  discount_amount numeric(18,2) not null default 0 check (discount_amount >= 0),
  total numeric(18,2) not null default 0 check (total >= 0),
  created_by uuid not null references users(id),
  completed_at timestamptz null,
  canceled_at timestamptz null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table sale_order_items (
  id uuid primary key default gen_random_uuid(),
  sale_order_id uuid not null references sale_orders(id) on delete cascade,
  product_id uuid not null references products(id),
  sku_snapshot varchar(64) not null,
  product_name_snapshot varchar(200) not null,
  qty numeric(18,3) not null check (qty > 0),
  unit_price numeric(18,2) not null check (unit_price >= 0),
  purchase_price_snapshot numeric(18,2) null check (purchase_price_snapshot >= 0),
  line_total numeric(18,2) not null check (line_total >= 0)
);

create table stock_ins (
  id uuid primary key default gen_random_uuid(),
  reference_no varchar(40) not null unique,
  status varchar(20) not null check (status in ('DRAFT', 'CONFIRMED', 'CANCELED')),
  note text null,
  created_by uuid not null references users(id),
  confirmed_at timestamptz null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table stock_in_items (
  id uuid primary key default gen_random_uuid(),
  stock_in_id uuid not null references stock_ins(id) on delete cascade,
  product_id uuid not null references products(id),
  qty numeric(18,3) not null check (qty > 0),
  unit_cost numeric(18,2) null check (unit_cost >= 0)
);

create table stock_adjustments (
  id uuid primary key default gen_random_uuid(),
  product_id uuid not null references products(id),
  qty_delta numeric(18,3) not null check (qty_delta <> 0),
  reason varchar(255) not null,
  qty_before numeric(18,3) not null check (qty_before >= 0),
  qty_after numeric(18,3) not null check (qty_after >= 0),
  actor_user_id uuid not null references users(id),
  created_at timestamptz not null default now()
);

create table inventory_transactions (
  id uuid primary key default gen_random_uuid(),
  product_id uuid not null references products(id),
  transaction_type varchar(30) not null check (transaction_type in ('SALE_OUT', 'STOCK_IN', 'ADJUSTMENT', 'SALE_RESTORE')),
  qty_delta numeric(18,3) not null,
  qty_before numeric(18,3) not null check (qty_before >= 0),
  qty_after numeric(18,3) not null check (qty_after >= 0),
  reference_type varchar(30) not null check (reference_type in ('SALE_ORDER', 'STOCK_IN', 'ADJUSTMENT')),
  reference_id uuid not null,
  reason varchar(255) null,
  actor_user_id uuid not null references users(id),
  created_at timestamptz not null default now()
);

create table api_idempotency (
  id uuid primary key default gen_random_uuid(),
  idempotency_key varchar(100) not null unique,
  endpoint varchar(120) not null,
  actor_user_id uuid not null references users(id),
  request_hash varchar(128) not null,
  response_status int not null,
  response_body jsonb not null,
  created_at timestamptz not null default now()
);

create index ix_users_role_status on users(role, status);
create index ix_products_status on products(status);
create index ix_products_low_stock on products(status, stock_qty, low_stock_threshold);
create index ix_sale_orders_status_created_at on sale_orders(status, created_at desc);
create index ix_sale_orders_completed_at on sale_orders(completed_at desc);
create index ix_soi_sale_order_id on sale_order_items(sale_order_id);
create index ix_soi_product_id on sale_order_items(product_id);
create index ix_stock_ins_status_created_at on stock_ins(status, created_at desc);
create index ix_sii_stock_in_id on stock_in_items(stock_in_id);
create index ix_sii_product_id on stock_in_items(product_id);
create index ix_stock_adj_product_created_at on stock_adjustments(product_id, created_at desc);
create index ix_stock_adj_actor_created_at on stock_adjustments(actor_user_id, created_at desc);
create index ix_inv_tx_product_created_at on inventory_transactions(product_id, created_at desc);
create index ix_inv_tx_reference on inventory_transactions(reference_type, reference_id);
create index ix_inv_tx_type_created_at on inventory_transactions(transaction_type, created_at desc);
create index ix_inv_tx_created_at on inventory_transactions(created_at desc);
