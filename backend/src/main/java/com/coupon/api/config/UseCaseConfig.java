package com.coupon.api.config;

import com.coupon.api.application.port.CouponRepository;
import com.coupon.api.application.usecase.CreateCouponUseCase;
import com.coupon.api.application.usecase.DeleteCouponUseCase;
import com.coupon.api.application.usecase.GetCouponByIdUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateCouponUseCase createCouponUseCase(CouponRepository couponRepository) {
        return new CreateCouponUseCase(couponRepository);
    }

    @Bean
    public GetCouponByIdUseCase getCouponByIdUseCase(CouponRepository couponRepository) {
        return new GetCouponByIdUseCase(couponRepository);
    }

    @Bean
    public DeleteCouponUseCase deleteCouponUseCase(CouponRepository couponRepository) {
        return new DeleteCouponUseCase(couponRepository);
    }
}
