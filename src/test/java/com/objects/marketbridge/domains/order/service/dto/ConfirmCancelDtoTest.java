package com.objects.marketbridge.domains.order.service.dto;

import com.objects.marketbridge.domains.order.domain.Order;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.payment.service.dto.RefundDto;
import com.objects.marketbridge.domains.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ConfirmCancelDtoTest {

    @Test
    @DisplayName("주문상세, 환불 정보가 주어지면 Response를 반환한다.")
    public void response_of() {
        // given
        Order order = Order.builder().build();
        ReflectionTestUtils.setField(order, "id", 1L, Long.class);

        Product product = Product.builder()
                .productNo("1")
                .name("빵빵이키링")
                .build();
        ReflectionTestUtils.setField(product, "id", 1L, Long.class);

        LocalDateTime cancelledAt = LocalDateTime.of(2024, 1, 30, 2, 20);
        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .orderNo("1")
                .product(product)
                .cancelledAt(cancelledAt)
                .reducedQuantity(2L)
                .price(1000L)
                .quantity(2L)
                .build();

        LocalDateTime refundProcessedAt = LocalDateTime.of(2024, 1, 30, 2, 20);

        RefundDto refundDto = RefundDto.builder()
                .refundProcessedAt(refundProcessedAt)
                .refundMethod("카드")
                .totalRefundAmount(2000L)
                .build();

        // when
        ConfirmCancelDto.Response result = ConfirmCancelDto.Response.of(orderDetail, refundDto);

        // then
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getOrderNo()).isEqualTo("1");
        assertThat(result.getTotalPrice()).isEqualTo(2000L);
        assertThat(result.getCancellationDate()).isEqualTo(cancelledAt);

        assertThat(result.getCancelledItem().getProductId()).isEqualTo(1L);
        assertThat(result.getCancelledItem().getProductNo()).isEqualTo("1");
        assertThat(result.getCancelledItem().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getCancelledItem().getPrice()).isEqualTo(1000L);
        assertThat(result.getCancelledItem().getQuantity()).isEqualTo(2L);
    }

    @Test
    @DisplayName("orderDetail이 주어지면 productInfo를 반환한다.")
    public void productInfo_of() {
        // given
        Product product = Product.builder()
                .productNo("1")
                .name("빵빵이키링")
                .build();
        ReflectionTestUtils.setField(product, "id", 1L, Long.class);

        OrderDetail orderDetail = OrderDetail.builder()
                .product(product)
                .price(1000L)
                .quantity(2L)
                .reducedQuantity(0L)
                .build();

        // when
        ConfirmCancelDto.ProductInfo result = ConfirmCancelDto.ProductInfo.of(orderDetail);

        // then
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getProductNo()).isEqualTo("1");
        assertThat(result.getName()).isEqualTo("빵빵이키링");
        assertThat(result.getPrice()).isEqualTo(1000L);
        assertThat(result.getQuantity()).isEqualTo(0L);
    }

    @Test
    @DisplayName("RefundDto가 주어지면 Dto.RefundInfo를 생성한다.")
    public void refundInfo_of() {
        // given
        LocalDateTime refundProcessedAt = LocalDateTime.of(2024, 1, 30, 2, 20);

        RefundDto refundDto = RefundDto.builder()
                .refundProcessedAt(refundProcessedAt)
                .refundMethod("카드")
                .totalRefundAmount(4000L)
                .build();

        // when
        ConfirmCancelDto.RefundInfo result = ConfirmCancelDto.RefundInfo.of(refundDto);

        // then
        assertThat(result.getRefundMethod()).isEqualTo("카드");
        assertThat(result.getRefundProcessedAt()).isEqualTo(refundProcessedAt);
        assertThat(result.getTotalRefundAmount()).isEqualTo(4000L);
    }
}