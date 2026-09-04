package com.maddogg.couponapi.provider;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.maddogg.couponapi.config.WalmartAffiliateProperties;
import com.maddogg.couponapi.model.Coupon;
import com.maddogg.couponapi.model.DiscountType;
import com.maddogg.couponapi.model.Retailer;

/**
 * Pulls Walmart's affiliate deal feed from Impact.com (the affiliate network Walmart's
 * affiliate program runs on) and maps it into {@link Coupon}s.
 *
 * <p>Impact.com uses HTTP Basic auth (AccountSID as username, AuthToken as password) and
 * exposes deals per campaign at
 * {@code GET https://api.impact.com/Mediapartners/{AccountSID}/Campaigns/{CampaignId}/Deals}.
 * The exact JSON field names below are a best effort based on Impact's published API shape;
 * this environment could not reach impact.com's docs directly to verify a live response, so
 * treat {@link #mapDeal(JsonNode)} as the one place to adjust once you can inspect a real
 * payload from your own Impact.com media partner account.
 *
 * <p>When {@code walmart.affiliate.enabled} is false or credentials are missing, this falls
 * back to bundled sample data so the rest of the service keeps working. The same fallback
 * kicks in if the live call fails for any reason (network, auth, unexpected shape).
 */
@Component
public class WalmartCouponProvider implements CouponProvider {

    private static final Logger log = LoggerFactory.getLogger(WalmartCouponProvider.class);

    private final WalmartAffiliateProperties properties;
    private final RestClient restClient;

    public WalmartCouponProvider(WalmartAffiliateProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClient = restClientBuilder.baseUrl(properties.getBaseUrl()).build();
    }

    @Override
    public Retailer getRetailer() {
        return Retailer.WALMART;
    }

    @Override
    public List<Coupon> fetchCoupons() {
        if (!properties.isConfigured()) {
            log.debug("Walmart affiliate feed not configured (walmart.affiliate.*); using sample data");
            return sampleCoupons();
        }
        try {
            List<Coupon> coupons = fetchFromImpact();
            return coupons.isEmpty() ? sampleCoupons() : coupons;
        } catch (Exception ex) {
            log.warn("Failed to fetch Walmart deals from Impact.com, falling back to sample data", ex);
            return sampleCoupons();
        }
    }

    private List<Coupon> fetchFromImpact() {
        String path = "/Mediapartners/{accountSid}/Campaigns/{campaignId}/Deals?PageSize={pageSize}";
        JsonNode response = restClient.get()
                .uri(path, properties.getAccountSid(), properties.getCampaignId(), properties.getPageSize())
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeader())
                .header(HttpHeaders.ACCEPT, "application/json")
                .retrieve()
                .body(JsonNode.class);

        if (response == null) {
            return List.of();
        }

        JsonNode deals = response.has("Deals") ? response.get("Deals") : response;
        if (!deals.isArray()) {
            log.warn("Unexpected Impact.com Deals response shape; skipping this fetch");
            return List.of();
        }

        List<Coupon> coupons = new ArrayList<>();
        for (JsonNode deal : deals) {
            Coupon coupon = mapDeal(deal);
            if (coupon != null) {
                coupons.add(coupon);
            }
        }
        return coupons;
    }

    private String basicAuthHeader() {
        String credentials = properties.getAccountSid() + ":" + properties.getAuthToken();
        return "Basic " + java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    /** Maps one Impact.com "Deal" JSON node into a {@link Coupon}. See class javadoc. */
    private Coupon mapDeal(JsonNode deal) {
        String id = text(deal, "Id", "DealId");
        String title = text(deal, "Name", "Title", "Description");
        if (id == null || title == null) {
            return null;
        }
        String description = Objects.requireNonNullElse(text(deal, "Description", "Name"), title);

        return Coupon.builder()
                .id("wmt-" + id)
                .retailer(Retailer.WALMART)
                .title(title)
                .description(description)
                .code(text(deal, "PromoCode", "Code", "CouponCode"))
                .discountType(inferDiscountType(description))
                .discountValue(number(deal, "Value", "PercentOff", "AmountOff"))
                .category(text(deal, "Category"))
                .startDate(date(deal, "StartDate"))
                .expirationDate(date(deal, "EndDate", "ExpirationDate"))
                .termsUrl(text(deal, "TrackingLink", "Url"))
                .build();
    }

    private DiscountType inferDiscountType(String description) {
        String normalized = description.toLowerCase(Locale.ROOT);
        if (normalized.contains("free shipping")) {
            return DiscountType.FREE_SHIPPING;
        }
        if (normalized.contains("buy one") || normalized.contains("bogo")) {
            return DiscountType.BOGO;
        }
        if (normalized.contains("%")) {
            return DiscountType.PERCENT_OFF;
        }
        if (normalized.contains("$")) {
            return DiscountType.DOLLAR_OFF;
        }
        return DiscountType.OTHER;
    }

    private String text(JsonNode node, String... fieldNames) {
        for (String field : fieldNames) {
            JsonNode value = node.get(field);
            if (value != null && !value.isNull() && !value.asText().isBlank()) {
                return value.asText();
            }
        }
        return null;
    }

    private double number(JsonNode node, String... fieldNames) {
        for (String field : fieldNames) {
            JsonNode value = node.get(field);
            if (value != null && value.isNumber()) {
                return value.asDouble();
            }
        }
        return 0.0;
    }

    private LocalDate date(JsonNode node, String... fieldNames) {
        String raw = text(node, fieldNames);
        if (raw == null) {
            return null;
        }
        try {
            return LocalDate.parse(raw.substring(0, Math.min(10, raw.length())));
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private List<Coupon> sampleCoupons() {
        return List.of(
                Coupon.builder()
                        .id("wmt-001")
                        .retailer(Retailer.WALMART)
                        .title("Free shipping, no minimum")
                        .description("Free standard shipping on eligible online orders, no minimum spend.")
                        .code("FREESHIP")
                        .discountType(DiscountType.FREE_SHIPPING)
                        .discountValue(0.0)
                        .category("Shipping")
                        .startDate(LocalDate.now().minusDays(5))
                        .expirationDate(LocalDate.now().plusDays(9))
                        .termsUrl("https://www.walmart.com/coupons")
                        .build(),
                Coupon.builder()
                        .id("wmt-002")
                        .retailer(Retailer.WALMART)
                        .title("Buy One Get One Free - Snacks")
                        .description("Buy one get one free on select snack brands.")
                        .code(null)
                        .discountType(DiscountType.BOGO)
                        .discountValue(100.0)
                        .category("Grocery")
                        .startDate(LocalDate.now().minusDays(1))
                        .expirationDate(LocalDate.now().plusDays(2))
                        .termsUrl("https://www.walmart.com/coupons")
                        .build());
    }
}
