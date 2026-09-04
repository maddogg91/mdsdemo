package com.maddogg.couponapi.provider;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.maddogg.couponapi.model.Coupon;
import com.maddogg.couponapi.model.DiscountType;
import com.maddogg.couponapi.model.Retailer;

/**
 * Sample/mock Walmart coupons. Walmart's public Affiliate/Marketplace APIs cover
 * product/pricing data rather than consumer coupons, so this provider ships with
 * representative sample data pending a real integration.
 */
@Component
public class WalmartCouponProvider implements CouponProvider {

    @Override
    public Retailer getRetailer() {
        return Retailer.WALMART;
    }

    @Override
    public List<Coupon> fetchCoupons() {
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
