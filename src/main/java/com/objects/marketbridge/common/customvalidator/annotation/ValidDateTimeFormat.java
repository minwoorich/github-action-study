package com.objects.marketbridge.common.customvalidator.annotation;

import com.objects.marketbridge.common.customvalidator.validator.DateTimeFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateTimeFormatValidator.class)
public @interface ValidDateTimeFormat {
    String message() default "올바른 날짜 및 시간 형식이 아닙니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String pattern() default "yyyy-MM-dd HH:mm:ss";
}
