package com.maddogg.couponapi.provider;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.maddogg.couponapi.model.Coupon;
import com.maddogg.couponapi.model.DiscountType;
import com.maddogg.couponapi.model.Retailer;

/**
 * Sample/mock Dollar General coupons. Dollar General does not expose a public coupon API,
 * so this provider ships with representative sample data. Swap the body of
 * {@link #fetchCoupons()} for a real integration (partner feed, affiliate API, etc.) when
 * one becomes available.
 */
@Component
public class DollarGeneralCouponProvider implements CouponProvider {

    @Override
    public Retailer getRetailer() {
        return Retailer.DOLLAR_GENERAL;
    }

    @Override
    public List<Coupon> fetchCoupons() {
        return List.of(
                Coupon.builder()
                        .id("dg-001")
                        .retailer(Retailer.DOLLAR_GENERAL)
                        .title("$5 off $25 purchase")
                        .description("Save $5 when you spend $25 or more storewide.")
                        .code("DG5OFF25")
                        .discountType(DiscountType.DOLLAR_OFF)
                        .discountValue(5.0)
                        .category("Storewide")
                        .startDate(LocalDate.now().minusDays(3))
                        .expirationDate(LocalDate.now().plusDays(11))
                        .termsUrl("https://www.dollargeneral.com/coupons")
                        .build(),
                Coupon.builder()
                        .id("dg-002")
                        .retailer(Retailer.DOLLAR_GENERAL)
                        .title("20% off household cleaning supplies")
                        .description("Save 20% on select cleaning and paper products.")
                        .code(null)
                        .discountType(DiscountType.PERCENT_OFF)
                        .discountValue(20.0)
                        .category("Household")
                        .startDate(LocalDate.now().minusDays(1))
                        .expirationDate(LocalDate.now().plusDays(6))
                        .termsUrl("https://www.dollargeneral.com/coupons")
                        .build());
    }
}
