package com.coupon.api.application.port;

import com.coupon.api.domain.Coupon;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository {
    
    Coupon save(Coupon coupon);
    
    Optional<Coupon> findByIdAndNotDeleted(UUID id);
    
    Optional<Coupon> findByIdIncludingDeleted(UUID id);
    
    Optional<Coupon> findByCodeAndNotDeleted(String code);
}
