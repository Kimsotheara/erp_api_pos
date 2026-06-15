# ERP POS API (Spring Boot)

Backend for the **ERP POS Platform** — a modular ERP + Point of Sale system that
serves multiple business types (Mini Mart, Pharmacy, Pub/Restaurant, …) from one
shared core. Built on consistent conventions: a `NormalizeResponse` envelope, an
`ErrorCode` enum, an `Audit` soft-delete base, MapStruct mappers and a generic
`PageAble` request/response. See [`README1.md`](README1.md) for the product-level
design and module status, and `src/main/resources/erp_schema.sql` for the
reference schema.

## Tech Stack
- Java 17, Spring Boot 3.4
- Spring Data JPA + PostgreSQL (HikariCP)
- Spring Security — **local JWT login** (HS256) **and** OAuth2 Resource Server
- MapStruct + Lombok
- springdoc-openapi (Swagger UI)

## Project Layout
```
com.theara.erp
├─ config/        SecurityConfig, JwtConfig (HS256 encoder/decoder), SwaggerConfig, DataInitializer
├─ common/        PageMapper (shared pagination helper)
├─ constant/      Audit (mapped-superclass), ErrorCode, enums (status types, BusinessType)
├─ entity/        JPA entities (44) — Company … KitchenTicket, User/Role/Permission
├─ repository/    Spring Data JPA repositories
├─ mapper/        MapStruct mappers (entity -> response)
├─ dto/request/   *Request + PageAbleRequest, LoginRequest
├─ dto/response/  *Response + NormalizeResponse, DefaultResponse, PageAbleResponse
├─ dto/projection/ interface projections for report aggregates
├─ service/       Service interfaces + impl/ ; JwtTokenProvider
├─ controller/    REST controllers (/api/v1/**) — 34 controllers
└─ exception/     GlobalExceptionHandler, ApiException, security entry-point/handlers
```

## Running

1. Create the database (or let Hibernate build the tables):
   ```bash
   createdb erp_pos
   ```
   `spring.jpa.hibernate.ddl-auto=update` creates/updates tables from the entities
   on startup. `erp_schema.sql` is kept as a hand-written reference.

2. Adjust DB credentials in `src/main/resources/application.properties` if needed
   (defaults: `postgres` / `123456`, port `5432`, db `erp_pos`).

3. Start the app (defaults to port **8090**):
   ```bash
   ./gradlew bootRun
   ```

On first start a **superadmin** is seeded automatically (see `DataInitializer`):

| | |
| --- | --- |
| Username | `superadmin` |
| Password | `123456` |
| Company | `Default Company` (auto-created) |
| Role | `SUPER_ADMIN` |

> Change these before any real deployment via `erp.security.superadmin-username` /
> `-password` (or `erp.security.seed-superadmin=false` to disable seeding), and
> override `erp.security.jwt.secret`.

## Authentication

The service issues its own **HS256 JWT** on login and validates it as an OAuth2
resource server (the same `erp.security.jwt.secret` signs and verifies). It can
*also* validate tokens from an external authorization server if you set
`spring.security.oauth2.resourceserver.jwt.issuer-uri`.

```bash
# 1) Log in -> get a token
curl -X POST http://localhost:8090/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"superadmin","password":"123456"}'
# -> { "result": true, "resultCode": "0000",
#      "body": { "accessToken": "<JWT>", "tokenType": "Bearer", "expiresIn": 3600, "user": {…} } }

# 2) Call a protected endpoint
curl http://localhost:8090/api/v1/products -H "Authorization: Bearer <JWT>"
```

Token claims: `sub`, `uid`, `companyId`, `roles`, `fullName`, `exp`
(TTL `erp.security.jwt.access-token-ttl-seconds`, default 3600s).
`GET /api/v1/auth/me` echoes the presented token's claims.

### In Swagger UI
1. Run **`Auth → POST /api/v1/auth/login`** and copy `body.accessToken`.
2. Click **Authorize** (top-right), paste the token under **bearerAuth**, Authorize.
3. All requests now send `Authorization: Bearer <token>`.

## Security

Controlled by `erp.security.enabled` in `application.properties`:
- `true` (current default) — every endpoint requires a valid JWT **except** the
  public allow-list: `/api/v1/auth/login`, `/swagger-ui/**`, `/api-docs/**`,
  `/actuator/health|info`.
- `false` — all endpoints open (local exploration without a token).

Unauthenticated (`401`) and forbidden (`403`) responses go through custom handlers
(`RestAuthenticationEntryPoint` / `RestAccessDeniedHandler`) so they return the
**same standard envelope** as everything else, not a bare status.

## Standard Response Envelope

Every response — success or error — has the same shape (`NormalizeResponse`):

```json
{ "result": true, "resultCode": "0000", "resultMessage": "Success", "body": { } }
```

| Case | HTTP | result | resultCode | body |
| --- | --- | --- | --- | --- |
| Success | 200 / 201 | `true` | `0000` | payload |
| Validation failed | 400 | `false` | `0003` | `{ field: message }` |
| Unauthorized (no/invalid token) | 401 | `false` | `0005` | `null` |
| Forbidden | 403 | `false` | `0007` | `null` |
| Not found | 404 | `false` | `0001` | `null` |
| Conflict (duplicate, insufficient stock) | 409 | `false` | `0006`/`0008`… | `null` |
| Unexpected | 500 | `false` | `0004` | `null` |

