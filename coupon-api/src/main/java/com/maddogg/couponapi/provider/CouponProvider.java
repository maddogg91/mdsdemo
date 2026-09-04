package com.maddogg.couponapi.provider;

import java.util.List;

import com.maddogg.couponapi.model.Coupon;
import com.maddogg.couponapi.model.Retailer;

/**
 * A source of coupon data for a single retailer. Implementations decide how the data is
 * actually obtained (a retailer's public API, an affiliate feed, scraping, or - as a
 * starting point - static sample data).
 */
public interface CouponProvider {

    Retailer getRetailer();

    List<Coupon> fetchCoupons();
}
