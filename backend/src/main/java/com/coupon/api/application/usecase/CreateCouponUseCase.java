package com.coupon.api.application.usecase;

import com.coupon.api.application.port.CouponRepository;
import com.coupon.api.domain.Coupon;
import com.coupon.api.dto.CouponRequestDTO;
import com.coupon.api.dto.CouponResponseDTO;

public class CreateCouponUseCase {

    private final CouponRepository couponRepository;

    public CreateCouponUseCase(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public CouponResponseDTO execute(CouponRequestDTO request) {
        Coupon coupon = Coupon.create(
            request.getCode(),
            request.getDescription(),
            request.getDiscountValue(),
            request.getExpirationDate(),
            request.getPublished()
        );

        Coupon savedCoupon = couponRepository.save(coupon);

        return CouponResponseDTO.builder()
            .id(savedCoupon.getId())
            .code(savedCoupon.getCode())
            .description(savedCoupon.getDescription())
            .discountValue(savedCoupon.getDiscountValue())
            .expirationDate(savedCoupon.getExpirationDate())
            .status(savedCoupon.getStatus())
            .published(savedCoupon.getPublished())
            .redeemed(savedCoupon.getRedeemed())
            .build();
    }
}
