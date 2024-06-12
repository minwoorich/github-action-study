package com.objects.marketbridge.domains.order.mock;

import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.payment.service.dto.PaymentDto;
import com.objects.marketbridge.domains.payment.service.dto.RefundCancelDto;
import com.objects.marketbridge.domains.payment.service.port.PaymentClient;
import com.objects.marketbridge.domains.payment.service.dto.RefundDto;
import lombok.Builder;

public class FakePaymentClient implements PaymentClient {

    private DateTimeHolder dateTimeHolder;

    @Builder
    public FakePaymentClient(DateTimeHolder dateTimeHolder) {
        this.dateTimeHolder = dateTimeHolder;
    }

    @Override
    public RefundDto refund(RefundCancelDto refundCancelDto) {
        return RefundDto.builder()
                .totalRefundAmount(Long.valueOf(refundCancelDto.getCancelAmount()))
                .refundMethod("카드")
                .refundProcessedAt(dateTimeHolder.getTimeNow())
                .build();
    }

    @Override
    public PaymentDto payment(Order order, String pgToken) {
        return PaymentDto.builder()
                .orderName(order.getOrderName())
                .quantity(3L)
                .totalAmount(order.getTotalPrice())
                .discountAmount(order.getTotalDiscount())
                .taxFreeAmount(0L)
                .build();
    }

}
