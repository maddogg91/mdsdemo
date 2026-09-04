package com.maddogg.couponapi.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    private String id;
    private Retailer retailer;
    private String title;
    private String description;
    private String code;
    private DiscountType discountType;
    private double discountValue;
    private String category;
    private LocalDate startDate;
    private LocalDate expirationDate;
    private String termsUrl;

    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDate.now());
    }
}
