package com.coupon.api.application.usecase;

import com.coupon.api.application.port.CouponRepository;
import com.coupon.api.domain.Coupon;
import com.coupon.api.domain.CouponStatus;
import com.coupon.api.dto.CouponResponseDTO;
import com.coupon.api.exception.CouponNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GetCouponByIdUseCaseTest {

    @Mock
    private CouponRepository couponRepository;

    private GetCouponByIdUseCase useCase;

    @Before
    public void setUp() {
        useCase = new GetCouponByIdUseCase(couponRepository);
    }

    @Test
    public void shouldGetExistingCoupon() {
        UUID couponId = UUID.randomUUID();
        Coupon coupon = Coupon.create("ABC123", "Test coupon", new BigDecimal("10.0"), LocalDateTime.now().plusDays(30), false);

        when(couponRepository.findByIdAndNotDeleted(couponId)).thenReturn(Optional.of(coupon));

        CouponResponseDTO response = useCase.execute(couponId);

        assertNotNull(response);
        assertEquals("ABC123", response.getCode());
        assertEquals("Test coupon", response.getDescription());
        assertEquals(new BigDecimal("10.0"), response.getDiscountValue());
        assertEquals(CouponStatus.ACTIVE, response.getStatus());
        assertFalse(response.getPublished());
        assertFalse(response.getRedeemed());

        verify(couponRepository, times(1)).findByIdAndNotDeleted(couponId);
    }

    @Test(expected = CouponNotFoundException.class)
    public void shouldNotGetNonExistentCoupon() {
        UUID couponId = UUID.randomUUID();

        when(couponRepository.findByIdAndNotDeleted(couponId)).thenReturn(Optional.empty());

        useCase.execute(couponId);
    }

    @Test(expected = CouponNotFoundException.class)
    public void shouldNotGetDeletedCoupon() {
        UUID couponId = UUID.randomUUID();

        when(couponRepository.findByIdAndNotDeleted(couponId)).thenReturn(Optional.empty());

        useCase.execute(couponId);

        verify(couponRepository, times(1)).findByIdAndNotDeleted(couponId);
    }

    @Test
    public void shouldGetPublishedCoupon() {
        UUID couponId = UUID.randomUUID();
        Coupon coupon = Coupon.create("ABC123", "Test", new BigDecimal("1.0"), LocalDateTime.now().plusDays(1), true);

        when(couponRepository.findByIdAndNotDeleted(couponId)).thenReturn(Optional.of(coupon));

        CouponResponseDTO response = useCase.execute(couponId);

        assertTrue(response.getPublished());
    }

    @Test
    public void shouldGetCouponWithAllAttributes() {
        UUID couponId = UUID.randomUUID();
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(30);
        Coupon coupon = Coupon.create("XYZ789", "Special discount", new BigDecimal("25.5"), expirationDate, true);

        when(couponRepository.findByIdAndNotDeleted(couponId)).thenReturn(Optional.of(coupon));

        CouponResponseDTO response = useCase.execute(couponId);

        assertEquals("XYZ789", response.getCode());
        assertEquals("Special discount", response.getDescription());
        assertEquals(new BigDecimal("25.5"), response.getDiscountValue());
        assertEquals(expirationDate, response.getExpirationDate());
        assertTrue(response.getPublished());
        assertFalse(response.getRedeemed());
        assertEquals(CouponStatus.ACTIVE, response.getStatus());
    }
}
