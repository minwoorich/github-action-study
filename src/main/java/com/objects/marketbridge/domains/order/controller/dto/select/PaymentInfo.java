package com.objects.marketbridge.domains.order.controller.dto.select;

import com.objects.marketbridge.domains.order.service.dto.GetOrderDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentInfo {
    private String paymentMethod;
    private String cardIssuerName;
    private Long totalAmount;
    private Long discountAmount;
    private Long deliveryFee;

    @Builder
    private PaymentInfo(String paymentMethod, String cardIssuerName, Long totalAmount, Long discountAmount, Long deliveryFee) {
        this.paymentMethod = paymentMethod;
        this.cardIssuerName = cardIssuerName;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.deliveryFee = deliveryFee;
    }

    public static PaymentInfo of(GetOrderDto getOrderDto) {
        return PaymentInfo.builder()
                .totalAmount(getOrderDto.getTotalPrice())
                .discountAmount(getOrderDto.getTotalDiscount())
                .paymentMethod(getOrderDto.getPaymentMethod())
                .cardIssuerName(getOrderDto.getCardIssuerName())
                .build();
    }

    public static PaymentInfo create(String paymentMethod, String cardIssuerName, Long totalAmount, Long discountAmount, Long deliveryFee) {
        return PaymentInfo.builder()
                .paymentMethod(paymentMethod)
                .cardIssuerName(cardIssuerName)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .deliveryFee(deliveryFee)
                .build();
    }
}
