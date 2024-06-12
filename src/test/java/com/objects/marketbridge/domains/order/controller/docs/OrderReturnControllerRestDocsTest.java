package com.objects.marketbridge.domains.order.controller.docs;


import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objects.marketbridge.common.security.annotation.WithMockCustomUser;
import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.domain.MembershipType;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.order.controller.OrderReturnController;
import com.objects.marketbridge.domains.order.controller.dto.ConfirmReturnHttp;
import com.objects.marketbridge.domains.order.service.OrderReturnService;
import com.objects.marketbridge.domains.order.service.dto.ConfirmReturnDto;
import com.objects.marketbridge.domains.order.service.dto.GetReturnDetailDto;
import com.objects.marketbridge.domains.order.service.dto.RequestReturnDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;


import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@WebMvcTest(OrderReturnController.class)
@ExtendWith(RestDocumentationExtension.class)
public class OrderReturnControllerRestDocsTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OrderReturnService orderReturnService;
    @MockBean
    MemberRepository memberRepository;
    @MockBean
    DateTimeHolder dateTimeHolder;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }

    @Test
    @DisplayName("반품 확정 API")
    public void confirmReturn() throws Exception {
        // given
        ConfirmReturnHttp.Request request = ConfirmReturnHttp.Request.builder()
                .orderDetailId(1L)
                .numberOfReturns(2L)
                .reason("단순변심")
                .build();

        LocalDateTime cancellationDate = LocalDateTime.of(2024, 1, 18, 12, 26);
        LocalDateTime refundProcessedAt = LocalDateTime.of(2024, 1, 18, 12, 26);

        ConfirmReturnDto.RefundInfo refundInfo = ConfirmReturnDto.RefundInfo.builder()
                .refundProcessedAt(refundProcessedAt)
                .refundMethod("카드")
                .totalRefundAmount(2000L)
                .build();
        ConfirmReturnDto.ProductInfo productInfo = ConfirmReturnDto.ProductInfo.builder()
                .productId(1L)
                .name("빵빵이 키링")
                .productNo("1")
                .price(2000L)
                .quantity(2L)
                .build();
        ConfirmReturnDto.Response response = ConfirmReturnDto.Response.builder()
                .orderId(1L)
                .orderNo("1")
                .totalPrice(2000L)
                .returnedDate(cancellationDate)
                .refundInfo(refundInfo)
                .returnedItem(productInfo)
                .build();

        given(orderReturnService.confirmReturn(any(ConfirmReturnDto.Request.class), any(DateTimeHolder.class)))
                .willReturn(response);

        // when // then
        mockMvc.perform(
                        post("/orders/return-flow/thank-you")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("order-confirm-return",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .requestFields(
                                                fieldWithPath("orderDetailId").type(JsonFieldType.NUMBER)
                                                        .description("주문 상세 ID"),
                                                fieldWithPath("numberOfReturns").type(JsonFieldType.NUMBER)
                                                        .description("반품 개수"),
                                                fieldWithPath("reason").type(JsonFieldType.STRING)
                                                        .description("반품 이유")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                        .description("코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                        .description("상태"),
                                                fieldWithPath("message").type(JsonFieldType.STRING)
                                                        .description("메시지"),
                                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                                        .description("응답 데이터"),
                                                fieldWithPath("data.orderId").type(JsonFieldType.NUMBER)
                                                        .description("주문 ID"),
                                                fieldWithPath("data.orderNo").type(JsonFieldType.STRING)
                                                        .description("주문 번호"),
                                                fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER)
                                                        .description("주문 총 가격"),
                                                fieldWithPath("data.returnedDate").type(JsonFieldType.STRING)
                                                        .description("주문 반품 시간"),
                                                fieldWithPath("data.returnedItem").type(JsonFieldType.OBJECT)
                                                        .description("반품된 상품"),
                                                fieldWithPath("data.returnedItem.productId").type(JsonFieldType.NUMBER)
                                                        .description("반품된 상품의 제품 ID"),
                                                fieldWithPath("data.returnedItem.productNo").type(JsonFieldType.STRING)
                                                        .description("반품된 상품의 제품 번호"),
                                                fieldWithPath("data.returnedItem.name").type(JsonFieldType.STRING)
                                                        .description("반품된 상품의 이름"),
                                                fieldWithPath("data.returnedItem.price").type(JsonFieldType.NUMBER)
                                                        .description("반품된 상품의 가격"),
                                                fieldWithPath("data.returnedItem.quantity").type(JsonFieldType.NUMBER)
                                                        .description("반품된 주문 수량"),
                                                fieldWithPath("data.refundInfo").type(JsonFieldType.OBJECT)
                                                        .description("환불 정보"),
                                                fieldWithPath("data.refundInfo.totalRefundAmount").type(JsonFieldType.NUMBER)
                                                        .description("환불 총 금액"),
                                                fieldWithPath("data.refundInfo.refundMethod").type(JsonFieldType.STRING)
                                                        .description("환불 방법"),
                                                fieldWithPath("data.refundInfo.refundProcessedAt").type(JsonFieldType.STRING)
                                                        .description("환불 시간")
                                        )
                                        .requestSchema(Schema.schema("PostOrdersReturnFlowReq"))
                                        .responseSchema(Schema.schema("PostOrdersReturnFlowRes"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("주문 반품 요청 API")
    @WithMockCustomUser
    public void requestReturn() throws Exception {
        // given
        RequestReturnDto.Response response = RequestReturnDto.Response.builder()
                .returnRefundInfo(
                        RequestReturnDto.ReturnRefundInfo.builder()
                                .returnFee(0L)
                                .deliveryFee(0L)
                                .productTotalPrice(2000L)
                                .build()
                )
                .productInfo(
                        RequestReturnDto.ProductInfo.builder()
                                .quantity(2L)
                                .name("빵빵이키링")
                                .price(1000L)
                                .image("빵빵이썸네일")
                                .build()
                )
                .build();

        Member member = Member.builder()
                .membership(MembershipType.WOW.getText())
                .build();

        given(memberRepository.findById(anyLong())).willReturn(member);
        given(orderReturnService.findReturnInfo(anyLong(), anyLong(), anyString()))
                .willReturn(response);

        // when // then
        mockMvc.perform(
                        get("/orders/return-flow")
                                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                                .accept(MediaType.APPLICATION_JSON)
                                .param("orderDetailId", "1")
                                .param("numberOfReturns", "2")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("order-return-request",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .queryParameters(
                                                parameterWithName("orderDetailId")
                                                        .description("주문 상세 ID"),
                                                parameterWithName("numberOfReturns")
                                                        .description("취소 수량")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                        .description("코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                        .description("상태"),
                                                fieldWithPath("message").type(JsonFieldType.STRING)
                                                        .description("메시지"),
                                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                                        .description("응답 데이터"),
                                                fieldWithPath("data.productInfo").type(JsonFieldType.OBJECT)
                                                        .description("반품 상품 정보"),
                                                fieldWithPath("data.productInfo.quantity").type(JsonFieldType.NUMBER)
                                                        .description("반품 상품 수량"),
                                                fieldWithPath("data.productInfo.name").type(JsonFieldType.STRING)
                                                        .description("반품 상품 이름"),
                                                fieldWithPath("data.productInfo.price").type(JsonFieldType.NUMBER)
                                                        .description("반품 상품 가격"),
                                                fieldWithPath("data.productInfo.image").type(JsonFieldType.STRING)
                                                        .description("반품 상품 썸네일"),
                                                fieldWithPath("data.returnRefundInfo").type(JsonFieldType.OBJECT)
                                                        .description("반품 환불 정보"),
                                                fieldWithPath("data.returnRefundInfo.deliveryFee").type(JsonFieldType.NUMBER)
                                                        .description("환불 배송비"),
                                                fieldWithPath("data.returnRefundInfo.returnFee").type(JsonFieldType.NUMBER)
                                                        .description("반품 배송비"),
                                                fieldWithPath("data.returnRefundInfo.productTotalPrice").type(JsonFieldType.NUMBER)
                                                        .description("환불(상품) 금액")
                                        )
                                        .responseSchema(Schema.schema("GetOrdersReturnFlowRes"))
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("반품 상세를 조회한다.")
    @WithMockCustomUser
    public void getReturnDetail() throws Exception {
        // given
        GetReturnDetailDto.ProductInfo productInfo = GetReturnDetailDto.ProductInfo.builder()
                .productId(1L)
                .productNo("1")
                .name("빵빵이 키링")
                .price(10000L)
                .quantity(2L)
                .build();

        GetReturnDetailDto.RefundInfo refundInfo = GetReturnDetailDto.RefundInfo.builder()
                .deliveryFee(0L)
                .refundFee(0L)
                .discountPrice(5000L)
                .totalPrice(100000L)
                .build();

        GetReturnDetailDto.Response response = GetReturnDetailDto.Response.builder()
                .orderDate(LocalDateTime.now())
                .cancelDate(LocalDateTime.now())
                .orderNo("123")
                .reason("빵빵이 기여워")
                .productInfo(productInfo)
                .refundInfo(refundInfo)
                .build();

        Member member = Member.builder()
                .membership(MembershipType.WOW.getText())
                .build();

        given(memberRepository.findById(anyLong())).willReturn(member);
        given(orderReturnService.findReturnDetail(anyLong(), any(String.class)))
                .willReturn(response);

        // when // then
        mockMvc.perform(
                        get("/orders/return/detail")
                                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                                .accept(MediaType.APPLICATION_JSON)
                                .param("returnedOrderDetailId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("order-return-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .queryParameters(
                                                parameterWithName("returnedOrderDetailId")
                                                        .description("반품 상세 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                        .description("코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                        .description("상태"),
                                                fieldWithPath("message").type(JsonFieldType.STRING)
                                                        .description("메시지"),
                                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                                        .description("응답 데이터"),
                                                fieldWithPath("data.orderDate").type(JsonFieldType.STRING)
                                                        .description("주문 날짜"),
                                                fieldWithPath("data.cancelDate").type(JsonFieldType.STRING)
                                                        .description("주문 취소 날짜"),
                                                fieldWithPath("data.orderNo").type(JsonFieldType.STRING)
                                                        .description("주문 번호"),
                                                fieldWithPath("data.reason").type(JsonFieldType.STRING)
                                                        .description("주문 취소 이유"),
                                                fieldWithPath("data.productInfo").type(JsonFieldType.OBJECT)
                                                        .description("상품 정보"),
                                                fieldWithPath("data.productInfo.productId").type(JsonFieldType.NUMBER)
                                                        .description("상품 ID"),
                                                fieldWithPath("data.productInfo.productNo").type(JsonFieldType.STRING)
                                                        .description("상품 번호"),
                                                fieldWithPath("data.productInfo.name").type(JsonFieldType.STRING)
                                                        .description("상품 이름"),
                                                fieldWithPath("data.productInfo.price").type(JsonFieldType.NUMBER)
                                                        .description("주문 가격"),
                                                fieldWithPath("data.productInfo.quantity").type(JsonFieldType.NUMBER)
                                                        .description("상품 주문 수량"),
                                                fieldWithPath("data.refundInfo").type(JsonFieldType.OBJECT)
                                                        .description("취소/반품 정보"),
                                                fieldWithPath("data.refundInfo.deliveryFee").type(JsonFieldType.NUMBER)
                                                        .description("배송 비용"),
                                                fieldWithPath("data.refundInfo.refundFee").type(JsonFieldType.NUMBER)
                                                        .description("반품 비용"),
                                                fieldWithPath("data.refundInfo.discountPrice").type(JsonFieldType.NUMBER)
                                                        .description("할인 금액"),
                                                fieldWithPath("data.refundInfo.totalPrice").type(JsonFieldType.NUMBER)
                                                        .description("상품 할인 전 금액 합계")
                                        )
                                        .responseSchema(Schema.schema("GetOrdersReturnDetailRes"))
                                        .build()
                        )
                ));
    }

}
