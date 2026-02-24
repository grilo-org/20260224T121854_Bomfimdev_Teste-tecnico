package com.coupon.api.domain;

import com.coupon.api.exception.CouponAlreadyDeletedException;
import com.coupon.api.exception.InvalidCouponException;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class CouponTest {

    @Test
    public void shouldCreateValidCoupon() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(30);
        
        Coupon coupon = Coupon.create("ABC-123", "Test coupon", new BigDecimal("10.0"), futureDate, false);

        assertNotNull(coupon);
        assertEquals("ABC123", coupon.getCode());
        assertEquals("Test coupon", coupon.getDescription());
        assertEquals(new BigDecimal("10.0"), coupon.getDiscountValue());
        assertEquals(futureDate, coupon.getExpirationDate());
        assertEquals(CouponStatus.ACTIVE, coupon.getStatus());
        assertFalse(coupon.getPublished());
        assertFalse(coupon.getRedeemed());
    }

    @Test
    public void shouldSanitizeCodeRemovingSpecialCharacters() {
        Coupon coupon = Coupon.create("AB@C-1#23", "Test", new BigDecimal("1.0"), LocalDateTime.now().plusDays(1), false);

        assertEquals("ABC123", coupon.getCode());
    }

    @Test(expected = InvalidCouponException.class)
    public void shouldNotCreateCouponWithCodeTooShort() {
        Coupon.create("AB-12", "Test", new BigDecimal("1.0"), LocalDateTime.now().plusDays(1), false);
    }

    @Test(expected = InvalidCouponException.class)
    public void shouldNotCreateCouponWithCodeTooLong() {
        Coupon.create("ABC-1234", "Test", new BigDecimal("1.0"), LocalDateTime.now().plusDays(1), false);
    }

    @Test(expected = InvalidCouponException.class)
    public void shouldNotCreateCouponWithNullCode() {
        Coupon.create(null, "Test", new BigDecimal("1.0"), LocalDateTime.now().plusDays(1), false);
    }

    @Test(expected = InvalidCouponException.class)
    public void shouldNotCreateCouponWithEmptyCode() {
        Coupon.create("", "Test", new BigDecimal("1.0"), LocalDateTime.now().plusDays(1), false);
    }

    @Test(expected = InvalidCouponException.class)
    public void shouldNotCreateCouponWithNullDescription() {
        Coupon.create("ABC123", null, new BigDecimal("1.0"), LocalDateTime.now().plusDays(1), false);
    }

    @Test(expected = InvalidCouponException.class)
    public void shouldNotCreateCouponWithEmptyDescription() {
        Coupon.create("ABC123", "", new BigDecimal("1.0"), LocalDateTime.now().plusDays(1), false);
    }

    @Test(expected = InvalidCouponException.class)
    public void shouldNotCreateCouponWithDiscountValueBelowMinimum() {
        Coupon.create("ABC123", "Test", new BigDecimal("0.3"), LocalDateTime.now().plusDays(1), false);
    }

    @Test
    public void shouldCreateCouponWithDiscountValueAtMinimum() {
        Coupon coupon = Coupon.create("ABC123", "Test", new BigDecimal("0.5"), LocalDateTime.now().plusDays(1), false);

        assertEquals(new BigDecimal("0.5"), coupon.getDiscountValue());
    }

    @Test(expected = InvalidCouponException.class)
    public void shouldNotCreateCouponWithNullDiscountValue() {
        Coupon.create("ABC123", "Test", null, LocalDateTime.now().plusDays(1), false);
    }

    @Test(expected = InvalidCouponException.class)
    public void shouldNotCreateCouponWithExpirationDateInPast() {
        Coupon.create("ABC123", "Test", new BigDecimal("1.0"), LocalDateTime.now().minusDays(1), false);
    }

    @Test(expected = InvalidCouponException.class)
    public void shouldNotCreateCouponWithNullExpirationDate() {
        Coupon.create("ABC123", "Test", new BigDecimal("1.0"), null, false);
    }

    @Test
    public void shouldCreateCouponWithPublishedTrue() {
        Coupon coupon = Coupon.create("ABC123", "Test", new BigDecimal("1.0"), LocalDateTime.now().plusDays(1), true);

        assertTrue(coupon.getPublished());
    }

    @Test
    public void shouldCreateCouponWithPublishedFalseWhenNull() {
        Coupon coupon = Coupon.create("ABC123", "Test", new BigDecimal("1.0"), LocalDateTime.now().plusDays(1), null);

        assertFalse(coupon.getPublished());
    }

    @Test
    public void shouldDeleteActiveCoupon() {
        Coupon coupon = Coupon.create("ABC123", "Test", new BigDecimal("1.0"), LocalDateTime.now().plusDays(1), false);

        coupon.delete();

        assertEquals(CouponStatus.DELETED, coupon.getStatus());
        assertTrue(coupon.isDeleted());
    }

    @Test(expected = CouponAlreadyDeletedException.class)
    public void shouldNotDeleteCouponTwice() {
        Coupon coupon = Coupon.create("ABC123", "Test", new BigDecimal("1.0"), LocalDateTime.now().plusDays(1), false);

        coupon.delete();
        coupon.delete();
    }

    @Test
    public void shouldIdentifyExpiredCoupon() {
        Coupon coupon = Coupon.create("ABC123", "Test", new BigDecimal("1.0"), LocalDateTime.now().plusSeconds(1), false);

        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            fail("Thread sleep interrupted");
        }

        assertTrue(coupon.isExpired());
    }

    @Test
    public void shouldIdentifyNonExpiredCoupon() {
        Coupon coupon = Coupon.create("ABC123", "Test", new BigDecimal("1.0"), LocalDateTime.now().plusDays(30), false);

        assertFalse(coupon.isExpired());
    }
}
