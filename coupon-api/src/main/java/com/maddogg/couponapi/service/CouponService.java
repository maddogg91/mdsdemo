package com.maddogg.couponapi.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.maddogg.couponapi.exception.RetailerNotSupportedException;
import com.maddogg.couponapi.model.Coupon;
import com.maddogg.couponapi.model.Retailer;
import com.maddogg.couponapi.provider.CouponProvider;

@Service
public class CouponService {

    private final Map<Retailer, CouponProvider> providersByRetailer;

    public CouponService(List<CouponProvider> providers) {
        this.providersByRetailer = providers.stream()
                .collect(Collectors.toMap(CouponProvider::getRetailer, Function.identity()));
    }

    @Cacheable("coupons")
    public List<Coupon> getAllCoupons(boolean includeExpired) {
        return providersByRetailer.values().stream()
                .flatMap(provider -> provider.fetchCoupons().stream())
                .filter(coupon -> includeExpired || !coupon.isExpired())
                .sorted(Comparator.comparing(Coupon::getExpirationDate,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    @Cacheable("couponsByRetailer")
    public List<Coupon> getCouponsForRetailer(Retailer retailer, boolean includeExpired) {
        CouponProvider provider = providersByRetailer.get(retailer);
        if (provider == null) {
            throw new RetailerNotSupportedException(retailer);
        }
        return provider.fetchCoupons().stream()
                .filter(coupon -> includeExpired || !coupon.isExpired())
                .sorted(Comparator.comparing(Coupon::getExpirationDate,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    public List<Coupon> searchCoupons(String query, boolean includeExpired) {
        String normalized = query.toLowerCase();
        return getAllCoupons(includeExpired).stream()
                .filter(coupon -> coupon.getTitle().toLowerCase().contains(normalized)
                        || coupon.getDescription().toLowerCase().contains(normalized)
                        || (coupon.getCategory() != null && coupon.getCategory().toLowerCase().contains(normalized)))
                .toList();
    }

    public List<Retailer> getSupportedRetailers() {
        return providersByRetailer.keySet().stream()
                .sorted(Comparator.comparing(Enum::name))
                .toList();
    }
}
