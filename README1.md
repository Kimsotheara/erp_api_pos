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
- Login via **OAuth2 / OpenID Connect** (Authorization Code + PKCE for the SPA)
- Social / external identity providers (Google, Microsoft, Apple) + local password fallback
- **JWT** access tokens (short-lived) with refresh-token rotation
- Multi-branch access, scope/claim-based authorization
- User / Role / Permission management
- Audit logging
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
- Stock in / out / transfer / adjustment
- Stock movement history, warehouse management
- **Tables:** `warehouses`, `stocks`, `stock_movements`, `stock_adjustments`

### Supplier Management
- Supplier registration, purchase history, outstanding balance
- **Tables:** `suppliers`, `supplier_contacts`

### Purchasing
- **Tables:** `purchase_orders`, `purchase_order_items`, `goods_receipts`, `goods_receipt_items`

### Customer Management
- Customer profile, loyalty points, membership, purchase history
- **Tables:** `customers`, `loyalty_points`

### Sales & POS
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
| Inventory & Warehouse | ✅ | `/api/v1/warehouses`, `/api/v1/inventory` |
| Supplier | ✅ | `/api/v1/suppliers` |
| Purchasing — Purchase Order | ✅ | `/api/v1/purchase-orders` |
| Purchasing — Goods Receipt | ✅ | `/api/v1/goods-receipts` |
| Customer | ✅ | `/api/v1/customers` |
| Sales & POS | ✅ | `/api/v1/sales` |
| Payment Method | ✅ | `/api/v1/payment-methods` |
| Financial — Cash Drawer | ✅ | `/api/v1/cash-drawers` |
| Financial — Income | ✅ | `/api/v1/incomes` |
| Financial — Expense | ✅ | `/api/v1/expenses` |
| Security — User | ✅ | `/api/v1/users` |
| Security — Role | ✅ | `/api/v1/roles` |
| Security — Permission | ✅ | `/api/v1/permissions` |
| Authentication — token issuance / external IdP | 🟡 | resource-server only (JWT validated, issued externally) |
| Multi-Branch Transfer | ⬜ | — |
| Pharmacy module | ⬜ | — |
| Pub / Restaurant module | ⬜ | — |
| Staff Shift & Attendance | ⬜ | — |
| Reporting / Analytics | ⬜ | — |

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
- Token **validation** is handled here as an OAuth2 resource server
  (`erp.security.enabled=true` + `issuer-uri`); token **issuance** and external IdP
  login are delegated to a separate authorization server (not in this service).

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
