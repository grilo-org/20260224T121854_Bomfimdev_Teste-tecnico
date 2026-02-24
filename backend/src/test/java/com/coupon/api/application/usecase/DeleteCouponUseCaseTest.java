package com.coupon.api.application.usecase;

import com.coupon.api.application.port.CouponRepository;
import com.coupon.api.domain.Coupon;
import com.coupon.api.domain.CouponStatus;
import com.coupon.api.exception.CouponAlreadyDeletedException;
import com.coupon.api.exception.CouponNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCouponUseCaseTest {

    @Mock
    private CouponRepository couponRepository;

    private DeleteCouponUseCase useCase;

    @Before
    public void setUp() {
        useCase = new DeleteCouponUseCase(couponRepository);
    }

    @Test
    public void shouldDeleteActiveCoupon() {
        UUID couponId = UUID.randomUUID();
        Coupon coupon = Coupon.create("ABC123", "Test", new BigDecimal("1.0"), LocalDateTime.now().plusDays(1), false);

        when(couponRepository.findByIdIncludingDeleted(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(couponId);

        ArgumentCaptor<Coupon> captor = ArgumentCaptor.forClass(Coupon.class);
        verify(couponRepository).save(captor.capture());

        Coupon savedCoupon = captor.getValue();
        assertEquals(CouponStatus.DELETED, savedCoupon.getStatus());
        assertTrue(savedCoupon.isDeleted());
    }

    @Test(expected = CouponNotFoundException.class)
    public void shouldNotDeleteNonExistentCoupon() {
        UUID couponId = UUID.randomUUID();

        when(couponRepository.findByIdIncludingDeleted(couponId)).thenReturn(Optional.empty());

        useCase.execute(couponId);
    }

    @Test(expected = CouponAlreadyDeletedException.class)
    public void shouldNotDeleteAlreadyDeletedCoupon() {
        UUID couponId = UUID.randomUUID();
        Coupon coupon = Coupon.create("ABC123", "Test", new BigDecimal("1.0"), LocalDateTime.now().plusDays(1), false);
        coupon.delete();

        when(couponRepository.findByIdIncludingDeleted(couponId)).thenReturn(Optional.of(coupon));

        useCase.execute(couponId);
    }

    @Test
    public void shouldCallRepositoryOnlyOnceWhenDeleting() {
        UUID couponId = UUID.randomUUID();
        Coupon coupon = Coupon.create("ABC123", "Test", new BigDecimal("1.0"), LocalDateTime.now().plusDays(1), false);

        when(couponRepository.findByIdIncludingDeleted(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(couponId);

        verify(couponRepository, times(1)).findByIdIncludingDeleted(couponId);
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    public void shouldPreserveOtherCouponDataWhenDeleting() {
        UUID couponId = UUID.randomUUID();
        Coupon coupon = Coupon.create("ABC123", "Test coupon", new BigDecimal("10.0"), LocalDateTime.now().plusDays(30), true);

        when(couponRepository.findByIdIncludingDeleted(couponId)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(couponId);

        ArgumentCaptor<Coupon> captor = ArgumentCaptor.forClass(Coupon.class);
        verify(couponRepository).save(captor.capture());

        Coupon savedCoupon = captor.getValue();
        assertEquals("ABC123", savedCoupon.getCode());
        assertEquals("Test coupon", savedCoupon.getDescription());
        assertEquals(new BigDecimal("10.0"), savedCoupon.getDiscountValue());
        assertTrue(savedCoupon.getPublished());
        assertEquals(CouponStatus.DELETED, savedCoupon.getStatus());
    }
}
