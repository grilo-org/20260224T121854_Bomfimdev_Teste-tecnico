package com.coupon.api.domain;

import com.coupon.api.exception.CouponAlreadyDeletedException;
import com.coupon.api.exception.InvalidCouponException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "status != 'DELETED'")
public class Coupon {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    @Column(nullable = false)
    private Boolean published;

    @Column(nullable = false)
    private Boolean redeemed;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Coupon(String code, String description, BigDecimal discountValue, 
                   LocalDateTime expirationDate, Boolean published) {
        this.code = sanitizeAndValidateCode(code);
        this.description = validateDescription(description);
        this.discountValue = validateDiscountValue(discountValue);
        this.expirationDate = validateExpirationDate(expirationDate);
        this.status = CouponStatus.ACTIVE;
        this.published = published != null ? published : false;
        this.redeemed = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Coupon create(String code, String description, BigDecimal discountValue,
                                LocalDateTime expirationDate, Boolean published) {
        return new Coupon(code, description, discountValue, expirationDate, published);
    }

    public void delete() {
        if (this.status == CouponStatus.DELETED) {
            throw new CouponAlreadyDeletedException("Coupon is already deleted");
        }
        this.status = CouponStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return this.expirationDate != null && this.expirationDate.isBefore(LocalDateTime.now());
    }

    public boolean isDeleted() {
        return this.status == CouponStatus.DELETED;
    }

    private String sanitizeAndValidateCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new InvalidCouponException("Code is required");
        }

        String sanitized = code.replaceAll("[^a-zA-Z0-9]", "");

        if (sanitized.length() != 6) {
            throw new InvalidCouponException("Code must have exactly 6 alphanumeric characters.");
        }

        return sanitized;
    }

    private String validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidCouponException("Description is required");
        }
        return description;
    }

    private BigDecimal validateDiscountValue(BigDecimal discountValue) {
        if (discountValue == null) {
            throw new InvalidCouponException("Discount value is required");
        }
        if (discountValue.compareTo(new BigDecimal("0.5")) < 0) {
            throw new InvalidCouponException("Discount value must be at least 0.5");
        }
        return discountValue;
    }

    private LocalDateTime validateExpirationDate(LocalDateTime expirationDate) {
        if (expirationDate == null) {
            throw new InvalidCouponException("Expiration date is required");
        }
        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw new InvalidCouponException("Expiration date cannot be in the past");
        }
        return expirationDate;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
