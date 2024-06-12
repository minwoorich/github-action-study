package com.objects.marketbridge.domains.payment.service.dto;

import com.objects.marketbridge.common.kakao.dto.KaKaoPayCancelResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RefundDto {

    private Long totalRefundAmount;
    private String refundMethod;
    private LocalDateTime refundProcessedAt; // 환불 일자

    @Builder
    public RefundDto(Long totalRefundAmount, String refundMethod, LocalDateTime refundProcessedAt) {
        this.totalRefundAmount = totalRefundAmount;
        this.refundMethod = refundMethod;
        this.refundProcessedAt = refundProcessedAt;
    }

    public static RefundDto of(KaKaoPayCancelResponse kaKaoPayCancelResponse) {
        return RefundDto.builder()
                .refundMethod(kaKaoPayCancelResponse.getPayment_method_type())
                .refundMethod(kaKaoPayCancelResponse.getCanceled_at())
                .totalRefundAmount((long) kaKaoPayCancelResponse.getCanceled_amount().getTotal())
                .build();
    }
}
