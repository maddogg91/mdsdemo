package com.maddogg.couponapi.provider;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.maddogg.couponapi.model.Coupon;
import com.maddogg.couponapi.model.DiscountType;
import com.maddogg.couponapi.model.Retailer;

/**
 * Sample/mock CVS coupons. CVS ExtraCare deals are normally delivered through the CVS
 * app/account, not a public API, so this provider ships with representative sample data
 * pending a real integration (e.g. an affiliate feed or an authenticated ExtraCare
 * integration).
 */
@Component
public class CvsCouponProvider implements CouponProvider {

    @Override
    public Retailer getRetailer() {
        return Retailer.CVS;
    }

    @Override
    public List<Coupon> fetchCoupons() {
        return List.of(
                Coupon.builder()
                        .id("cvs-001")
                        .retailer(Retailer.CVS)
                        .title("Extra 30% off one item")
                        .description("Extra 30% off any single regular-priced item with ExtraCare card.")
                        .code("CVS30")
                        .discountType(DiscountType.PERCENT_OFF)
                        .discountValue(30.0)
                        .category("ExtraCare")
                        .startDate(LocalDate.now().minusDays(2))
                        .expirationDate(LocalDate.now().plusDays(4))
                        .termsUrl("https://www.cvs.com/coupons")
                        .build(),
                Coupon.builder()
                        .id("cvs-002")
                        .retailer(Retailer.CVS)
                        .title("$3 ExtraBucks on vitamins")
                        .description("Earn $3 in ExtraBucks Rewards when you spend $15 on vitamins.")
                        .code(null)
                        .discountType(DiscountType.DOLLAR_OFF)
                        .discountValue(3.0)
                        .category("Health")
                        .startDate(LocalDate.now())
                        .expirationDate(LocalDate.now().plusDays(13))
                        .termsUrl("https://www.cvs.com/coupons")
                        .build());
    }
}
