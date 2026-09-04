package com.maddogg.couponapi.model;

public enum Retailer {
    DOLLAR_GENERAL("Dollar General"),
    CVS("CVS Pharmacy"),
    WALMART("Walmart");

    private final String displayName;

    Retailer(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
