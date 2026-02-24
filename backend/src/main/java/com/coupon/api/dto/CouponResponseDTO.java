package com.coupon.api.dto;

import com.coupon.api.domain.CouponStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponseDTO {

    private UUID id;
    private String code;
    private String description;
    private BigDecimal discountValue;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime expirationDate;
    
    private CouponStatus status;
    private Boolean published;
    private Boolean redeemed;

}
