package com.coupon.api.infrastructure.persistence;

import com.coupon.api.application.port.CouponRepository;
import com.coupon.api.domain.Coupon;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CouponRepositoryAdapter implements CouponRepository {

    private final CouponJpaRepository jpaRepository;

    public CouponRepositoryAdapter(CouponJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Coupon save(Coupon coupon) {
        return jpaRepository.save(coupon);
    }

    @Override
    public Optional<Coupon> findByIdAndNotDeleted(UUID id) {
        return jpaRepository.findByIdAndNotDeleted(id);
    }

    @Override
    public Optional<Coupon> findByIdIncludingDeleted(UUID id) {
        return jpaRepository.findByIdIncludingDeleted(id);
    }

    @Override
    public Optional<Coupon> findByCodeAndNotDeleted(String code) {
        return jpaRepository.findByCodeAndNotDeleted(code);
    }
}
