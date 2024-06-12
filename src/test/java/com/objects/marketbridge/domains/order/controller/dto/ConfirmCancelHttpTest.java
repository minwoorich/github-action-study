package com.objects.marketbridge.domains.order.controller.dto;

import com.objects.marketbridge.domains.order.controller.dto.ConfirmCancelHttp;
import com.objects.marketbridge.domains.order.service.dto.ConfirmCancelDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ConfirmCancelHttpTest {

    @Test
    @DisplayName("Dto로 변환할 수 있다.")
    public void toDto() {
        // given
        ConfirmCancelHttp.Request request = ConfirmCancelHttp.Request.builder()
                .orderDetailId(1L)
                .numberOfCancellation(2L)
                .reason("단순변심")
                .build();

        // when
        ConfirmCancelDto.Request result = request.toDto();

        // then
        assertThat(result.getOrderDetailId()).isEqualTo(1L);
        assertThat(result.getNumberOfCancellation()).isEqualTo(2L);
        assertThat(result.getReason()).isEqualTo("단순변심");
    }

    @Test
    @DisplayName("Dto.Response가 주어지면 Http.Response로 변환한다.")
    public void response_of() {
        // given
        LocalDateTime cancellationDate = LocalDateTime.of(2024, 2, 3, 3, 9);

        ConfirmCancelDto.ProductInfo productInfo = ConfirmCancelDto.ProductInfo.builder()
                .productId(1L)
                .productNo("1")
                .name("빵빵이키링")
                .price(1000L)
                .quantity(1L)
                .build();

        LocalDateTime refundDate = LocalDateTime.of(2024, 2, 3, 3, 10);
        ConfirmCancelDto.RefundInfo refundInfo = ConfirmCancelDto.RefundInfo.builder()
                .totalRefundAmount(5000L)
                .refundMethod("카드")
                .refundProcessedAt(refundDate)
                .build();

        ConfirmCancelDto.Response param = ConfirmCancelDto.Response.builder()
                .orderId(1L)
                .orderNo("1")
                .totalPrice(5000L)
                .cancellationDate(cancellationDate)
                .cancelledItem(productInfo)
                .refundInfo(refundInfo)
                .build();

        // when
        ConfirmCancelHttp.Response result = ConfirmCancelHttp.Response.of(param);

        // then
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getOrderNo()).isEqualTo("1");
        assertThat(result.getTotalPrice()).isEqualTo(5000L);
        assertThat(result.getCancellationDate()).isEqualTo(cancellationDate);

        assertThat(result.getCancelledItem().getProductId()).isEqualTo(1L);
        assertThat(result.getCancelledItem().getProductNo()).isEqualTo("1");
        assertThat(result.getCancelledItem().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getCancelledItem().getPrice()).isEqualTo(1000L);
        assertThat(result.getCancelledItem().getQuantity()).isEqualTo(1L);

        assertThat(result.getRefundInfo().getRefundMethod()).isEqualTo("카드");
        assertThat(result.getRefundInfo().getRefundProcessedAt()).isEqualTo(refundDate);
        assertThat(result.getRefundInfo().getTotalRefundAmount()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("Dto.ProductInfo가 주어지면 Http.ProductInfo를 반환한다.")
    public void productInfo_of() {
        // given
        ConfirmCancelDto.ProductInfo dtoProductInfo = ConfirmCancelDto.ProductInfo.builder()
                .productId(1L)
                .productNo("1")
                .name("빵빵이키링")
                .price(1000L)
                .quantity(1L)
                .build();

        // when
        ConfirmCancelHttp.ProductInfo result = ConfirmCancelHttp.ProductInfo.of(dtoProductInfo);

        // then
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getProductNo()).isEqualTo("1");
        assertThat(result.getName()).isEqualTo("빵빵이키링");
        assertThat(result.getPrice()).isEqualTo(1000L);
        assertThat(result.getQuantity()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Dto.RefundInfo가 주어지면 Http.RefundInfo를 반환한다.")
    public void refundInfo_of() {
        // given
        LocalDateTime refundDate = LocalDateTime.of(2024, 2, 3, 3, 10);
        ConfirmCancelDto.RefundInfo dtoRefundInfo = ConfirmCancelDto.RefundInfo.builder()
                .totalRefundAmount(5000L)
                .refundMethod("카드")
                .refundProcessedAt(refundDate)
                .build();

        // when
        ConfirmCancelHttp.RefundInfo result = ConfirmCancelHttp.RefundInfo.of(dtoRefundInfo);

        // then
        assertThat(result.getRefundMethod()).isEqualTo("카드");
        assertThat(result.getRefundProcessedAt()).isEqualTo(refundDate);
        assertThat(result.getTotalRefundAmount()).isEqualTo(5000L);
    }
}