# ERP POS Platform

A modular **ERP + Point of Sale** platform that can be configured for multiple
business types from a single shared core. Industry-specific modules are enabled
or disabled through configuration, so the same codebase powers a Mini Mart, a
Pharmacy, or a Pub without forking.

## Supported Business Types

| Type | Core ERP | Industry Module |
| --- | --- | --- |
| Mini Mart | ✅ | Mini Mart |
| Pharmacy | ✅ | Pharmacy |
| Convenience Store | ✅ | Mini Mart |
| Pub & Bar | ✅ | Pub / Restaurant |
| Coffee Shop | ✅ | Pub / Restaurant |
| Restaurant | ✅ | Pub / Restaurant |
| Retail Shop | ✅ | Mini Mart |

> The **core** stays identical across every deployment. A business profile flag
> (`business_type`) drives which optional modules load at runtime.

 

# Core ERP Modules

Shared by **all** business types.

### Authentication & Security
- **Local login** (`POST /api/v1/auth/login`) — username/password against a BCrypt hash, returns a signed HS256 **JWT**
- **Current token claims** (`GET /api/v1/auth/me`) — echoes the presented token's claims
- **Self-service password change** (`POST /api/v1/auth/change-password`) — verifies the current password, rejects reusing it, re-hashes with BCrypt
- Login via **OAuth2 / OpenID Connect** (Authorization Code + PKCE for the SPA) — resource server also validates external-issuer JWTs
- Social / external identity providers (Google, Microsoft, Apple) + local password fallback
- **JWT** access tokens (short-lived, default TTL 3600s); refresh-token rotation *(planned)*
- Multi-branch access, scope/claim-based authorization
- User / Role / Permission management (permissions catalog → role → user, per company)
- Audit logging (soft-delete `is_deleted`, `created_at` / `updated_at` on every entity)
- **Tables:** `users`, `roles`, `permissions`, `role_permissions`, `user_roles`, `audit_logs`, `oauth_clients`, `oauth_accounts`, `refresh_tokens`

#### OAuth2 Login Flow

```
User → Vue SPA  ──(1) /authorize (Auth Code + PKCE)──▶  Auth Server (Spring Security)
                ◀─(2) authorization code ───────────────
SPA → Backend   ──(3) exchange code + verifier ────────▶  /token
                ◀─(4) access JWT + refresh token ────────
SPA → API       ──(5) Bearer access JWT ───────────────▶  Resource Server
                                                          validate signature + scopes/roles
                ──(6) refresh when expired ─────────────▶  /token (rotate refresh)
```

**Notes**
- **Authorization Code + PKCE** is used for the browser SPA (no client secret in the front end).
- Access tokens are stateless JWTs validated by the resource server via the auth server's JWKS endpoint; refresh tokens are persisted (`refresh_tokens`) so they can be revoked.
- External IdP identities are linked to local users through `oauth_accounts` (provider + provider_user_id), enabling both SSO and local login.
- Roles/permissions are emitted as JWT claims, then enforced by Spring Security method/route guards.

### Organization Management
- Company & branch setup, business profile
- Currency and tax configuration
- **Tables:** `companies`, `branches`, `currencies`, `taxes`

### Product Management
- Product CRUD, category, brand, unit of measure
- Barcode and price management
- **Tables:** `products`, `categories`, `brands`, `units`, `product_prices`

### Inventory Management
- **Stock in** (`POST /api/v1/inventory/stock-in`) — receive goods, raises on-hand and updates moving-average cost
- **Stock out** (`POST /api/v1/inventory/stock-out`) — manual issue / write-off, refuses to go negative (409 *Insufficient stock*)
- **Stock adjustment / stock-take** (`POST /api/v1/inventory/adjust`) — set on-hand to a physically counted quantity; the difference is posted as an `ADJUSTMENT` and the moving-average cost is preserved
- **On-hand query** (`GET /api/v1/inventory/on-hand`) — current quantity & average cost for a product in a warehouse
- **Stock movement ledger** (`GET /api/v1/inventory/movements`) — paginated history, filterable by `warehouseId` / `productId`
- Warehouse management (`/api/v1/warehouses`)
- Every quantity change goes through one **`InventoryService.applyMovement`** that pessimistically locks the stock row, blocks overselling, keeps a moving-average cost, and appends to the `stock_movements` ledger — so a failed line rolls the whole transaction back
- **Movement types:** `IN`, `OUT`, `TRANSFER`, `ADJUSTMENT`, `SALE`, `PURCHASE`, `RETURN`
- **Tables:** `warehouses`, `stocks`, `stock_movements`

