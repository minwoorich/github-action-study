package com.objects.marketbridge.domains.order.controller.dto;

import com.objects.marketbridge.domains.order.controller.dto.RequestReturnHttp;
import com.objects.marketbridge.domains.order.service.dto.RequestReturnDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestReturnHttpTest {

    @Test
    @DisplayName("Dto.Response 주어지면 Http.Response 반환한다.")
    public void response_of() {
        // given
        RequestReturnDto.ProductInfo dtoProductInfo = RequestReturnDto.ProductInfo.builder()
                .name("빵빵이키링")
                .price(1000L)
                .quantity(1L)
                .image("빵빵이썸네일")
                .build();

        RequestReturnDto.ReturnRefundInfo dtoReturnRefundInfo = RequestReturnDto.ReturnRefundInfo.builder()
                .deliveryFee(0L)
                .returnFee(0L)
                .productTotalPrice(5000L)
                .build();

        RequestReturnDto.Response dtoResponse = RequestReturnDto.Response.builder()
                .productInfo(dtoProductInfo)
                .returnRefundInfo(dtoReturnRefundInfo)
                .build();

        // when
        RequestReturnHttp.Response result = RequestReturnHttp.Response.of(dtoResponse);

        // then
        assertThat(result.getProductInfo().getName()).isEqualTo("빵빵이키링");
        assertThat(result.getProductInfo().getPrice()).isEqualTo(1000L);
        assertThat(result.getProductInfo().getQuantity()).isEqualTo(1L);
        assertThat(result.getProductInfo().getImage()).isEqualTo("빵빵이썸네일");

        assertThat(result.getReturnRefundInfo().getDeliveryFee()).isEqualTo(0L);
        assertThat(result.getReturnRefundInfo().getReturnFee()).isEqualTo(0L);
        assertThat(result.getReturnRefundInfo().getProductTotalPrice()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("Dto.ProductInfo 주어지면 Http.ProductInfo 반환한다.")
    public void productInfo_of() {
        // given
        RequestReturnDto.ProductInfo dto = RequestReturnDto.ProductInfo.builder()
                .quantity(1L)
                .name("빵빵이키링")
                .price(1000L)
                .image("빵빵이썸네일")
                .build();

        // when
        RequestReturnHttp.ProductInfo result = RequestReturnHttp.ProductInfo.of(dto);

        // then
        assertThat(result.getName()).isEqualTo("빵빵이키링");
        assertThat(result.getPrice()).isEqualTo(1000L);
        assertThat(result.getQuantity()).isEqualTo(1L);
        assertThat(result.getImage()).isEqualTo("빵빵이썸네일");
    }

    @Test
    @DisplayName("Dto.ReturnRefundInfo가 주어지면 Http.ReturnRefundInfo로 반환한다.")
    public void returnRefundInfo_of() {
        RequestReturnDto.ReturnRefundInfo dtoCancelRefundInfo = RequestReturnDto.ReturnRefundInfo.builder()
                .deliveryFee(0L)
                .returnFee(0L)
                .productTotalPrice(5000L)
                .build();

        // when
        RequestReturnHttp.ReturnRefundInfo result = RequestReturnHttp.ReturnRefundInfo.of(dtoCancelRefundInfo);

        // then
        assertThat(result.getDeliveryFee()).isEqualTo(0L);
        assertThat(result.getReturnFee()).isEqualTo(0L);
        assertThat(result.getProductTotalPrice()).isEqualTo(5000L);
    }

}