package com.maddogg.couponapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maddogg.couponapi.model.Coupon;
import com.maddogg.couponapi.model.Retailer;
import com.maddogg.couponapi.service.CouponService;

@RestController
@RequestMapping("/api/v1")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/retailers")
    public List<Retailer> getSupportedRetailers() {
        return couponService.getSupportedRetailers();
    }

    @GetMapping("/coupons")
    public List<Coupon> getAllCoupons(
            @RequestParam(name = "includeExpired", defaultValue = "false") boolean includeExpired) {
        return couponService.getAllCoupons(includeExpired);
    }

    @GetMapping("/coupons/search")
    public List<Coupon> searchCoupons(
            @RequestParam("query") String query,
            @RequestParam(name = "includeExpired", defaultValue = "false") boolean includeExpired) {
        return couponService.searchCoupons(query, includeExpired);
    }

    @GetMapping("/retailers/{retailer}/coupons")
    public List<Coupon> getCouponsForRetailer(
            @PathVariable("retailer") String retailer,
            @RequestParam(name = "includeExpired", defaultValue = "false") boolean includeExpired) {
        Retailer resolved = resolveRetailer(retailer);
        return couponService.getCouponsForRetailer(resolved, includeExpired);
    }

    private Retailer resolveRetailer(String value) {
        try {
            return Retailer.valueOf(value.trim().toUpperCase().replace('-', '_').replace(' ', '_'));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown retailer: " + value);
        }
    }
}
