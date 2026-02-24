package com.coupon.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequestDTO {

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Discount value is required")
    private BigDecimal discountValue;

    @NotNull(message = "Expiration date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime expirationDate;

    private Boolean published;

}
