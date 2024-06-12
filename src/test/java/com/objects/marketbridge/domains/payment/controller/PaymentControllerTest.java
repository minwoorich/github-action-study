package com.objects.marketbridge.domains.payment.controller;

import com.objects.marketbridge.common.kakao.dto.KakaoPayApproveResponse;
import com.objects.marketbridge.domains.member.domain.AddressValue;
import com.objects.marketbridge.domains.payment.controller.PaymentController;
import com.objects.marketbridge.domains.payment.controller.dto.CancelledPaymentHttp;
import com.objects.marketbridge.domains.payment.controller.dto.CompleteOrderHttp;
import com.objects.marketbridge.domains.payment.service.CreatePaymentService;
import com.objects.marketbridge.domains.payment.service.QuitPaymentService;
import com.objects.marketbridge.domains.payment.service.dto.ProductInfoDto;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.objects.marketbridge.common.kakao.enums.CardCoType.KAKAOBANK;
import static com.objects.marketbridge.common.kakao.enums.KakaoStatus.QUIT_PAYMENT;
import static com.objects.marketbridge.domains.payment.domain.PaymentType.CARD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@WebMvcTest(PaymentController.class)
@ExtendWith(RestDocumentationExtension.class)
class PaymentControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean CreatePaymentService createPaymentService;
    @MockBean QuitPaymentService quitPaymentService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }

    @DisplayName("결제 성공")
    @Test
    void createPayment() throws Exception {
        // given
        CompleteOrderHttp.Response response = createResponse();

        // when
        KakaoPayApproveResponse kakaoPayApproveResponse = KakaoPayApproveResponse.builder().build();
        given(createPaymentService.approve(anyString(), anyString())).willReturn(kakaoPayApproveResponse);
        given(createPaymentService.create(any(KakaoPayApproveResponse.class))).willReturn(response);

        //then
        mockMvc.perform(get("/payment/kakao-pay/approval/{orderNo}", "orderNo1")
                        .param("pg_token", "pgTokenValue")
                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("payment-create",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("orderNo").description("주문 번호")
                        ),
                        queryParameters(
                                parameterWithName("pg_token").description("pg토큰값")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("응답 코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("HTTP 응답"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),

                                fieldWithPath("data.paymentMethodType").type(JsonFieldType.STRING)
                                        .description("결제 수단"),
                                fieldWithPath("data.orderName").type(JsonFieldType.STRING)
                                        .description("주문 이름"),
                                fieldWithPath("data.approvedAt").type(JsonFieldType.STRING)
                                        .description("결제 승인 일시 (yyyy-MM-dd HH:mm:ss)"),

                                fieldWithPath("data.totalAmount").type(JsonFieldType.NUMBER)
                                        .description("총 결제 금액"),
                                fieldWithPath("data.discountAmount").type(JsonFieldType.NUMBER)
                                        .description("총 할인 금액"),
                                fieldWithPath("data.taxFreeAmount").type(JsonFieldType.NUMBER)
                                        .description("상품 비과세 금액"),

                                fieldWithPath("data.cardIssuerName").type(JsonFieldType.STRING)
                                        .description("카드 발급사 명 (현금결제시 null)"),
                                fieldWithPath("data.cardInstallMonth").type(JsonFieldType.NUMBER)
                                        .description("할부 개월 수 (현금결제시 null)"),

                                fieldWithPath("data.addressValue").type(JsonFieldType.OBJECT)
                                        .description("주문자 주소 정보"),
                                fieldWithPath("data.addressValue.phoneNo").type(JsonFieldType.STRING)
                                        .description("주문자 연락처"),
                                fieldWithPath("data.addressValue.name").type(JsonFieldType.STRING)
                                        .description("주문자 이름"),
                                fieldWithPath("data.addressValue.city").type(JsonFieldType.STRING)
                                        .description("도시"),
                                fieldWithPath("data.addressValue.street").type(JsonFieldType.STRING)
                                        .description("도로명"),
                                fieldWithPath("data.addressValue.zipcode").type(JsonFieldType.STRING)
                                        .description("우편번호"),
                                fieldWithPath("data.addressValue.detail").type(JsonFieldType.STRING)
                                        .description("상세 주소"),
                                fieldWithPath("data.addressValue.alias").type(JsonFieldType.STRING)
                                        .description("주소 별칭"),

                                fieldWithPath("data.productInfos[]").type(JsonFieldType.ARRAY)
                                        .description("주문 상품 리스트"),
                                fieldWithPath("data.productInfos[].isOwn").type(JsonFieldType.BOOLEAN)
                                        .description("마켓브릿지 상품인지 구분하는 값"),
                                fieldWithPath("data.productInfos[].name").type(JsonFieldType.STRING)
                                        .description("상품 이름"),
                                fieldWithPath("data.productInfos[].price").type(JsonFieldType.NUMBER)
                                        .description("상품 가격"),
                                fieldWithPath("data.productInfos[].isSubs").type(JsonFieldType.BOOLEAN)
                                        .description("정기 배송 상품인지 구분하는 값"),
                                fieldWithPath("data.productInfos[].thumbImgUrl").type(JsonFieldType.STRING)
                                        .description("상품 썸네일 이미지 URL"),
                                fieldWithPath("data.productInfos[].discountRate").type(JsonFieldType.NUMBER)
                                        .description("할인율(0~100)"),
                                fieldWithPath("data.productInfos[].sellerName").type(JsonFieldType.STRING)
                                        .description("판매자 이름"),
                                fieldWithPath("data.productInfos[].deliveredDate").type(JsonFieldType.STRING)
                                        .description("배송 도착 일자 (yyyy-MM-dd)")
                        )));
    }

    private CompleteOrderHttp.Response createResponse() {
        return CompleteOrderHttp.Response.create(CARD.toString(), "가방 외 1건",
                "2024-01-01 13:30:20",
                10000L, 1000L, 0L,
                "카카오뱅크", 0L,
                createAddressValue("01044442222", "홍길동", "서울", "세종대로", "23333", "우리집", "민들레아파트 110동 1212호"),
                createProductInfoDtos());
    }

    private AddressValue createAddressValue(String phoneNo, String name, String city, String street, String zipcode, String detail, String alias) {
        return AddressValue.create(phoneNo, name, city, street, zipcode, detail, alias);
    }

    private List<ProductInfoDto> createProductInfoDtos() {
        ProductInfoDto productInfoDto = ProductInfoDto.create(true, "가방", 10000L, false, "thumbImgUrl", 0L, "임꺽정", "2024-01-01");

        return List.of(productInfoDto);
    }

    @DisplayName("결제 승인 취소")
    @Test
    void kakaoPaymentApproveCancel() throws Exception {

        // given
        CancelledPaymentHttp.Response response = createCancelledResponse();
        given(quitPaymentService.response(anyString())).willReturn(response);
        willDoNothing().given(quitPaymentService).cancel(anyString());

        // when, then
        mockMvc.perform(get("/kakao-pay/cancel/{orderNo}", "orderNo1")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("payment-cancel",
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("orderNo").description("주문 번호")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("응답 코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("HTTP 응답"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),

                                fieldWithPath("data.paymentMethodType").type(JsonFieldType.STRING)
                                        .description("결제 수단"),
                                fieldWithPath("data.orderName").type(JsonFieldType.STRING)
                                        .description("주문 이름"),
                                fieldWithPath("data.canceledAt").type(JsonFieldType.NULL)
                                        .description("주문 취소 일시 (yyyy-MM-dd HH:mm:ss) (결제 취소인 경우엔 null)"),
                                fieldWithPath("data.kakaoStatus").type(JsonFieldType.STRING)
                                        .description("카카오 결제 상태"),
                                fieldWithPath("data.totalAmount").type(JsonFieldType.NUMBER)
                                        .description("총 결제 금액"),
                                fieldWithPath("data.discountAmount").type(JsonFieldType.NUMBER)
                                        .description("총 할인 금액"),
                                fieldWithPath("data.taxFreeAmount").type(JsonFieldType.NUMBER)
                                        .description("상품 비과세 금액"),

                                fieldWithPath("data.cardIssuerName").type(JsonFieldType.STRING)
                                        .description("카드 발급사 명 (현금결제시 null)"),
                                fieldWithPath("data.cardInstallMonth").type(JsonFieldType.NUMBER)
                                        .description("할부 개월 수 (현금결제시 null)"),

                                fieldWithPath("data.productInfos[]").type(JsonFieldType.ARRAY)
                                        .description("주문 상품 리스트"),
                                fieldWithPath("data.productInfos[].isOwn").type(JsonFieldType.BOOLEAN)
                                        .description("마켓브릿지 상품인지 구분하는 값"),
                                fieldWithPath("data.productInfos[].name").type(JsonFieldType.STRING)
                                        .description("상품 이름"),
                                fieldWithPath("data.productInfos[].price").type(JsonFieldType.NUMBER)
                                        .description("상품 가격"),
                                fieldWithPath("data.productInfos[].isSubs").type(JsonFieldType.BOOLEAN)
                                        .description("정기 배송 상품인지 구분하는 값"),
                                fieldWithPath("data.productInfos[].thumbImgUrl").type(JsonFieldType.STRING)
                                        .description("상품 썸네일 이미지 URL"),
                                fieldWithPath("data.productInfos[].discountRate").type(JsonFieldType.NUMBER)
                                        .description("할인율(0~100)"),
                                fieldWithPath("data.productInfos[].sellerName").type(JsonFieldType.STRING)
                                        .description("판매자 이름"),
                                fieldWithPath("data.productInfos[].deliveredDate").type(JsonFieldType.STRING)
                                        .description("예상 도착 날짜 ((yyyy-MM-dd))")
                        )));
    }

    private CancelledPaymentHttp.Response createCancelledResponse() {
        return CancelledPaymentHttp.Response.create(
                CARD.toString(),
                "가방 외 1건",
                null,
                QUIT_PAYMENT.toString(),
                10000L,
                500L,
                0L,
                KAKAOBANK.toString(),
                0L,
                createProductInfoDtos()
                );
    }
}