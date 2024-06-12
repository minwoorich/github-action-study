package com.objects.marketbridge.domains.order.controller.docs;

import com.objects.marketbridge.common.RestDocsSupportWebAppContext;
import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.kakao.dto.KakaoPayReadyResponse;
import com.objects.marketbridge.common.security.annotation.WithMockCustomUser;
import com.objects.marketbridge.domains.order.controller.OrderController;
import com.objects.marketbridge.domains.order.controller.dto.CreateOrderHttp;
import com.objects.marketbridge.domains.order.service.CreateOrderService;
import com.objects.marketbridge.domains.order.service.GetOrderService;
import com.objects.marketbridge.domains.order.service.dto.CreateOrderDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@WebMvcTest(OrderController.class)
public class OrderControllerErrorTest extends RestDocsSupportWebAppContext {

    @MockBean CreateOrderService createOrderService;
    @MockBean GetOrderService getOrderService;

    @DisplayName("[POST/orders] 상품 재고가 없는경우")
    @Test
    @WithMockCustomUser
    void createOrder_error1() throws Exception {

        // given
        CreateOrderHttp.Request createOrderRequest = getCreateOrderRequest(createProductValues());
        KakaoPayReadyResponse response = KakaoPayReadyResponse.builder()
                .tid("tid")
                .nextRedirectPcUrl("nextRedirectPcUrl")
                .nextRedirectAppUrl("nextRedirectAppUrl")
                .nextRedirectMobileUrl("nextRedirectMobileUrl")
                .androidAppScheme("androidAppScheme")
                .iosAppScheme("iosAppScheme")
                .createdAt("createdAt")
                .build();

        given(createOrderService.ready(any(CreateOrderHttp.Request.class),anyString(), anyLong())).willReturn(response);
        doThrow(CustomLogicException.createBadRequestError(OUT_OF_STOCK))
                .when(createOrderService).create(any(CreateOrderDto.class));

        // when
        MockHttpServletRequestBuilder requestBuilder =
                post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(createOrderRequest))
                        .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(OUT_OF_STOCK.name()))
                .andExpect(jsonPath("$.message").value("재고가 없습니다."))
                .andDo(print())
                .andDo(document("order-create-outofstock-error",
                        preprocessResponse(prettyPrint())
                ));
    }


    @DisplayName("[POST/orders] 이미 사용한 쿠폰을 사용하려고 할 경우")
    @Test
    @WithMockCustomUser
    void createOrder_error2() throws Exception {

        // given
        CreateOrderHttp.Request createOrderRequest = getCreateOrderRequest(createProductValues());
        KakaoPayReadyResponse response = KakaoPayReadyResponse.builder()
                .tid("tid")
                .nextRedirectPcUrl("nextRedirectPcUrl")
                .nextRedirectAppUrl("nextRedirectAppUrl")
                .nextRedirectMobileUrl("nextRedirectMobileUrl")
                .androidAppScheme("androidAppScheme")
                .iosAppScheme("iosAppScheme")
                .createdAt("createdAt")
                .build();

        given(createOrderService.ready(any(CreateOrderHttp.Request.class),anyString(), anyLong())).willReturn(response);
        doThrow(CustomLogicException.createBadRequestError(COUPON_ALREADY_USED))
                .when(createOrderService).create(any(CreateOrderDto.class));

        // when
        MockHttpServletRequestBuilder requestBuilder =
                post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(createOrderRequest))
                        .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(COUPON_ALREADY_USED.name()))
                .andExpect(jsonPath("$.message").value("이미 사용한 쿠폰입니다."))
                .andDo(print())
                .andDo(document("order-create-couponalreadyused-error",
                        preprocessResponse(prettyPrint())
                ));
    }

    @DisplayName("[POST/orders] 쿠폰 최소 주문금액보다 적게 주문 한 경우")
    @Test
    @WithMockCustomUser
    void createOrder_error3() throws Exception {

        // given
        CreateOrderHttp.Request createOrderRequest = getCreateOrderRequest(createProductValues());
        KakaoPayReadyResponse response = KakaoPayReadyResponse.builder()
                .tid("tid")
                .nextRedirectPcUrl("nextRedirectPcUrl")
                .nextRedirectAppUrl("nextRedirectAppUrl")
                .nextRedirectMobileUrl("nextRedirectMobileUrl")
                .androidAppScheme("androidAppScheme")
                .iosAppScheme("iosAppScheme")
                .createdAt("createdAt")
                .build();

        given(createOrderService.ready(any(CreateOrderHttp.Request.class),anyString(), anyLong())).willReturn(response);
        doThrow(CustomLogicException.createBadRequestError(COUPON_CONDITION_VIOLATION))
                .when(createOrderService).create(any(CreateOrderDto.class));

        // when
        MockHttpServletRequestBuilder requestBuilder =
                post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(createOrderRequest))
                        .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(COUPON_CONDITION_VIOLATION.name()))
                .andExpect(jsonPath("$.message").value("최소 주문 금액 조건 미달입니다."))
                .andDo(print())
                .andDo(document("order-create-couponconditionviolation-error",
                        preprocessResponse(prettyPrint())
                ));
    }

    @DisplayName("[POST/orders] 유효기간 만료된 쿠폰 사용할 경우")
    @Test
    @WithMockCustomUser
    void createOrder_error4() throws Exception {

        // given
        CreateOrderHttp.Request createOrderRequest = getCreateOrderRequest(createProductValues());
        KakaoPayReadyResponse response = KakaoPayReadyResponse.builder()
                .tid("tid")
                .nextRedirectPcUrl("nextRedirectPcUrl")
                .nextRedirectAppUrl("nextRedirectAppUrl")
                .nextRedirectMobileUrl("nextRedirectMobileUrl")
                .androidAppScheme("androidAppScheme")
                .iosAppScheme("iosAppScheme")
                .createdAt("createdAt")
                .build();

        given(createOrderService.ready(any(CreateOrderHttp.Request.class),anyString(), anyLong())).willReturn(response);
        doThrow(CustomLogicException.createBadRequestError(COUPON_EXPIRED))
                .when(createOrderService).create(any(CreateOrderDto.class));

        // when
        MockHttpServletRequestBuilder requestBuilder =
                post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(createOrderRequest))
                        .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(COUPON_EXPIRED.name()))
                .andExpect(jsonPath("$.message").value("만료된 쿠폰입니다."))
                .andDo(print())
                .andDo(document("order-create-couponexpired-error",
                        preprocessResponse(prettyPrint())
                ));
    }

    @DisplayName("[POST/orders] 상품과 관련이없는 쿠폰을 사용하려고 할 경우")
    @Test
    @WithMockCustomUser
    void createOrder_error5() throws Exception {

        // given
        CreateOrderHttp.Request createOrderRequest = getCreateOrderRequest(createProductValues());
        KakaoPayReadyResponse response = KakaoPayReadyResponse.builder()
                .tid("tid")
                .nextRedirectPcUrl("nextRedirectPcUrl")
                .nextRedirectAppUrl("nextRedirectAppUrl")
                .nextRedirectMobileUrl("nextRedirectMobileUrl")
                .androidAppScheme("androidAppScheme")
                .iosAppScheme("iosAppScheme")
                .createdAt("createdAt")
                .build();

        given(createOrderService.ready(any(CreateOrderHttp.Request.class),anyString(), anyLong())).willReturn(response);
        doThrow(CustomLogicException.createBadRequestError(COUPON_INCOMPATIBLE))
                .when(createOrderService).create(any(CreateOrderDto.class));

        // when
        MockHttpServletRequestBuilder requestBuilder =
                post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(createOrderRequest))
                        .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(COUPON_INCOMPATIBLE.name()))
                .andExpect(jsonPath("$.message").value("상품과 호환되지 않는 쿠폰입니다."))
                .andDo(print())
                .andDo(document("order-create-couponincompatible-error",
                        preprocessResponse(prettyPrint())
                ));
    }

    private CreateOrderHttp.Request getCreateOrderRequest(List<CreateOrderHttp.Request.ProductInfo> productValues) {
        return CreateOrderHttp.Request.builder()
                .totalAmount(10000L)
                .realAmount(9500L)
                .totalDiscountAmount(500L)
                .addressId(1L)
                .orderName("가방 1건")
                .productValues(productValues)
                .build();
    }

    private List<CreateOrderHttp.Request.ProductInfo> createProductValues() {
        CreateOrderHttp.Request.ProductInfo productValue1 = CreateOrderHttp.Request.ProductInfo.builder()
                .productId(1L)
                .price(10000L)
                .quantity(1L)
                .hasCouponUsed(true)
                .couponId(1L)
                .couponPrice(500L)
                .couponMinimumPrice(1000L)
                .couponEndDate("2030-01-01T12:00:00")
                .deliveredDate("2024-01-21")
                .build();

        return List.of(productValue1);
    }
}
