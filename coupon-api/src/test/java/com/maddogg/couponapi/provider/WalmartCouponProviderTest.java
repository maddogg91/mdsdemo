package com.maddogg.couponapi.provider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import com.maddogg.couponapi.config.WalmartAffiliateProperties;
import com.maddogg.couponapi.model.Coupon;
import com.maddogg.couponapi.model.Retailer;

class WalmartCouponProviderTest {

    @Test
    void fetchCoupons_fallsBackToSampleDataWhenNotConfigured() {
        WalmartAffiliateProperties properties = new WalmartAffiliateProperties();
        properties.setEnabled(false);
        WalmartCouponProvider provider = new WalmartCouponProvider(properties, RestClient.builder());

        List<Coupon> coupons = provider.fetchCoupons();

        assertThat(coupons).isNotEmpty();
        assertThat(coupons).allMatch(c -> c.getRetailer() == Retailer.WALMART);
    }

    @Test
    void fetchCoupons_fallsBackToSampleDataWhenCredentialsIncomplete() {
        WalmartAffiliateProperties properties = new WalmartAffiliateProperties();
        properties.setEnabled(true);
        properties.setAccountSid("sid");
        // authToken and campaignId intentionally left unset
        WalmartCouponProvider provider = new WalmartCouponProvider(properties, RestClient.builder());

        List<Coupon> coupons = provider.fetchCoupons();

        assertThat(coupons).isNotEmpty();
    }

    @Test
    void getRetailer_isWalmart() {
        WalmartCouponProvider provider = new WalmartCouponProvider(new WalmartAffiliateProperties(), RestClient.builder());

        assertThat(provider.getRetailer()).isEqualTo(Retailer.WALMART);
    }
}
