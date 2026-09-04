package com.maddogg.couponapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.maddogg.couponapi.exception.RetailerNotSupportedException;
import com.maddogg.couponapi.model.Coupon;
import com.maddogg.couponapi.model.DiscountType;
import com.maddogg.couponapi.model.Retailer;
import com.maddogg.couponapi.provider.CouponProvider;

class CouponServiceTest {

    private CouponProvider providerFor(Retailer retailer, Coupon... coupons) {
        return new CouponProvider() {
            @Override
            public Retailer getRetailer() {
                return retailer;
            }

            @Override
            public List<Coupon> fetchCoupons() {
                return List.of(coupons);
            }
        };
    }

    private Coupon coupon(String id, Retailer retailer, LocalDate expiration) {
        return Coupon.builder()
                .id(id)
                .retailer(retailer)
                .title("Title " + id)
                .description("Description " + id)
                .discountType(DiscountType.PERCENT_OFF)
                .discountValue(10.0)
                .category("Test")
                .expirationDate(expiration)
                .build();
    }

    @Test
    void getAllCoupons_excludesExpiredByDefault() {
        Coupon active = coupon("active", Retailer.WALMART, LocalDate.now().plusDays(5));
        Coupon expired = coupon("expired", Retailer.WALMART, LocalDate.now().minusDays(1));
        CouponService service = new CouponService(List.of(providerFor(Retailer.WALMART, active, expired)));

        List<Coupon> result = service.getAllCoupons(false);

        assertThat(result).extracting(Coupon::getId).containsExactly("active");
    }

    @Test
    void getAllCoupons_includesExpiredWhenRequested() {
        Coupon active = coupon("active", Retailer.CVS, LocalDate.now().plusDays(5));
        Coupon expired = coupon("expired", Retailer.CVS, LocalDate.now().minusDays(1));
        CouponService service = new CouponService(List.of(providerFor(Retailer.CVS, active, expired)));

        List<Coupon> result = service.getAllCoupons(true);

        assertThat(result).hasSize(2);
    }

    @Test
    void getCouponsForRetailer_throwsForUnknownRetailer() {
        CouponService service = new CouponService(List.of(providerFor(Retailer.CVS)));

        assertThatThrownBy(() -> service.getCouponsForRetailer(Retailer.WALMART, true))
                .isInstanceOf(RetailerNotSupportedException.class);
    }

    @Test
    void searchCoupons_matchesTitleCaseInsensitive() {
        Coupon coupon = Coupon.builder()
                .id("dg-1")
                .retailer(Retailer.DOLLAR_GENERAL)
                .title("Free Shipping Weekend")
                .description("No minimum")
                .discountType(DiscountType.FREE_SHIPPING)
                .discountValue(0)
                .category("Shipping")
                .expirationDate(LocalDate.now().plusDays(2))
                .build();
        CouponService service = new CouponService(List.of(providerFor(Retailer.DOLLAR_GENERAL, coupon)));

        List<Coupon> result = service.searchCoupons("shipping", false);

        assertThat(result).extracting(Coupon::getId).containsExactly("dg-1");
    }
}
