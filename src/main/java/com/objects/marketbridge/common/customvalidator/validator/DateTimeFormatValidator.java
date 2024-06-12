package com.objects.marketbridge.common.customvalidator.validator;

import com.objects.marketbridge.common.customvalidator.annotation.ValidDateTimeFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeFormatValidator implements ConstraintValidator<ValidDateTimeFormat, String> {

    private String pattern; // 패턴을 저장할 변수 추가

    @Override
    public void initialize(ValidDateTimeFormat constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern(); // 어노테이션으로부터 패턴을 가져와서 변수에 저장
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 값이 없는 경우는 유효하다고 가정
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern); // 동적으로 패턴 생성
            formatter.parse(value);
            return true; // 파싱이 성공하면 유효한 형식

        } catch (DateTimeParseException e) {
            return false; // 파싱이 실패하면 유효하지 않은 형식
        }
    }
}
