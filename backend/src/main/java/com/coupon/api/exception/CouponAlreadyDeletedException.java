package com.coupon.api.exception;

public class CouponAlreadyDeletedException extends RuntimeException {

    public CouponAlreadyDeletedException(String message) {
        super(message);
    }

}
