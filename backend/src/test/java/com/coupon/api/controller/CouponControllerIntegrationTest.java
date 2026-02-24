package com.coupon.api.controller;

import com.coupon.api.domain.Coupon;
import com.coupon.api.domain.CouponStatus;
import com.coupon.api.dto.CouponRequestDTO;
import com.coupon.api.infrastructure.persistence.CouponJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CouponControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CouponJpaRepository couponRepository;

    @Before
    public void setUp() {
        couponRepository.deleteAll();
    }

    @Test
    public void testCreateCoupon_Success() throws Exception {
        CouponRequestDTO requestDTO = CouponRequestDTO.builder()
                .code("ABC-123")
                .description("Test coupon for integration")
                .discountValue(new BigDecimal("15.0"))
                .expirationDate(LocalDateTime.now().plusDays(30))
                .published(false)
                .build();

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.code", is("ABC123")))
                .andExpect(jsonPath("$.description", is("Test coupon for integration")))
                .andExpect(jsonPath("$.discountValue", is(15.0)))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.published", is(false)))
                .andExpect(jsonPath("$.redeemed", is(false)));
    }

    @Test
    public void testCreateCoupon_WithPublishedTrue() throws Exception {
        CouponRequestDTO requestDTO = CouponRequestDTO.builder()
                .code("XYZ789")
                .description("Published coupon")
                .discountValue(new BigDecimal("20.0"))
                .expirationDate(LocalDateTime.now().plusDays(60))
                .published(true)
                .build();

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.published", is(true)));
    }

    @Test
    public void testCreateCoupon_InvalidCode() throws Exception {
        CouponRequestDTO requestDTO = CouponRequestDTO.builder()
                .code("AB-12")
                .description("Invalid code")
                .discountValue(new BigDecimal("10.0"))
                .expirationDate(LocalDateTime.now().plusDays(30))
                .build();

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", containsString("6 alphanumeric characters")));
    }

    @Test
    public void testCreateCoupon_DiscountValueTooLow() throws Exception {
        CouponRequestDTO requestDTO = CouponRequestDTO.builder()
                .code("ABC123")
                .description("Low discount")
                .discountValue(new BigDecimal("0.3"))
                .expirationDate(LocalDateTime.now().plusDays(30))
                .build();

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", containsString("at least 0.5")));
    }

    @Test
    public void testCreateCoupon_ExpirationDateInPast() throws Exception {
        CouponRequestDTO requestDTO = CouponRequestDTO.builder()
                .code("ABC123")
                .description("Expired coupon")
                .discountValue(new BigDecimal("10.0"))
                .expirationDate(LocalDateTime.now().minusDays(1))
                .build();

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message", containsString("cannot be in the past")));
    }

    @Test
    public void testCreateCoupon_MissingRequiredFields() throws Exception {
        CouponRequestDTO requestDTO = CouponRequestDTO.builder()
                .code("ABC123")
                .build();

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation failed")));
    }

    @Test
    public void testGetCouponById_Success() throws Exception {
        Coupon coupon = createAndSaveCoupon();

        mockMvc.perform(get("/coupon/" + coupon.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(coupon.getId().toString())))
                .andExpect(jsonPath("$.code", is("ABC123")))
                .andExpect(jsonPath("$.description", is("Test coupon")));
    }

    @Test
    public void testGetCouponById_NotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/coupon/" + randomId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    public void testGetCouponById_DeletedCouponNotFound() throws Exception {
        Coupon coupon = createAndSaveCoupon();
        coupon.delete();
        couponRepository.save(coupon);

        mockMvc.perform(get("/coupon/" + coupon.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCoupon_Success() throws Exception {
        Coupon coupon = createAndSaveCoupon();
        UUID couponId = coupon.getId();

        mockMvc.perform(delete("/coupon/" + couponId))
                .andExpect(status().isNoContent());

        Coupon deletedCoupon = couponRepository.findById(couponId).orElse(null);
        assertNotNull(deletedCoupon);
        assertEquals(CouponStatus.DELETED, deletedCoupon.getStatus());
    }

    @Test
    public void testDeleteCoupon_NotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(delete("/coupon/" + randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCoupon_AlreadyDeleted() throws Exception {
        Coupon coupon = createAndSaveCoupon();
        coupon.delete();
        couponRepository.save(coupon);

        mockMvc.perform(delete("/coupon/" + coupon.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already deleted")));
    }

    private Coupon createAndSaveCoupon() {
        Coupon coupon = Coupon.create("ABC123", "Test coupon", new BigDecimal("10.0"), LocalDateTime.now().plusDays(30), false);
        return couponRepository.save(coupon);
    }

}
