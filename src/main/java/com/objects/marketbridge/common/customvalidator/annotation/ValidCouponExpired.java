package com.objects.marketbridge.common.customvalidator.annotation;

import com.objects.marketbridge.common.customvalidator.validator.CouponExpiredValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CouponExpiredValidator.class)
public @interface ValidCouponExpired {
    String message() default "쿠폰 유효기간이 만료되었습니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
