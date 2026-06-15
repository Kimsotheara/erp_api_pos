# ERP POS API (Spring Boot)

Backend for the [ERP POS Platform](../README.md). Built to the shared conventions
of `banking_api` (NormalizeResponse wrapper, `ErrorCode` enum, `Audit` soft-delete
base, MapStruct mappers, PageAble request/response) and the schema in
[`../database/erp_schema.sql`](../database/erp_schema.sql).

## Tech Stack
- Java 17, Spring Boot 3.4
- Spring Data JPA + PostgreSQL
- Spring Security + OAuth2 Resource Server (JWT)
- MapStruct + Lombok
- springdoc-openapi (Swagger UI)

## Project Layout
```
com.theara.erp
├─ config/        SwaggerConfig (OAuth2/JWT), SecurityConfig (toggleable resource server)
├─ constant/      Audit (mapped-superclass), ErrorCode, BusinessType
├─ entity/        Company, Branch, Currency, Tax, Category, Brand, Unit, Product
├─ repository/    Spring Data JPA repositories
├─ mapper/        MapStruct mappers (entity <-> DTO)
├─ dto/request/   *Request + PageAbleRequest
├─ dto/response/  *Response + NormalizeResponse, DefaultResponse, PageAbleResponse
├─ service/       Service interfaces + impl/
├─ controller/    REST controllers (/api/v1/**)
└─ exception/     GlobalExceptionHandler
```

## Running

1. Create the database and load the schema:
   ```bash
   createdb erp_pos
   psql -d erp_pos -f ../database/erp_schema.sql
   ```
   (Or let `spring.jpa.hibernate.ddl-auto=update` create tables from the entities.)

2. Adjust DB credentials in `src/main/resources/application.properties` if needed
   (defaults: `postgres` / `123456`, port `5432`).

3. Start the app (defaults to port **8090**):
   ```bash
   ./gradlew bootRun
   ```

## Swagger / OpenAPI
- UI:   http://localhost:8090/swagger-ui.html
- JSON: http://localhost:8090/api-docs

The OpenAPI doc declares two security schemes:
- **bearerAuth** — paste a raw OAuth2 access token (JWT) into the Authorize dialog.
- **oauth2** — Authorization Code + PKCE flow (set your auth-server URLs in `SwaggerConfig`).

## Security (OAuth2)
Controlled by `erp.security.enabled` in `application.properties`:
- `false` (default, local dev) — all endpoints open so Swagger is browsable without a token.
- `true` — every endpoint except Swagger/actuator requires a valid JWT. Set
  `spring.security.oauth2.resourceserver.jwt.issuer-uri` to your Authorization
  Server (e.g. Keycloak/Auth0) issuer. See `config/SecurityConfig.java`.

## Implemented vs. Scaffolded

| Module | Entity | Repo | API (controller/service) |
| --- | --- | --- | --- |
| Organization | Company | ✅ | ✅ full CRUD |
| Organization | Branch, Currency, Tax | ✅ | entity + repo only |
| Product | Product | ✅ | ✅ full CRUD |
| Product | Category, Brand, Unit, ProductPrice | ✅ | entity + repo only |
| Customer | Customer | ✅ | ✅ full CRUD |
| Inventory | Warehouse | ✅ | ✅ full CRUD |
| Inventory | Stock, StockMovement | ✅ | ✅ stock-in + on-hand |
| Sales / POS | PaymentMethod | ✅ | ✅ full CRUD |
| Sales / POS | Invoice, InvoiceItem, Payment | ✅ | ✅ checkout + get invoice |

### Sales / POS checkout (verified end-to-end)
`POST /api/v1/sales/checkout` runs the full flow in one transaction:
prices each line (request price or active RETAIL `product_prices`), computes
per-line tax (exclusive) and discounts, generates a per-company invoice number,
persists the invoice with items + payments, sets status (`PAID`/`PARTIAL`/`OPEN`)
and change, then deducts stock via `InventoryService`.

Inventory changes always go through `InventoryService.applyMovement`, which
**pessimistically locks** the `stocks` row, refuses to go negative (409
*Insufficient stock*), keeps a **moving-average cost**, and appends to the
`stock_movements` ledger. An oversell rolls the whole sale back.

Still not generated: Purchasing, Financial (cash drawer/expense/income),
Multi-Branch transfers, Pharmacy and Pub/Restaurant modules, and the Auth/User/
Role tables (security is wired but users/roles aren't persisted yet).

## Adding a new module (the pattern)
1. **Entity** in `entity/` extending `Audit`, with `@SQLRestriction("is_deleted = 0")`.
2. **Repository** extending `JpaRepository<Entity, Long>` (+ existence checks).
3. **DTOs**: `XxxRequest` (Bean Validation) and `XxxResponse`.
4. **Mapper**: MapStruct `@Mapper(componentModel = "spring")`. For foreign keys,
   map IDs only and resolve to managed entities in the service (see `ProductServiceImpl`).
5. **Service** interface + `impl` (transactional, throws `ResponseStatusException`).
6. **Controller** `/api/v1/...` returning `DefaultResponse.withCode(...)`.
