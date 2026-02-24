package com.coupon.api.infrastructure.persistence;

import com.coupon.api.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponJpaRepository extends JpaRepository<Coupon, UUID> {

    @Query("SELECT c FROM Coupon c WHERE c.id = :id AND c.status != 'DELETED'")
    Optional<Coupon> findByIdAndNotDeleted(@Param("id") UUID id);

    @Query("SELECT c FROM Coupon c WHERE c.id = :id")
    Optional<Coupon> findByIdIncludingDeleted(@Param("id") UUID id);

    @Query("SELECT c FROM Coupon c WHERE c.code = :code AND c.status != 'DELETED'")
    Optional<Coupon> findByCodeAndNotDeleted(@Param("code") String code);

}
