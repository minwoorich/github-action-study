package com.objects.marketbridge.domains.order.controller.dto;

import com.objects.marketbridge.domains.order.controller.dto.GetCancelDetailHttp;
import com.objects.marketbridge.domains.order.service.dto.GetCancelDetailDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class GetCancelDetailHttpTest {

    @Test
    @DisplayName("Dto.Response 주어지면 Http.Response 반환한다.")
    public void response_of() {
        // given
        LocalDateTime orderDate = LocalDateTime.of(2024, 2, 3, 3, 9);
        LocalDateTime cancelDate = LocalDateTime.of(2024, 2, 3, 4, 4);

        GetCancelDetailDto.ProductInfo dtoProductInfo = GetCancelDetailDto.ProductInfo.builder()
                .productId(1L)
                .productNo("1")
                .name("빵빵이키링")
                .price(1000L)
                .quantity(1L)
                .build();

        GetCancelDetailDto.RefundInfo dtoRefundInfo = GetCancelDetailDto.RefundInfo.builder()
                .deliveryFee(0L)
                .refundFee(0L)
                .discountPrice(3000L)
                .totalPrice(5000L)
                .build();

        GetCancelDetailDto.Response dtoResponse = GetCancelDetailDto.Response.builder()
                .orderDate(orderDate)
                .cancelDate(cancelDate)
                .orderNo("1")
                .reason("단순변심")
                .productInfo(dtoProductInfo)
                .refundInfo(dtoRefundInfo)
                .build();

        // when
        GetCancelDetailHttp.Response result = GetCancelDetailHttp.Response.of(dtoResponse);

        // then
        assertThat(result.getOrderDate()).isEqualTo(orderDate);
        assertThat(result.getCancelDate()).isEqualTo(cancelDate);
        assertThat(result.getOrderNo()).isEqualTo("1");
        assertThat(result.getReason()).isEqualTo("단순변심");

        assertThat(result.getProductInfo().getProductId()).isEqualTo(1L);
        assertThat(result.getProductInfo().getProductNo()).isEqualTo("1");
        assertThat(result.getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getProductInfo().getPrice()).isEqualTo(1000L);
        assertThat(result.getProductInfo().getQuantity()).isEqualTo(1L);

        assertThat(result.getRefundInfo().getDeliveryFee()).isEqualTo(0L);
        assertThat(result.getRefundInfo().getRefundFee()).isEqualTo(0L);
        assertThat(result.getRefundInfo().getDiscountPrice()).isEqualTo(3000L);
        assertThat(result.getRefundInfo().getTotalPrice()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("Dto.ProductInfo 주어지면 Http.ProductInfo 반환한다.")
    public void productInfo_of() {
        // given
        GetCancelDetailDto.ProductInfo dtoProductInfo = GetCancelDetailDto.ProductInfo.builder()
                .productId(1L)
                .productNo("1")
                .name("빵빵이키링")
                .price(1000L)
                .quantity(1L)
                .build();

        // when
        GetCancelDetailHttp.ProductInfo result = GetCancelDetailHttp.ProductInfo.of(dtoProductInfo);

        // then
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getProductNo()).isEqualTo("1");
        assertThat(result.getName()).isEqualTo("빵빵이키링");
        assertThat(result.getPrice()).isEqualTo(1000L);
        assertThat(result.getQuantity()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Dto.CancelRefundInfo가 주어지면 Http.CancelRefundInfo로 반환한다.")
    public void cancelRefundInfo_of() {
        // given
        GetCancelDetailDto.RefundInfo dtoRefundInfo = GetCancelDetailDto.RefundInfo.builder()
                .deliveryFee(0L)
                .refundFee(0L)
                .discountPrice(3000L)
                .totalPrice(5000L)
                .build();

        // when
        GetCancelDetailHttp.RefundInfo result = GetCancelDetailHttp.RefundInfo.of(dtoRefundInfo);

        // then
        assertThat(result.getDeliveryFee()).isEqualTo(0L);
        assertThat(result.getRefundFee()).isEqualTo(0L);
        assertThat(result.getDiscountPrice()).isEqualTo(3000L);
        assertThat(result.getTotalPrice()).isEqualTo(5000L);
    }
}