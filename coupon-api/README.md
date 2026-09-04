# coupon-api

A Spring Boot microservice that exposes coupon/deal information for major retailers
(Dollar General, CVS, Walmart) through a single REST API.

## Why sample data

None of these retailers publish a public API for consumer coupons, so each retailer is
backed by a `CouponProvider` implementation that currently returns representative sample
data:

- `DollarGeneralCouponProvider`
- `CvsCouponProvider`
- `WalmartCouponProvider`

To wire up a real source (an affiliate feed, a partner API, a scraper, etc.), implement
`CouponProvider` and annotate it `@Component` — it's picked up automatically by
`CouponService`. Adding a new retailer means adding a `Retailer` enum value plus one new
provider; no other code changes.

## Running

```bash
cd coupon-api
./mvnw spring-boot:run
```

The service starts on `http://localhost:8080`.

## API

| Method | Path                                   | Description                                   |
|--------|-----------------------------------------|------------------------------------------------|
| GET    | `/api/v1/retailers`                     | List supported retailers                        |
| GET    | `/api/v1/coupons`                       | All active coupons across retailers              |
| GET    | `/api/v1/coupons?includeExpired=true`   | Include expired coupons                          |
| GET    | `/api/v1/retailers/{retailer}/coupons`  | Coupons for one retailer (e.g. `walmart`, `cvs`, `dollar_general`) |
| GET    | `/api/v1/coupons/search?query=...`      | Search coupons by title/description/category    |

Interactive API docs (Swagger UI): `http://localhost:8080/swagger-ui.html`

Health check: `http://localhost:8080/actuator/health`

## Tests

```bash
./mvnw test
```