### Supplier Management
- Supplier registration, purchase history, outstanding balance
- **Tables:** `suppliers`, `supplier_contacts`

### Purchasing
- **Tables:** `purchase_orders`, `purchase_order_items`, `goods_receipts`, `goods_receipt_items`

### Customer Management
- Customer profile, loyalty points, membership, purchase history
- **Tables:** `customers`, `loyalty_points`

### Sales & POS
- **Checkout** (`POST /api/v1/sales/checkout`) — prices the cart (request price or active `RETAIL` product price), computes per-line tax (exclusive) and discounts, generates a per-company invoice number, persists the invoice with items + payments, sets status (`PAID` / `PARTIAL` / `OPEN`) and change, then deducts stock — all in one transaction
- **Invoice lookup & history** (`GET /api/v1/sales/invoices/{id}`, `GET /api/v1/sales/invoices`)
- **Void** (`POST /api/v1/sales/invoices/{id}/void`) — whole-invoice reversal: restocks every line (and its medicine batch) and sets the invoice to `VOID`
- **Return / refund** (`POST /api/v1/sales/invoices/{id}/return`) — **partial or full** return: restocks the returned quantity of each selected line, refunds the proportional line value (tax included, net of line discount), tracks each line's `returned_quantity` and the invoice's `refunded_amount`, and moves the invoice to `PARTIALLY_REFUNDED` or `REFUNDED`. Over-returning a line is rejected (409)
- **Payment methods** (`/api/v1/payment-methods`) — cash, card, transfer, etc.; an invoice can split across several tenders
- **Invoice statuses:** `OPEN`, `PAID`, `PARTIAL`, `VOID`, `PARTIALLY_REFUNDED`, `REFUNDED`
- **Tables:** `invoices`, `invoice_items`, `payments`, `payment_methods`

### Financial Management
- Income & expense tracking, cash flow, daily closing
- **Tables:** `expenses`, `income`, `cash_drawers`, `cash_movements`

### Reporting
- Sales · Profit · Expense · Inventory · Purchase · Customer · Audit reports

---

# Core Workflows

### Purchasing Flow

```
Purchase Request → Purchase Order → Approval → Goods Receipt → Inventory Update
```

### Sales / POS Flow

```
Scan Product → Cart → Discount → Payment → Receipt → Inventory Deduction
```

### Returns / Refund Flow

```
Open Invoice → Select Lines + Quantity → Restock Goods → Refund Proportional Value
        → Invoice becomes PARTIALLY_REFUNDED or REFUNDED
```

---

# Feature Modules

## Pharmacy Module
*Enabled only for Pharmacy businesses.*

**Features:** batch tracking, expiry-date monitoring, prescription medicine,
drug classification, manufacturer tracking.

**Tables:** `medicine_batches`, `prescriptions`, `drug_categories`, `manufacturers`

**Workflow:**
```
Receive Medicine → Assign Batch → Track Expiry → Sell Medicine → Reduce Batch Quantity
```

## Pub / Restaurant Module
*Enabled only for Pub, Bar, Restaurant, Coffee Shop.*

**Features:** table management, reservations, kitchen order tickets, split bill,
happy-hour pricing, menu management.

**Tables:** `tables`, `reservations`, `kitchen_tickets`, `menu_items`

**Workflow:**
```
Open Table → Create Order → Send To Kitchen → Serve Customer → Payment → Close Table
```

## Mini Mart Module
*Enabled only for Mini Mart, Convenience Store, Retail Shop.*

**Features:** fast checkout, barcode scanner, loyalty program, promotions, combo packages.

**Workflow:**
```
Scan Product → Cart → Payment → Receipt
```

---

# Multi-Branch Support

- Branch inventory, inter-branch transfer, branch sales report, centralized dashboard
- **Tables:** `branches`, `branch_inventory`, `branch_transfers`

---

# Analytics Dashboard

Widgets: Today's Sales · Top Selling Products · Low Stock Alert · Expiring
Medicines · Revenue by Branch · Customer Growth · Profit Trend.

 
# Technology Stack

