-- =====================================================================
-- ERP POS Platform — PostgreSQL Schema
-- Target: PostgreSQL 14+
-- Run:    psql -U postgres -d erp_pos -f erp_schema.sql
--
-- Layout:
--   00  Extensions & enums
--   01  Authentication & Security
--   02  Organization
--   03  Product
--   04  Inventory & Warehouse
--   05  Supplier
--   06  Purchasing
--   07  Customer
--   08  Sales & POS
--   09  Financial
--   10  Multi-Branch
--   11  Pharmacy module
--   12  Pub / Restaurant module
-- =====================================================================

-- ---------------------------------------------------------------------
-- 00  Extensions & enums
-- ---------------------------------------------------------------------
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

DO $$ BEGIN
    CREATE TYPE business_type        AS ENUM ('MINI_MART','PHARMACY','CONVENIENCE','PUB','COFFEE_SHOP','RESTAURANT','RETAIL');
    CREATE TYPE stock_movement_type  AS ENUM ('IN','OUT','TRANSFER','ADJUSTMENT','SALE','PURCHASE','RETURN');
    CREATE TYPE po_status            AS ENUM ('DRAFT','REQUESTED','APPROVED','REJECTED','PARTIAL','RECEIVED','CANCELLED');
    CREATE TYPE invoice_status       AS ENUM ('OPEN','PAID','PARTIAL','VOID','REFUNDED');
    CREATE TYPE payment_status       AS ENUM ('PENDING','COMPLETED','FAILED','REFUNDED');
    CREATE TYPE cash_drawer_status   AS ENUM ('OPEN','CLOSED');
    CREATE TYPE transfer_status      AS ENUM ('DRAFT','IN_TRANSIT','RECEIVED','CANCELLED');
    CREATE TYPE table_status         AS ENUM ('AVAILABLE','OCCUPIED','RESERVED','CLEANING');
    CREATE TYPE kitchen_status       AS ENUM ('NEW','PREPARING','READY','SERVED','CANCELLED');
    CREATE TYPE shift_session_status AS ENUM ('SCHEDULED','OPEN','CLOSED','MISSED','CANCELLED');
    CREATE TYPE attendance_type      AS ENUM ('CLOCK_IN','CLOCK_OUT','BREAK_START','BREAK_END');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- Reusable trigger to maintain updated_at
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS trigger AS $$
BEGIN NEW.updated_at = now(); RETURN NEW; END;
$$ LANGUAGE plpgsql;


