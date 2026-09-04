# coupon-api

A Spring Boot microservice that exposes coupon/deal information for major retailers
(Dollar General, CVS, Walmart) through a single REST API.

## Why sample data

None of these retailers publish a public API for consumer coupons, so each retailer is
backed by a `CouponProvider` implementation. `DollarGeneralCouponProvider` and
`CvsCouponProvider` currently return representative sample data. `WalmartCouponProvider` is
wired up to a real source, described below.

To wire up a real source for the other retailers (an affiliate feed, a partner API, a
scraper, etc.), implement `CouponProvider` and annotate it `@Component` — it's picked up
automatically by `CouponService`. Adding a new retailer means adding a `Retailer` enum value
plus one new provider; no other code changes.

## Walmart: Impact.com affiliate feed

`WalmartCouponProvider` pulls Walmart's deal feed from [Impact.com](https://impact.com), the
affiliate network Walmart's affiliate program runs on.

**Setup:**

1. Apply to Walmart's affiliate program (via Impact.com) and get approved.
2. In your Impact.com media partner account, find your **Account SID**, **Auth Token**, and
   Walmart's **Campaign ID** (Program ID).
3. Set these environment variables (or the equivalent `walmart.affiliate.*` properties in
   `application.yml`):

   ```bash
   export WALMART_AFFILIATE_ENABLED=true
   export WALMART_AFFILIATE_ACCOUNT_SID=your-account-sid
   export WALMART_AFFILIATE_AUTH_TOKEN=your-auth-token
   export WALMART_AFFILIATE_CAMPAIGN_ID=walmarts-campaign-id
   ```

With `WALMART_AFFILIATE_ENABLED` unset or `false` (the default), or if any credential is
missing, `WalmartCouponProvider` falls back to sample data — the service always starts and
responds. The same fallback kicks in if a live call to Impact.com fails for any reason.

**A note on the field mapping:** this environment couldn't reach `developer.impact.com` to
verify a live response schema, so the JSON field names `WalmartCouponProvider.mapDeal()`
looks for (`Name`/`Title`, `PromoCode`/`Code`, `StartDate`/`EndDate`, etc.) are a best-effort
guess based on Impact's publicly documented API shape, not a verified payload. Once you have
real credentials, hit the endpoint directly (e.g. with `curl`, Basic Auth as
`AccountSID:AuthToken`) against
`https://api.impact.com/Mediapartners/{AccountSID}/Campaigns/{CampaignId}/Deals`, compare the
actual field names to `mapDeal()`, and adjust — that's the one method that needs it.

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
