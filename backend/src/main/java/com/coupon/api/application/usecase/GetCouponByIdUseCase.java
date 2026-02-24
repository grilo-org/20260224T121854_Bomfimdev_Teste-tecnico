package com.coupon.api.application.usecase;

import com.coupon.api.application.port.CouponRepository;
import com.coupon.api.domain.Coupon;
import com.coupon.api.dto.CouponResponseDTO;
import com.coupon.api.exception.CouponNotFoundException;

import java.util.UUID;

public class GetCouponByIdUseCase {

    private final CouponRepository couponRepository;

    public GetCouponByIdUseCase(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public CouponResponseDTO execute(UUID id) {
        Coupon coupon = couponRepository.findByIdAndNotDeleted(id)
            .orElseThrow(() -> new CouponNotFoundException("Coupon not found with id: " + id));

        return CouponResponseDTO.builder()
            .id(coupon.getId())
            .code(coupon.getCode())
            .description(coupon.getDescription())
            .discountValue(coupon.getDiscountValue())
            .expirationDate(coupon.getExpirationDate())
            .status(coupon.getStatus())
            .published(coupon.getPublished())
            .redeemed(coupon.getRedeemed())
            .build();
    }
}
