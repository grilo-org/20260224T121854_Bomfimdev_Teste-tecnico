package com.coupon.api.application.usecase;

import com.coupon.api.application.port.CouponRepository;
import com.coupon.api.domain.Coupon;
import com.coupon.api.exception.CouponNotFoundException;

import java.util.UUID;

public class DeleteCouponUseCase {

    private final CouponRepository couponRepository;

    public DeleteCouponUseCase(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public void execute(UUID id) {
        Coupon coupon = couponRepository.findByIdIncludingDeleted(id)
            .orElseThrow(() -> new CouponNotFoundException("Coupon not found with id: " + id));

        coupon.delete();

        couponRepository.save(coupon);
    }
}
