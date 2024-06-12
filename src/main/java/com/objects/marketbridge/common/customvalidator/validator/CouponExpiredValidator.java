package com.objects.marketbridge.common.customvalidator.validator;

import com.objects.marketbridge.common.customvalidator.annotation.ValidCouponExpired;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CouponExpiredValidator implements ConstraintValidator<ValidCouponExpired, String> {

    @Override
    public void initialize(ValidCouponExpired constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime couponEndDate = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        return now.isBefore(couponEndDate);
    }
}