**Frontend:** Vue 3 · Pinia · Tailwind CSS
**Backend:** Spring Boot 3 · Java 17 · PostgreSQL
**Infrastructure:** Docker · Nginx · Ubuntu
**Reports:** JasperReports · Excel Export · PDF Export
 

# Implementation Status (Backend API)

Legend: ✅ implemented · 🟡 partial · ⬜ planned

| Module | Status | Base path |
| --- | --- | --- |
| Organization — Company | ✅ | `/api/v1/companies` |
| Organization — Branch | ✅ | `/api/v1/branches` |
| Organization — Currency | ✅ | `/api/v1/currencies` |
| Organization — Tax | ✅ | `/api/v1/taxes` |
| Product — Catalog | ✅ | `/api/v1/products` |
| Product — Category | ✅ | `/api/v1/categories` |
| Product — Brand | ✅ | `/api/v1/brands` |
| Product — Unit | ✅ | `/api/v1/units` |
| Inventory & Warehouse | ✅ | `/api/v1/warehouses`, `/api/v1/inventory` (stock-in / stock-out / adjust / on-hand / movements) |
| Supplier | ✅ | `/api/v1/suppliers` |
| Purchasing — Purchase Order | ✅ | `/api/v1/purchase-orders` |
| Purchasing — Goods Receipt | ✅ | `/api/v1/goods-receipts` |
| Customer | ✅ | `/api/v1/customers` |
| Sales & POS | ✅ | `/api/v1/sales` (checkout / invoices / void / return) |
| Payment Method | ✅ | `/api/v1/payment-methods` |
| Financial — Cash Drawer | ✅ | `/api/v1/cash-drawers` |
| Financial — Income | ✅ | `/api/v1/incomes` |
| Financial — Expense | ✅ | `/api/v1/expenses` |
| Security — User | ✅ | `/api/v1/users` |
| Security — Role | ✅ | `/api/v1/roles` |
| Security — Permission | ✅ | `/api/v1/permissions` |
| Authentication — local login (JWT issuance) | ✅ | `/api/v1/auth/login`, `/api/v1/auth/me`, `/api/v1/auth/change-password` |
| Authentication — refresh-token rotation | ⬜ | planned (`refresh_tokens`) |
| Authentication — external IdP / SSO | 🟡 | resource-server also validates external-issuer JWTs |
| Multi-Branch Transfer | ✅ | `/api/v1/branch-transfers` |
| Pharmacy — Manufacturer | ✅ | `/api/v1/manufacturers` |
| Pharmacy — Drug Category | ✅ | `/api/v1/drug-categories` |
| Pharmacy — Medicine Batch (expiry) | ✅ | `/api/v1/medicine-batches` |
| Pharmacy — Prescription | ✅ | `/api/v1/prescriptions` |
| Pub/Restaurant — Table | ✅ | `/api/v1/tables` |
| Pub/Restaurant — Reservation | ✅ | `/api/v1/reservations` |
| Pub/Restaurant — Menu Item | ✅ | `/api/v1/menu-items` |
| Pub/Restaurant — Kitchen Ticket | ✅ | `/api/v1/kitchen-tickets` |
| Staff — Shift template | ✅ | `/api/v1/shifts` |
| Staff — Shift roster & attendance | ✅ | `/api/v1/staff-shifts` |
| Reporting / Analytics | ✅ | `/api/v1/reports` |

Every endpoint follows the same conventions:
- Standard envelope `{ result, resultCode, resultMessage, body }` (`NormalizeResponse`).
- List endpoints accept `pageNumber`, `size`, `sortProperty`, `sortDirection` query params.
- Soft delete (`is_deleted` flag) — `DELETE` never physically removes a row.
- Bean-validation on request bodies; errors surface via the global exception handler.
- Interactive docs at `/swagger-ui.html` (OpenAPI at `/api-docs`).

### Procurement → Inventory flow (implemented)

```
Create PO (DRAFT) → submit (REQUESTED) → approve (APPROVED)
        → Goods Receipt → stock-in via InventoryService (PURCHASE movement)
        → PO becomes PARTIAL or RECEIVED, product cost updated
```

### Inventory operations (implemented)

```
Stock-in (IN, +qty, recompute avg cost) ─┐
Stock-out (OUT, −qty, block oversell)  ──┼─▶ applyMovement (lock row, ledger entry)
Adjust to counted qty (ADJUSTMENT, ±Δ) ──┘    → stock_movements
On-hand query        → current quantity & avg cost
Movements ledger     → paginated history (filter by warehouse / product)
```