Controllers return `DefaultResponse.withCode(...)`; all exceptions are normalized
in `GlobalExceptionHandler`; services throw via the `ApiException` factory.
List endpoints accept `pageNumber`, `size`, `sortProperty`, `sortDirection`.

## Swagger / OpenAPI
- UI:   http://localhost:8090/swagger-ui.html
- JSON: http://localhost:8090/api-docs

## Modules & Endpoints (all implemented)

| Area | Base path(s) |
| --- | --- |
| Auth | `/api/v1/auth/login`, `/auth/me` |
| Security | `/users`, `/roles`, `/permissions` |
| Organization | `/companies`, `/branches`, `/currencies`, `/taxes` |
| Product | `/products`, `/categories`, `/brands`, `/units` |
| Inventory | `/warehouses`, `/inventory` |
| Supplier | `/suppliers` |
| Purchasing | `/purchase-orders`, `/goods-receipts` |
| Customer | `/customers` |
| Sales / POS | `/sales`, `/payment-methods` |
| Financial | `/cash-drawers`, `/incomes`, `/expenses` |
| Multi-branch | `/branch-transfers` |
| Pharmacy | `/manufacturers`, `/drug-categories`, `/medicine-batches`, `/prescriptions` |
| Pub / Restaurant | `/tables`, `/reservations`, `/menu-items`, `/kitchen-tickets` |
| Staff | `/shifts`, `/staff-shifts` |
| Reporting | `/reports/sales-summary`, `/top-products`, `/profit`, `/low-stock`, `/expense-summary`, `/dashboard` |

### Sales / POS checkout (verified end-to-end)
`POST /api/v1/sales/checkout` runs the full flow in one transaction: prices each
line (request price or active RETAIL `product_prices`), computes per-line tax
(exclusive) and discounts, generates a per-company invoice number, persists the
invoice with items + payments, sets status (`PAID`/`PARTIAL`/`OPEN`) and change,
then deducts stock via `InventoryService`.

Inventory changes always go through `InventoryService.applyMovement`, which
**pessimistically locks** the `stocks` row, refuses to go negative (409
*Insufficient stock*), keeps a **moving-average cost**, and appends to the
`stock_movements` ledger. An oversell rolls the whole transaction back.

### Workflow endpoints (beyond plain CRUD)
- **Purchasing**: PO `submit` → `approve`/reject; `goods-receipts` post stock-in and advance the PO to `PARTIAL`/`RECEIVED`.
- **Branch transfer**: `ship` (deduct source warehouse) → `receive` (add destination); `cancel` restores in-transit stock.
- **Cash drawer**: `open` → `movements` (pay-in/out) → `close` (computes expected vs counted difference).
- **Pharmacy**: medicine batches post stock-in; `GET /medicine-batches/expiring?withinDays=N` for expiry alerts.
- **Kitchen ticket**: `NEW → PREPARING → READY → SERVED` via `PATCH /{id}/status`.
- **Staff shift**: roster, then `POST /{id}/punch` (CLOCK_IN/OUT/BREAK) — worked minutes = span − breaks.

## Performance — N+1 avoided
List endpoints map lazy `@ManyToOne`/collection associations per row. To keep that
from becoming an N+1 storm, Hibernate batch-fetching is enabled globally:

```properties
spring.jpa.properties.hibernate.default_batch_fetch_size=100
```

This batches lazy loads into `WHERE id IN (…)` queries, so a page of N rows costs a
small constant number of queries instead of `1 + (associations × N)` (measured: a
product list dropped from **29 → 5** queries). `spring.jpa.open-in-view=false` is
set so lazy loading never leaks into JSON serialization.

## Shared / common code (DRY)
- **`NormalizeResponse` / `DefaultResponse`** — the response envelope.
- **`PageAbleRequest` / `PageAbleResponse` / `common.PageMapper`** — pagination.
- **`exception.ApiException`** — factory for `notFound` / `conflict` / `alreadyExists` / `badRequest`.
- **`constant.Audit`** — `is_deleted`, `created_at`, `updated_at` + `@SQLRestriction`.
- **`GlobalExceptionHandler`** + security handlers — one envelope for every error.

## Adding a new module (the pattern)
1. **Entity** in `entity/` extending `Audit`, with `@SQLRestriction("is_deleted = 0")`.
2. **Repository** extending `JpaRepository<Entity, Long>` (+ existence checks).
3. **DTOs**: `XxxRequest` (Bean Validation) and `XxxResponse`.
4. **Mapper**: MapStruct `@Mapper(componentModel = "spring")`. Map foreign keys as
   IDs only and resolve them to managed entities in the service.
5. **Service** interface + `impl` — transactional; use `ApiException.*` for errors
   and `PageMapper.toResponse(page, mapper::toResponse)` for lists.
6. **Controller** `/api/v1/...` returning `DefaultResponse.withCode(...)` with
   Swagger `@Tag`/`@Operation` annotations.
