package com.objects.marketbridge.domains.order.controller.dto;

import com.objects.marketbridge.domains.order.controller.dto.RequestCancelHttp;
import com.objects.marketbridge.domains.order.service.dto.RequestCancelDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestCancelHttpTest {

    @Test
    @DisplayName("Dto.Response 주어지면 Http.Response 반환한다.")
    public void response_of() {
        RequestCancelDto.ProductInfo productInfoDto = RequestCancelDto.ProductInfo.builder()
                .name("빵빵이키링")
                .price(1000L)
                .quantity(1L)
                .image("빵빵이썸네일")
                .build();

        RequestCancelDto.CancelRefundInfo cancelRefundInfoDto = RequestCancelDto.CancelRefundInfo.builder()
                .deliveryFee(0L)
                .refundFee(0L)
                .discountPrice(3000L)
                .totalPrice(5000L)
                .build();

        RequestCancelDto.Response dtoResponse = RequestCancelDto.Response.builder()
                .productInfo(productInfoDto)
                .cancelRefundInfo(cancelRefundInfoDto)
                .build();

        // when
        RequestCancelHttp.Response result = RequestCancelHttp.Response.of(dtoResponse);

        // then
        assertThat(result.getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getProductInfo().getPrice()).isEqualTo(1000L);
        assertThat(result.getProductInfo().getQuantity()).isEqualTo(1L);
        assertThat(result.getProductInfo().getImage()).isEqualTo("빵빵이썸네일");

        assertThat(result.getCancelRefundInfo().getDeliveryFee()).isEqualTo(0L);
        assertThat(result.getCancelRefundInfo().getRefundFee()).isEqualTo(0L);
        assertThat(result.getCancelRefundInfo().getDiscountPrice()).isEqualTo(3000L);
        assertThat(result.getCancelRefundInfo().getTotalPrice()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("Dto.ProductInfo 주어지면 Http.ProductInfo 반환한다.")
    public void productInfo_of() {
        // given
        RequestCancelDto.ProductInfo dto = RequestCancelDto.ProductInfo.builder()
                .name("빵빵이키링")
                .price(1000L)
                .quantity(1L)
                .image("빵빵이썸네일")
                .build();

        // when
        RequestCancelHttp.ProductInfo result = RequestCancelHttp.ProductInfo.of(dto);

        // then
        assertThat(result.getName()).isEqualTo("빵빵이키링");
        assertThat(result.getPrice()).isEqualTo(1000L);
        assertThat(result.getQuantity()).isEqualTo(1L);
        assertThat(result.getImage()).isEqualTo("빵빵이썸네일");
    }

    @Test
    @DisplayName("Dto.CancelRefundInfo가 주어지면 Http.CancelRefundInfo로 반환한다.")
    public void cancelRefundInfo_of() {
        RequestCancelDto.CancelRefundInfo dto = RequestCancelDto.CancelRefundInfo.builder()
                .deliveryFee(0L)
                .refundFee(0L)
                .discountPrice(3000L)
                .totalPrice(5000L)
                .build();

        // when
        RequestCancelHttp.CancelRefundInfo result = RequestCancelHttp.CancelRefundInfo.of(dto);

        // then
        assertThat(result.getDeliveryFee()).isEqualTo(0L);
        assertThat(result.getRefundFee()).isEqualTo(0L);
        assertThat(result.getDiscountPrice()).isEqualTo(3000L);
        assertThat(result.getTotalPrice()).isEqualTo(5000L);
    }
}