-- ---------------------------------------------------------------------
-- 02  Organization  (defined early — referenced by almost everything)
-- ---------------------------------------------------------------------
CREATE TABLE companies (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(150) NOT NULL,
    legal_name      VARCHAR(200),
    business_type   business_type NOT NULL DEFAULT 'MINI_MART',
    tax_number      VARCHAR(50),
    phone           VARCHAR(30),
    email           VARCHAR(120),
    address         TEXT,
    logo_url        VARCHAR(255),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE currencies (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(3) NOT NULL UNIQUE,   -- ISO 4217
    name            VARCHAR(60) NOT NULL,
    symbol          VARCHAR(8),
    exchange_rate   NUMERIC(18,6) NOT NULL DEFAULT 1,
    is_base         BOOLEAN NOT NULL DEFAULT FALSE,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE branches (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    code            VARCHAR(30) NOT NULL,
    name            VARCHAR(150) NOT NULL,
    phone           VARCHAR(30),
    address         TEXT,
    currency_id     BIGINT REFERENCES currencies(id),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (company_id, code)
);

CREATE TABLE taxes (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name            VARCHAR(60) NOT NULL,
    rate            NUMERIC(6,3) NOT NULL,         -- percentage, e.g. 10.000
    is_inclusive    BOOLEAN NOT NULL DEFAULT FALSE,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);


-- ---------------------------------------------------------------------
-- 01  Authentication & Security
-- ---------------------------------------------------------------------
CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    username        VARCHAR(60) NOT NULL,
    email           VARCHAR(120),
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(150),
    phone           VARCHAR(30),
    default_branch_id BIGINT REFERENCES branches(id),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (company_id, username)
);

CREATE TABLE roles (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name            VARCHAR(60) NOT NULL,
    description     VARCHAR(255),
    is_system       BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (company_id, name)
);

CREATE TABLE permissions (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(80) NOT NULL UNIQUE,   -- e.g. PRODUCT_CREATE
    description     VARCHAR(255)
);

CREATE TABLE role_permissions (
    role_id         BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id   BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE user_roles (
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id         BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Multi-branch access: which branches a user may operate in
CREATE TABLE user_branches (
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    branch_id       BIGINT NOT NULL REFERENCES branches(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, branch_id)
);

CREATE TABLE audit_logs (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT REFERENCES companies(id) ON DELETE SET NULL,
    user_id         BIGINT REFERENCES users(id) ON DELETE SET NULL,
    action          VARCHAR(60) NOT NULL,          -- CREATE / UPDATE / DELETE / LOGIN ...
    entity          VARCHAR(80),                   -- table or domain name
    entity_id       VARCHAR(60),
    old_values      JSONB,
    new_values      JSONB,
    ip_address      VARCHAR(45),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_audit_logs_entity ON audit_logs (entity, entity_id);
CREATE INDEX idx_audit_logs_user   ON audit_logs (user_id, created_at);


-- ---------------------------------------------------------------------
-- 03  Product
-- ---------------------------------------------------------------------
CREATE TABLE categories (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    parent_id       BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    name            VARCHAR(120) NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE brands (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name            VARCHAR(120) NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE units (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name            VARCHAR(40) NOT NULL,          -- e.g. Piece, Box, Kg
    abbreviation    VARCHAR(12),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE products (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    sku             VARCHAR(60) NOT NULL,
    barcode         VARCHAR(60),
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    category_id     BIGINT REFERENCES categories(id),
    brand_id        BIGINT REFERENCES brands(id),
    unit_id         BIGINT REFERENCES units(id),
    tax_id          BIGINT REFERENCES taxes(id),
    cost_price      NUMERIC(18,4) NOT NULL DEFAULT 0,
    is_service      BOOLEAN NOT NULL DEFAULT FALSE,
    track_stock     BOOLEAN NOT NULL DEFAULT TRUE,
    reorder_level   NUMERIC(18,3) NOT NULL DEFAULT 0,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (company_id, sku),
    CONSTRAINT uk_products_company_barcode UNIQUE (company_id, barcode)  -- NULLs allowed (multiple); enforced when a barcode is set
);
CREATE INDEX idx_products_barcode ON products (barcode);
CREATE INDEX idx_products_name    ON products (company_id, name);

-- Price tiers / price lists per product
CREATE TABLE product_prices (
    id              BIGSERIAL PRIMARY KEY,
    product_id      BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    price_type      VARCHAR(30) NOT NULL DEFAULT 'RETAIL',  -- RETAIL / WHOLESALE / MEMBER
    price           NUMERIC(18,4) NOT NULL,
    currency_id     BIGINT REFERENCES currencies(id),
    valid_from      DATE,
    valid_to        DATE,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_product_prices_product ON product_prices (product_id, price_type);


-- ---------------------------------------------------------------------
-- 04  Inventory & Warehouse
-- ---------------------------------------------------------------------
CREATE TABLE warehouses (
    id              BIGSERIAL PRIMARY KEY,
    branch_id       BIGINT NOT NULL REFERENCES branches(id) ON DELETE CASCADE,
    code            VARCHAR(30) NOT NULL,
    name            VARCHAR(120) NOT NULL,
    is_default      BOOLEAN NOT NULL DEFAULT FALSE,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (branch_id, code)
);

-- Current quantity on hand per product per warehouse
CREATE TABLE stocks (
    id              BIGSERIAL PRIMARY KEY,
    warehouse_id    BIGINT NOT NULL REFERENCES warehouses(id) ON DELETE CASCADE,
    product_id      BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity        NUMERIC(18,3) NOT NULL DEFAULT 0,
    avg_cost        NUMERIC(18,4) NOT NULL DEFAULT 0,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (warehouse_id, product_id)
);

-- Append-only ledger of every stock change
CREATE TABLE stock_movements (
    id              BIGSERIAL PRIMARY KEY,
    warehouse_id    BIGINT NOT NULL REFERENCES warehouses(id),
    product_id      BIGINT NOT NULL REFERENCES products(id),
    movement_type   stock_movement_type NOT NULL,
    quantity        NUMERIC(18,3) NOT NULL,        -- signed: +in / -out
    unit_cost       NUMERIC(18,4) NOT NULL DEFAULT 0,
    reference_type  VARCHAR(40),                   -- INVOICE / GRN / TRANSFER / ADJUSTMENT
    reference_id    BIGINT,
    note            TEXT,
    created_by      BIGINT REFERENCES users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_stock_mov_product ON stock_movements (product_id, created_at);
CREATE INDEX idx_stock_mov_ref     ON stock_movements (reference_type, reference_id);

CREATE TABLE stock_adjustments (
    id              BIGSERIAL PRIMARY KEY,
    warehouse_id    BIGINT NOT NULL REFERENCES warehouses(id),
    reference_no    VARCHAR(40) NOT NULL,
    reason          VARCHAR(255),
    adjusted_by     BIGINT REFERENCES users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE stock_adjustment_items (
    id              BIGSERIAL PRIMARY KEY,
    adjustment_id   BIGINT NOT NULL REFERENCES stock_adjustments(id) ON DELETE CASCADE,
    product_id      BIGINT NOT NULL REFERENCES products(id),
    quantity_diff   NUMERIC(18,3) NOT NULL,        -- signed
    unit_cost       NUMERIC(18,4) NOT NULL DEFAULT 0
);


-- ---------------------------------------------------------------------
-- 05  Supplier
-- ---------------------------------------------------------------------
CREATE TABLE suppliers (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    code            VARCHAR(30),
    name            VARCHAR(180) NOT NULL,
    tax_number      VARCHAR(50),
    phone           VARCHAR(30),
    email           VARCHAR(120),
    address         TEXT,
    outstanding_balance NUMERIC(18,4) NOT NULL DEFAULT 0,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE supplier_contacts (
    id              BIGSERIAL PRIMARY KEY,
    supplier_id     BIGINT NOT NULL REFERENCES suppliers(id) ON DELETE CASCADE,
    name            VARCHAR(120) NOT NULL,
    position        VARCHAR(80),
    phone           VARCHAR(30),
    email           VARCHAR(120)
);


-- ---------------------------------------------------------------------
-- 06  Purchasing
-- ---------------------------------------------------------------------
CREATE TABLE purchase_orders (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    branch_id       BIGINT NOT NULL REFERENCES branches(id),
    supplier_id     BIGINT NOT NULL REFERENCES suppliers(id),
    po_number       VARCHAR(40) NOT NULL,
    status          po_status NOT NULL DEFAULT 'DRAFT',
    order_date      DATE NOT NULL DEFAULT CURRENT_DATE,
    expected_date   DATE,
    subtotal        NUMERIC(18,4) NOT NULL DEFAULT 0,
    tax_amount      NUMERIC(18,4) NOT NULL DEFAULT 0,
    total_amount    NUMERIC(18,4) NOT NULL DEFAULT 0,
    note            TEXT,
    requested_by    BIGINT REFERENCES users(id),
    approved_by     BIGINT REFERENCES users(id),
    approved_at     TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (company_id, po_number)
);

CREATE TABLE purchase_order_items (
    id              BIGSERIAL PRIMARY KEY,
    purchase_order_id BIGINT NOT NULL REFERENCES purchase_orders(id) ON DELETE CASCADE,
    product_id      BIGINT NOT NULL REFERENCES products(id),
    quantity        NUMERIC(18,3) NOT NULL,
    unit_cost       NUMERIC(18,4) NOT NULL,
    received_qty    NUMERIC(18,3) NOT NULL DEFAULT 0,
    line_total      NUMERIC(18,4) NOT NULL DEFAULT 0
);

CREATE TABLE goods_receipts (
    id              BIGSERIAL PRIMARY KEY,
    purchase_order_id BIGINT REFERENCES purchase_orders(id) ON DELETE SET NULL,
    warehouse_id    BIGINT NOT NULL REFERENCES warehouses(id),
    grn_number      VARCHAR(40) NOT NULL UNIQUE,
    received_date   DATE NOT NULL DEFAULT CURRENT_DATE,
    received_by     BIGINT REFERENCES users(id),
    note            TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE goods_receipt_items (
    id              BIGSERIAL PRIMARY KEY,
    goods_receipt_id BIGINT NOT NULL REFERENCES goods_receipts(id) ON DELETE CASCADE,
    product_id      BIGINT NOT NULL REFERENCES products(id),
    quantity        NUMERIC(18,3) NOT NULL,
    unit_cost       NUMERIC(18,4) NOT NULL DEFAULT 0
);


-- ---------------------------------------------------------------------
-- 07  Customer
-- ---------------------------------------------------------------------
CREATE TABLE customers (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    code            VARCHAR(30),
    name            VARCHAR(180) NOT NULL,
    phone           VARCHAR(30),
    email           VARCHAR(120),
    address         TEXT,
    membership_no   VARCHAR(40),
    membership_tier VARCHAR(30),
    loyalty_balance INTEGER NOT NULL DEFAULT 0,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_customers_phone ON customers (phone);

CREATE TABLE loyalty_points (
    id              BIGSERIAL PRIMARY KEY,
    customer_id     BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    points          INTEGER NOT NULL,              -- signed: earned / redeemed
    reason          VARCHAR(120),
    reference_type  VARCHAR(40),
    reference_id    BIGINT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);


-- ---------------------------------------------------------------------
-- 08  Sales & POS
-- ---------------------------------------------------------------------
CREATE TABLE payment_methods (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name            VARCHAR(60) NOT NULL,          -- Cash / Card / QR / Wallet
    is_cash         BOOLEAN NOT NULL DEFAULT FALSE,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE invoices (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    branch_id       BIGINT NOT NULL REFERENCES branches(id),
    customer_id     BIGINT REFERENCES customers(id),
    invoice_number  VARCHAR(40) NOT NULL,
    status          invoice_status NOT NULL DEFAULT 'OPEN',
    invoice_date    TIMESTAMPTZ NOT NULL DEFAULT now(),
    subtotal        NUMERIC(18,4) NOT NULL DEFAULT 0,
    discount_amount NUMERIC(18,4) NOT NULL DEFAULT 0,
    tax_amount      NUMERIC(18,4) NOT NULL DEFAULT 0,
    total_amount    NUMERIC(18,4) NOT NULL DEFAULT 0,
    paid_amount     NUMERIC(18,4) NOT NULL DEFAULT 0,
    change_amount   NUMERIC(18,4) NOT NULL DEFAULT 0,
    note            TEXT,
    cashier_id      BIGINT REFERENCES users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (company_id, invoice_number)
);
CREATE INDEX idx_invoices_branch_date ON invoices (branch_id, invoice_date);
CREATE INDEX idx_invoices_customer    ON invoices (customer_id);

CREATE TABLE invoice_items (
    id              BIGSERIAL PRIMARY KEY,
    invoice_id      BIGINT NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    product_id      BIGINT NOT NULL REFERENCES products(id),
    description     VARCHAR(200),
    quantity        NUMERIC(18,3) NOT NULL,
    unit_price      NUMERIC(18,4) NOT NULL,
    discount_amount NUMERIC(18,4) NOT NULL DEFAULT 0,
    tax_amount      NUMERIC(18,4) NOT NULL DEFAULT 0,
    line_total      NUMERIC(18,4) NOT NULL DEFAULT 0,
    batch_id        BIGINT   -- FK added after pharmacy tables exist
);
CREATE INDEX idx_invoice_items_product ON invoice_items (product_id);

CREATE TABLE payments (
    id              BIGSERIAL PRIMARY KEY,
    invoice_id      BIGINT NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    payment_method_id BIGINT NOT NULL REFERENCES payment_methods(id),
    amount          NUMERIC(18,4) NOT NULL,
    status          payment_status NOT NULL DEFAULT 'COMPLETED',
    reference_no    VARCHAR(80),
    paid_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);


-- ---------------------------------------------------------------------
-- 09  Financial
-- ---------------------------------------------------------------------
CREATE TABLE cash_drawers (
    id              BIGSERIAL PRIMARY KEY,
    branch_id       BIGINT NOT NULL REFERENCES branches(id),
    opened_by       BIGINT REFERENCES users(id),
    closed_by       BIGINT REFERENCES users(id),
    status          cash_drawer_status NOT NULL DEFAULT 'OPEN',
    opening_balance NUMERIC(18,4) NOT NULL DEFAULT 0,
    closing_balance NUMERIC(18,4),
    expected_balance NUMERIC(18,4),
    difference      NUMERIC(18,4),
    opened_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    closed_at       TIMESTAMPTZ
);

CREATE TABLE cash_movements (
    id              BIGSERIAL PRIMARY KEY,
    cash_drawer_id  BIGINT NOT NULL REFERENCES cash_drawers(id) ON DELETE CASCADE,
    direction       VARCHAR(10) NOT NULL,          -- IN / OUT
    amount          NUMERIC(18,4) NOT NULL,
    reason          VARCHAR(150),
    reference_type  VARCHAR(40),
    reference_id    BIGINT,
    created_by      BIGINT REFERENCES users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE income (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    branch_id       BIGINT REFERENCES branches(id),
    category        VARCHAR(80),
    amount          NUMERIC(18,4) NOT NULL,
    description     VARCHAR(255),
    income_date     DATE NOT NULL DEFAULT CURRENT_DATE,
    created_by      BIGINT REFERENCES users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE expenses (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    branch_id       BIGINT REFERENCES branches(id),
    category        VARCHAR(80),
    amount          NUMERIC(18,4) NOT NULL,
    description     VARCHAR(255),
    expense_date    DATE NOT NULL DEFAULT CURRENT_DATE,
    created_by      BIGINT REFERENCES users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);


-- ---------------------------------------------------------------------
-- 09b  Staff Shift & Attendance
-- ---------------------------------------------------------------------
-- Reusable shift template per branch (e.g. Morning 08:00–16:00)
CREATE TABLE shifts (
    id              BIGSERIAL PRIMARY KEY,
    branch_id       BIGINT NOT NULL REFERENCES branches(id) ON DELETE CASCADE,
    name            VARCHAR(60) NOT NULL,          -- Morning / Evening / Night
    start_time      TIME NOT NULL,
    end_time        TIME NOT NULL,
    crosses_midnight BOOLEAN NOT NULL DEFAULT FALSE,
    break_minutes   INTEGER NOT NULL DEFAULT 0,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (branch_id, name)
);

-- One row per staff member rostered onto a shift for a given date.
-- This is the "support staff shift" record: who works which shift, where, when.
CREATE TABLE staff_shifts (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    branch_id       BIGINT NOT NULL REFERENCES branches(id),
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    shift_id        BIGINT REFERENCES shifts(id),   -- nullable for ad-hoc shifts
    shift_date      DATE NOT NULL,
    status          shift_session_status NOT NULL DEFAULT 'SCHEDULED',
    scheduled_start TIMESTAMPTZ,
    scheduled_end   TIMESTAMPTZ,
    actual_start    TIMESTAMPTZ,                    -- set on first clock-in
    actual_end      TIMESTAMPTZ,                    -- set on final clock-out
    worked_minutes  INTEGER,                        -- computed at close
    cash_drawer_id  BIGINT REFERENCES cash_drawers(id), -- drawer opened for this shift
    note            TEXT,
    created_by      BIGINT REFERENCES users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (user_id, branch_id, shift_date, shift_id)
);
CREATE INDEX idx_staff_shifts_branch_date ON staff_shifts (branch_id, shift_date);
CREATE INDEX idx_staff_shifts_user_date   ON staff_shifts (user_id, shift_date);

-- Clock-in / clock-out / break punches for an assigned shift (audit trail)
CREATE TABLE shift_attendance (
    id              BIGSERIAL PRIMARY KEY,
    staff_shift_id  BIGINT NOT NULL REFERENCES staff_shifts(id) ON DELETE CASCADE,
    event_type      attendance_type NOT NULL,
    event_time      TIMESTAMPTZ NOT NULL DEFAULT now(),
    source          VARCHAR(30),                    -- POS / MOBILE / BIOMETRIC
    note            VARCHAR(255),
    created_by      BIGINT REFERENCES users(id)
);
CREATE INDEX idx_shift_attendance_shift ON shift_attendance (staff_shift_id, event_time);

-- Link a cash drawer session back to the shift that owns it (optional reverse ref)
ALTER TABLE cash_drawers
    ADD COLUMN staff_shift_id BIGINT REFERENCES staff_shifts(id);


-- ---------------------------------------------------------------------
-- 10  Multi-Branch
-- ---------------------------------------------------------------------
-- Aggregated stock per branch (rollup across warehouses)
CREATE TABLE branch_inventory (
    id              BIGSERIAL PRIMARY KEY,
    branch_id       BIGINT NOT NULL REFERENCES branches(id) ON DELETE CASCADE,
    product_id      BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity        NUMERIC(18,3) NOT NULL DEFAULT 0,
    UNIQUE (branch_id, product_id)
);

CREATE TABLE branch_transfers (
    id              BIGSERIAL PRIMARY KEY,
    transfer_no     VARCHAR(40) NOT NULL UNIQUE,
    from_branch_id  BIGINT NOT NULL REFERENCES branches(id),
    to_branch_id    BIGINT NOT NULL REFERENCES branches(id),
    status          transfer_status NOT NULL DEFAULT 'DRAFT',
    transfer_date   DATE NOT NULL DEFAULT CURRENT_DATE,
    received_date   DATE,
    note            TEXT,
    created_by      BIGINT REFERENCES users(id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (from_branch_id <> to_branch_id)
);

CREATE TABLE branch_transfer_items (
    id              BIGSERIAL PRIMARY KEY,
    branch_transfer_id BIGINT NOT NULL REFERENCES branch_transfers(id) ON DELETE CASCADE,
    product_id      BIGINT NOT NULL REFERENCES products(id),
    quantity        NUMERIC(18,3) NOT NULL
);


-- ---------------------------------------------------------------------
-- 11  Pharmacy module
-- ---------------------------------------------------------------------
CREATE TABLE manufacturers (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name            VARCHAR(180) NOT NULL,
    country         VARCHAR(80),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE drug_categories (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name            VARCHAR(120) NOT NULL,         -- Antibiotic / Analgesic ...
    requires_prescription BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE medicine_batches (
    id              BIGSERIAL PRIMARY KEY,
    product_id      BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    warehouse_id    BIGINT NOT NULL REFERENCES warehouses(id),
    manufacturer_id BIGINT REFERENCES manufacturers(id),
    drug_category_id BIGINT REFERENCES drug_categories(id),
    batch_number    VARCHAR(60) NOT NULL,
    manufacture_date DATE,
    expiry_date     DATE NOT NULL,
    quantity        NUMERIC(18,3) NOT NULL DEFAULT 0,
    cost_price      NUMERIC(18,4) NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (product_id, batch_number)
);
CREATE INDEX idx_medicine_batches_expiry ON medicine_batches (expiry_date);

CREATE TABLE prescriptions (
    id              BIGSERIAL PRIMARY KEY,
    invoice_id      BIGINT REFERENCES invoices(id) ON DELETE SET NULL,
    customer_id     BIGINT REFERENCES customers(id),
    doctor_name     VARCHAR(150),
    doctor_license  VARCHAR(80),
    notes           TEXT,
    issued_date     DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Now that medicine_batches exists, link invoice lines to a batch
ALTER TABLE invoice_items
    ADD CONSTRAINT fk_invoice_items_batch
    FOREIGN KEY (batch_id) REFERENCES medicine_batches(id);


-- ---------------------------------------------------------------------
-- 12  Pub / Restaurant module
-- ---------------------------------------------------------------------
CREATE TABLE restaurant_tables (
    id              BIGSERIAL PRIMARY KEY,
    branch_id       BIGINT NOT NULL REFERENCES branches(id) ON DELETE CASCADE,
    name            VARCHAR(40) NOT NULL,          -- T1, Bar-3 ...
    capacity        INTEGER NOT NULL DEFAULT 2,
    status          table_status NOT NULL DEFAULT 'AVAILABLE',
    UNIQUE (branch_id, name)
);

CREATE TABLE reservations (
    id              BIGSERIAL PRIMARY KEY,
    table_id        BIGINT NOT NULL REFERENCES restaurant_tables(id) ON DELETE CASCADE,
    customer_id     BIGINT REFERENCES customers(id),
    customer_name   VARCHAR(150),
    phone           VARCHAR(30),
    party_size      INTEGER NOT NULL DEFAULT 1,
    reserved_from   TIMESTAMPTZ NOT NULL,
    reserved_to     TIMESTAMPTZ,
    note            TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE menu_items (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    product_id      BIGINT REFERENCES products(id),
    name            VARCHAR(150) NOT NULL,
    category        VARCHAR(80),
    price           NUMERIC(18,4) NOT NULL,
    happy_hour_price NUMERIC(18,4),
    is_available    BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE kitchen_tickets (
    id              BIGSERIAL PRIMARY KEY,
    invoice_id      BIGINT REFERENCES invoices(id) ON DELETE SET NULL,
    table_id        BIGINT REFERENCES restaurant_tables(id),
    ticket_number   VARCHAR(40) NOT NULL,
    status          kitchen_status NOT NULL DEFAULT 'NEW',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    served_at       TIMESTAMPTZ
);

CREATE TABLE kitchen_ticket_items (
    id              BIGSERIAL PRIMARY KEY,
    kitchen_ticket_id BIGINT NOT NULL REFERENCES kitchen_tickets(id) ON DELETE CASCADE,
    menu_item_id    BIGINT REFERENCES menu_items(id),
    quantity        NUMERIC(10,2) NOT NULL DEFAULT 1,
    note            VARCHAR(255),                  -- "no ice", "extra spicy"
    status          kitchen_status NOT NULL DEFAULT 'NEW'
);


-- ---------------------------------------------------------------------
-- updated_at triggers
-- ---------------------------------------------------------------------
DO $$
DECLARE t TEXT;
BEGIN
    FOR t IN SELECT unnest(ARRAY[
        'companies','branches','users','products','suppliers',
        'purchase_orders','customers','invoices','staff_shifts'
    ])
    LOOP
        EXECUTE format(
            'CREATE TRIGGER trg_%1$s_updated BEFORE UPDATE ON %1$s
             FOR EACH ROW EXECUTE FUNCTION set_updated_at();', t);
    END LOOP;
END $$;

-- =====================================================================
-- End of schema
-- =====================================================================