### Sales / POS checkout & returns (implemented)

```
Checkout: price cart → tax + discount → invoice no. → persist items + payments
        → status PAID / PARTIAL / OPEN + change → deduct stock (SALE movement)

Void:   restock all lines (RETURN) → status VOID
Return: restock selected lines + qty (RETURN) → refund proportional value
        → returned_quantity / refunded_amount tracked
        → status PARTIALLY_REFUNDED or REFUNDED (over-return rejected)
```

### Cash drawer flow (implemented)

```
Open drawer (float) → pay-in / pay-out movements → Close drawer
        → expected = opening + Σ(IN) − Σ(OUT); difference = counted − expected
```

### Identity & access (implemented)

- `Permission` (global capability catalog, e.g. `PRODUCT_CREATE`) → assigned to
  `Role` (per company) → assigned to `User` (per company), plus per-user branch access.
- User passwords use the **local-password fallback**: stored BCrypt-hashed in
  `users.password_hash` and never returned by the API.
- **Local login** (`POST /api/v1/auth/login`) authenticates username/password against
  the BCrypt hash and returns a signed **HS256 JWT** (claims: `sub`, `uid`, `companyId`,
  `roles`, `fullName`, `exp`). The same `erp.security.jwt.secret` signs and validates,
  so login-issued tokens pass the resource server when `erp.security.enabled=true`.
- **Password change** (`POST /api/v1/auth/change-password`) lets the authenticated
  user (identified by the `uid` claim) rotate their own password after verifying the
  current one; the new hash replaces `users.password_hash` and the same value can never
  be re-set as "new".
- On first startup a **SUPER_ADMIN** is seeded (`erp.security.seed-superadmin=true`):
  default `superadmin` / `123456` under a "Default Company" — change these in production.
- The resource server can *also* validate JWTs from an external authorization server
  (set `spring.security.oauth2.resourceserver.jwt.issuer-uri`) for SSO / external IdP.

```bash
# Log in and grab a token
curl -X POST http://localhost:8090/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"superadmin","password":"123456"}'
# -> { "body": { "accessToken": "<JWT>", "tokenType": "Bearer", "expiresIn": 3600, "user": {...} } }
```

### Inter-branch transfer flow (implemented)

```
Create transfer (DRAFT) → ship (IN_TRANSIT, deduct source warehouse)
        → receive (RECEIVED, add destination warehouse)
        → cancel restores in-transit stock to the source
```

### Pharmacy flow (implemented)

```
Receive medicine → create batch (stock-in via InventoryService)
        → track expiry (GET /medicine-batches/expiring?withinDays=N)
        → sell (invoice line carries batch_id) → record prescription
```

### Pub / Restaurant flow (implemented)

```
Open table (status OCCUPIED) → kitchen ticket (NEW)
        → PREPARING → READY → SERVED (served_at stamped)
        → payment via Sales → free table (status AVAILABLE)
```

### Staff shift & attendance flow (implemented)

```
Define shift template → roster staff (SCHEDULED)
        → punch CLOCK_IN (OPEN) → BREAK_START/BREAK_END
        → punch CLOCK_OUT (CLOSED): worked_minutes = span − paired breaks
```

### Reporting (implemented)

`GET /api/v1/reports/...` — `sales-summary`, `top-products`, `profit`
(revenue − COGS via product cost), `low-stock` (on-hand ≤ reorder level),
`expense-summary` (by category), and `dashboard` (today + month-to-date
snapshot with top products and low-stock list). All are read-only JPQL
aggregates scoped by `companyId` and a date range.

# Getting Started

> Scaffolding placeholder — update commands once the repos are initialized.

```bash
# Backend (Spring Boot 3 / Java 17)
cd backend
./gradlew bootRun

# Frontend (Vue 3 / Vite)
cd frontend
npm install
npm run dev

# Full stack via Docker
docker compose up -d
```

## Configuration

Set the active business type to control which feature modules load:

```yaml
# application.yml
erp:
  business-type: MINI_MART   # MINI_MART | PHARMACY | PUB | RESTAURANT | ...
  modules:
    pharmacy: false
    pub: false
    mini-mart: true
```
