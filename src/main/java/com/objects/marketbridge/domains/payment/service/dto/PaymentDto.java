package com.objects.marketbridge.domains.payment.service.dto;

import com.objects.marketbridge.common.kakao.dto.KakaoPayApproveResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentDto {

    private String orderName;
    private Long quantity;
    private Long totalAmount;
    private Long discountAmount;
    private Long taxFreeAmount;

    @Builder
    private PaymentDto(String orderName, Long quantity, Long totalAmount, Long discountAmount, Long taxFreeAmount) {
        this.orderName = orderName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.taxFreeAmount = taxFreeAmount;
    }

    public static PaymentDto of(KakaoPayApproveResponse approve) {
        return PaymentDto.builder()
                .orderName(approve.getOrderName())
                .quantity(approve.getQuantity())
                .totalAmount(approve.getAmount().getTotalAmount())
                .discountAmount(approve.getAmount().getDiscountAmount())
                .taxFreeAmount(approve.getAmount().getTaxFreeAmount())
                .build();
    }
}
