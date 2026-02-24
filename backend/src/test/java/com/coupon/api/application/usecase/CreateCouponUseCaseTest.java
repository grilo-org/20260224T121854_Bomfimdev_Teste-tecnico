package com.coupon.api.application.usecase;

import com.coupon.api.application.port.CouponRepository;
import com.coupon.api.domain.Coupon;
import com.coupon.api.domain.CouponStatus;
import com.coupon.api.dto.CouponRequestDTO;
import com.coupon.api.dto.CouponResponseDTO;
import com.coupon.api.exception.InvalidCouponException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateCouponUseCaseTest {

    @Mock
    private CouponRepository couponRepository;

    private CreateCouponUseCase useCase;

    @Before
    public void setUp() {
        useCase = new CreateCouponUseCase(couponRepository);
    }

    @Test
    public void shouldCreateCouponWithValidData() {
        CouponRequestDTO request = CouponRequestDTO.builder()
            .code("ABC-123")
            .description("Test coupon")
            .discountValue(new BigDecimal("10.0"))
            .expirationDate(LocalDateTime.now().plusDays(30))
            .published(false)
            .build();

        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CouponResponseDTO response = useCase.execute(request);

        assertNotNull(response);
        assertEquals("ABC123", response.getCode());
        assertEquals("Test coupon", response.getDescription());
        assertEquals(new BigDecimal("10.0"), response.getDiscountValue());
        assertEquals(CouponStatus.ACTIVE, response.getStatus());
        assertFalse(response.getPublished());
        assertFalse(response.getRedeemed());

        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    public void shouldSanitizeCodeWhenCreatingCoupon() {
        CouponRequestDTO request = CouponRequestDTO.builder()
            .code("AB@C-1#23")
            .description("Test")
            .discountValue(new BigDecimal("1.0"))
            .expirationDate(LocalDateTime.now().plusDays(1))
            .published(false)
            .build();

        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CouponResponseDTO response = useCase.execute(request);

        assertEquals("ABC123", response.getCode());
    }

    @Test(expected = InvalidCouponException.class)
    public void shouldNotCreateCouponWithInvalidCode() {
        CouponRequestDTO request = CouponRequestDTO.builder()
            .code("AB-12")
            .description("Test")
            .discountValue(new BigDecimal("1.0"))
            .expirationDate(LocalDateTime.now().plusDays(1))
            .published(false)
            .build();

        useCase.execute(request);
    }

    @Test(expected = InvalidCouponException.class)
    public void shouldNotCreateCouponWithLowDiscountValue() {
        CouponRequestDTO request = CouponRequestDTO.builder()
            .code("ABC123")
            .description("Test")
            .discountValue(new BigDecimal("0.3"))
            .expirationDate(LocalDateTime.now().plusDays(1))
            .published(false)
            .build();

        useCase.execute(request);
    }

    @Test(expected = InvalidCouponException.class)
    public void shouldNotCreateCouponWithPastExpirationDate() {
        CouponRequestDTO request = CouponRequestDTO.builder()
            .code("ABC123")
            .description("Test")
            .discountValue(new BigDecimal("1.0"))
            .expirationDate(LocalDateTime.now().minusDays(1))
            .published(false)
            .build();

        useCase.execute(request);
    }

    @Test
    public void shouldCreateCouponAsPublishedWhenRequested() {
        CouponRequestDTO request = CouponRequestDTO.builder()
            .code("ABC123")
            .description("Test")
            .discountValue(new BigDecimal("1.0"))
            .expirationDate(LocalDateTime.now().plusDays(1))
            .published(true)
            .build();

        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CouponResponseDTO response = useCase.execute(request);

        assertTrue(response.getPublished());
    }

    @Test
    public void shouldCreateCouponAsNotPublishedByDefault() {
        CouponRequestDTO request = CouponRequestDTO.builder()
            .code("ABC123")
            .description("Test")
            .discountValue(new BigDecimal("1.0"))
            .expirationDate(LocalDateTime.now().plusDays(1))
            .published(null)
            .build();

        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CouponResponseDTO response = useCase.execute(request);

        assertFalse(response.getPublished());
    }

    @Test
    public void shouldAlwaysCreateCouponAsNotRedeemed() {
        CouponRequestDTO request = CouponRequestDTO.builder()
            .code("ABC123")
            .description("Test")
            .discountValue(new BigDecimal("1.0"))
            .expirationDate(LocalDateTime.now().plusDays(1))
            .published(false)
            .build();

        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CouponResponseDTO response = useCase.execute(request);

        assertFalse(response.getRedeemed());
    }

    @Test
    public void shouldAlwaysCreateCouponWithActiveStatus() {
        CouponRequestDTO request = CouponRequestDTO.builder()
            .code("ABC123")
            .description("Test")
            .discountValue(new BigDecimal("1.0"))
            .expirationDate(LocalDateTime.now().plusDays(1))
            .published(false)
            .build();

        when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CouponResponseDTO response = useCase.execute(request);

        assertEquals(CouponStatus.ACTIVE, response.getStatus());
    }
}
