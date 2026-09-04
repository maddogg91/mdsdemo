package com.maddogg.couponapi.exception;

import com.maddogg.couponapi.model.Retailer;

public class RetailerNotSupportedException extends RuntimeException {

    public RetailerNotSupportedException(Retailer retailer) {
        super("No coupon provider registered for retailer: " + retailer);
    }
}
