package com.objects.marketbridge.domains.order.controller.dto;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.kakao.dto.KakaoPayReadyRequest;
import com.objects.marketbridge.common.kakao.dto.KakaoPayReadyResponse;
import com.objects.marketbridge.domains.order.controller.dto.CreateOrderHttp;
import com.objects.marketbridge.domains.order.domain.OrderDetail;
import com.objects.marketbridge.domains.order.domain.ProductValue;
import com.objects.marketbridge.domains.order.service.dto.CreateOrderDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class CreateOrderHttpTest {

    @DisplayName("CreateOrderHttp.Request 를 CreateOrderDto 로 변환할 수 있다")
    @Test
    void requestToDto() {

        // given
        CreateOrderHttp.Request request = CreateOrderHttp.Request.builder()
                .totalAmount(3000L)
                .realAmount(1500L)
                .totalDiscountAmount(1500L)
                .addressId(1L)
                .orderName("가방외 3건")
                .productValues(createProductValues())
                .build();

        String orderNo = "aaaa-aaaa-aaaa";
        String tid = "tid";
        Long memberId = 1L;

        // when
        CreateOrderDto dto = request.toDto(orderNo, tid, memberId);

        //then
        assertThat(dto).extracting(
                "tid",
                "memberId",
                "addressId",
                "orderName",
                "orderNo",
                "totalOrderPrice",
                "realOrderPrice",
                "totalDiscountPrice")
                .containsExactlyInAnyOrder(
                "tid",
                1L,
                request.getAddressId(),
                request.getOrderName(),
                "aaaa-aaaa-aaaa",
                request.getTotalAmount(),
                request.getRealAmount(),
                request.getTotalDiscountAmount()
        );
    }

    @DisplayName("CreateOrderHttp.Request.ProductInfo 를 CreateOrderDto.ProductDto 로 변환할 수 있다")
    @Test
    void productInfoToDto() {

        // given
        CreateOrderHttp.Request.ProductInfo productInfo = CreateOrderHttp.Request.ProductInfo.builder()
                .productId(1L)
                .price(1000L)
                .quantity(1L)
                .hasCouponUsed(true)
                .couponId(1L)
                .couponPrice(500L)
                .couponMinimumPrice(1L)
                .couponEndDate("2024-12-12 12:00:00")
                .deliveredDate("2023-12-12 12:00:00")
                .build();

        // when
        CreateOrderDto.ProductDto productDto = productInfo.toDto();

        //then
        assertThat(productDto).extracting(
                "productId",
                "price",
                "quantity",
                "hasCouponUsed",
                "couponId",
                "couponPrice",
                "couponMinimumPrice",
                "couponEndDate",
                "deliveredDate").containsExactly(
                productInfo.getProductId(),
                productInfo.getPrice(),
                productInfo.getQuantity(),
                productInfo.getHasCouponUsed(),
                productInfo.getCouponId(),
                productInfo.getCouponPrice(),
                productInfo.getCouponMinimumPrice(),
                productInfo.getCouponEndDate(),
                productInfo.getDeliveredDate()
        );

    }

    @DisplayName("CreateOrderHttp.Request 를 KakaoPayReadyRequest 로 변환할 수 있다")
    @Test
    void requestToKakaoPayReadyRequest() {

        // given
        CreateOrderHttp.Request request = CreateOrderHttp.Request.builder()
                .totalAmount(3000L)
                .realAmount(1500L)
                .totalDiscountAmount(1500L)
                .addressId(1L)
                .orderName("가방외 3건")
                .productValues(createProductValues())
                .build();

        String orderNo = "aaaa-aaaa-aaaa";
        Long memberId = 1L;
        String cid = "cid";
        String approvalUrl = "approvalUrl";
        String failUrl = "failUrl";
        String cancelUrl = "cancelUrl";

        // when
        KakaoPayReadyRequest kakaoReadyRequest = request.toKakaoReadyRequest(orderNo, memberId, cid, approvalUrl, failUrl, cancelUrl);

        //then
        assertThat(kakaoReadyRequest).extracting(
                        "partnerOrderId",
                        "partnerUserId",
                        "cid",
                        "approvalUrl",
                        "failUrl",
                        "cancelUrl",
                        "quantity",
                        "itemName",
                        "taxFreeAmount",
                        "totalAmount")
                .containsExactlyInAnyOrder(
                        orderNo,
                        memberId.toString(),
                        cid,
                        approvalUrl+"/"+orderNo,
                        failUrl+"/"+orderNo,
                        cancelUrl+"/"+orderNo,
                        request.getProductValues().stream().mapToLong(CreateOrderHttp.Request.ProductInfo::getQuantity).sum(),
                        request.getOrderName(),
                        0L,
                        request.getRealAmount()
                );

    }

    @DisplayName("KakaoPayReadyResponse 로 부터 CreateOrderHttp.Response 를 만들 수 있다.")
    @Test
    void response_of() {
        // given
        KakaoPayReadyResponse kakaoResp = KakaoPayReadyResponse.builder()
                .tid("tid")
                .nextRedirectPcUrl("pc")
                .nextRedirectAppUrl("app")
                .nextRedirectMobileUrl("mobile")
                .androidAppScheme("android")
                .iosAppScheme("app")
                .createdAt("createdAt")
                .build();

        // when
        CreateOrderHttp.Response createOrderResp = CreateOrderHttp.Response.of(kakaoResp);

        //then
        assertThat(createOrderResp).extracting(
                "tid",
                "nextRedirectPcUrl",
                "nextRedirectAppUrl",
                "nextRedirectMobileUrl",
                "androidAppScheme",
                "iosAppScheme",
                "createdAt").containsExactlyInAnyOrder(
                kakaoResp.getTid(),
                kakaoResp.getNextRedirectPcUrl(),
                kakaoResp.getNextRedirectAppUrl(),
                kakaoResp.getNextRedirectMobileUrl(),
                kakaoResp.getAndroidAppScheme(),
                kakaoResp.getIosAppScheme(),
                kakaoResp.getCreatedAt());
    }

    private List<CreateOrderHttp.Request.ProductInfo> createProductValues(){
        CreateOrderHttp.Request.ProductInfo productValue1 = CreateOrderHttp.Request.ProductInfo.builder()
                .productId(1L)
                .price(1000L)
                .quantity(1L)
                .hasCouponUsed(true)
                .couponId(1L)
                .couponPrice(500L)
                .couponMinimumPrice(1L)
                .couponEndDate("2024-12-12 12:00:00")
                .deliveredDate("2023-03-31").build();

        CreateOrderHttp.Request.ProductInfo productValue2 = CreateOrderHttp.Request.ProductInfo.builder()
                .productId(2L)
                .price(1000L)
                .quantity(1L)
                .hasCouponUsed(true)
                .couponId(2L)
                .couponPrice(500L)
                .couponMinimumPrice(1L)
                .couponEndDate("2024-12-12 12:00:00")
                .deliveredDate("2023-03-31").build();

        CreateOrderHttp.Request.ProductInfo productValue3 = CreateOrderHttp.Request.ProductInfo.builder()
                .productId(3L)
                .price(1000L)
                .quantity(1L)
                .hasCouponUsed(true)
                .couponId(3L)
                .couponPrice(500L)
                .couponMinimumPrice(1L)
                .couponEndDate("2024-12-12 12:00:00")
                .deliveredDate("2023-03-31").build();

        return List.of(productValue1, productValue2, productValue3);
    }

//    @DisplayName("@NotNull 이 붙은 필드에 값이 들어오지 않으면 예외를 발생한다")
//    @Test
//    void validation1(){
//        //given, when
//        Throwable thrown = catchThrowable(() -> CreateOrderHttp.Request.builder()
//                .totalAmount(null)
//                .realAmount(1500L)
//                .totalDiscountAmount(1500L)
//                .addressId(1L)
//                .orderName("가방외 3건")
//                .productValues(createProductValues())
//                .build());
//
//        //then
//        assertThat(thrown).isInstanceOf(CustomLogicException.class);
//
//    }

}