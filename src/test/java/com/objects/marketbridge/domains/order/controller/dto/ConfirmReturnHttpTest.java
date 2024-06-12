package com.objects.marketbridge.domains.order.controller.dto;

import com.objects.marketbridge.domains.order.controller.dto.ConfirmReturnHttp;
import com.objects.marketbridge.domains.order.service.dto.ConfirmReturnDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ConfirmReturnHttpTest {

    @Test
    @DisplayName("Dto로 변환할 수 있다.")
    public void toDto() {
        // given
        ConfirmReturnHttp.Request request = ConfirmReturnHttp.Request.builder()
                .orderDetailId(1L)
                .numberOfReturns(2L)
                .reason("단순변심")
                .build();

        // when
        ConfirmReturnDto.Request result = request.toDto();

        // then
        assertThat(result.getOrderDetailId()).isEqualTo(1L);
        assertThat(result.getNumberOfReturns()).isEqualTo(2L);
        assertThat(result.getReason()).isEqualTo("단순변심");
    }

    @Test
    @DisplayName("Dto.Response가 주어지면 Http.Response로 변환한다.")
    public void response_of() {
        // given
        LocalDateTime cancellationDate = LocalDateTime.of(2024, 2, 3, 3, 9);

        ConfirmReturnDto.ProductInfo productInfo = ConfirmReturnDto.ProductInfo.builder()
                .productId(1L)
                .productNo("1")
                .name("빵빵이키링")
                .price(1000L)
                .quantity(1L)
                .build();

        LocalDateTime refundDate = LocalDateTime.of(2024, 2, 3, 3, 10);
        ConfirmReturnDto.RefundInfo refundInfo = ConfirmReturnDto.RefundInfo.builder()
                .totalRefundAmount(5000L)
                .refundMethod("카드")
                .refundProcessedAt(refundDate)
                .build();

        ConfirmReturnDto.Response param = ConfirmReturnDto.Response.builder()
                .orderId(1L)
                .orderNo("1")
                .totalPrice(5000L)
                .returnedDate(cancellationDate)
                .returnedItem(productInfo)
                .refundInfo(refundInfo)
                .build();

        // when
        ConfirmReturnHttp.Response result = ConfirmReturnHttp.Response.of(param);

        // then
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getOrderNo()).isEqualTo("1");
        assertThat(result.getTotalPrice()).isEqualTo(5000L);
        assertThat(result.getReturnedDate()).isEqualTo(cancellationDate);

        assertThat(result.getReturnedItem().getProductId()).isEqualTo(1L);
        assertThat(result.getReturnedItem().getProductNo()).isEqualTo("1");
        assertThat(result.getReturnedItem().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getReturnedItem().getPrice()).isEqualTo(1000L);
        assertThat(result.getReturnedItem().getQuantity()).isEqualTo(1L);

        assertThat(result.getRefundInfo().getRefundMethod()).isEqualTo("카드");
        assertThat(result.getRefundInfo().getRefundProcessedAt()).isEqualTo(refundDate);
        assertThat(result.getRefundInfo().getTotalRefundAmount()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("Dto.ProductInfo가 주어지면 Http.ProductInfo를 반환한다.")
    public void productInfo_of() {
        // given
        ConfirmReturnDto.ProductInfo dtoProductInfo = ConfirmReturnDto.ProductInfo.builder()
                .productId(1L)
                .productNo("1")
                .name("빵빵이키링")
                .price(1000L)
                .quantity(1L)
                .build();

        // when
        ConfirmReturnHttp.ProductInfo result = ConfirmReturnHttp.ProductInfo.of(dtoProductInfo);

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
        ConfirmReturnDto.RefundInfo dtoRefundInfo = ConfirmReturnDto.RefundInfo.builder()
                .totalRefundAmount(5000L)
                .refundMethod("카드")
                .refundProcessedAt(refundDate)
                .build();

        // when
        ConfirmReturnHttp.RefundInfo result = ConfirmReturnHttp.RefundInfo.of(dtoRefundInfo);

        // then
        assertThat(result.getRefundMethod()).isEqualTo("카드");
        assertThat(result.getRefundProcessedAt()).isEqualTo(refundDate);
        assertThat(result.getTotalRefundAmount()).isEqualTo(5000L);
    }
}