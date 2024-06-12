package com.objects.marketbridge.domains.member.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objects.marketbridge.common.kakao.KakaoPayConfig;
import com.objects.marketbridge.common.kakao.dto.KakaoPayApproveResponse;
import com.objects.marketbridge.common.kakao.dto.KakaoPayReadyRequest;
import com.objects.marketbridge.common.kakao.dto.KakaoPayReadyResponse;
import com.objects.marketbridge.common.security.annotation.WithMockCustomUser;
import com.objects.marketbridge.common.security.config.SpringSecurityTestConfig;
import com.objects.marketbridge.domains.member.controller.request.CreateSubsRequest;
import com.objects.marketbridge.domains.member.dto.CreateSubsDto;
import com.objects.marketbridge.domains.member.service.MemberShipService;
import com.objects.marketbridge.domains.payment.domain.Amount;
import com.objects.marketbridge.domains.payment.domain.CardInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.time.LocalDateTime;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(MemberShipController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {SpringSecurityTestConfig.class})
@ExtendWith(RestDocumentationExtension.class)
public class MembershipControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MemberShipService memberShipService;
    @MockBean
    private KakaoPayConfig kakaoPayConfig;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }


    @Test
    @DisplayName("SID를 받기위한 0회차 결제 준비")
    @WithMockCustomUser
    public void saveOrder() throws Exception {
        //given
        CreateSubsRequest createSubsRequest = CreateSubsRequest.builder()
                .name("멤버십 변경")
                .price(35000L)
                .build();

        KakaoPayReadyResponse response = KakaoPayReadyResponse.builder()
                .tid("tid")
                .nextRedirectPcUrl("nextRedirectPcUrl")
                .nextRedirectAppUrl("nextRedirectAppUrl")
                .nextRedirectMobileUrl("nextRedirectMobileUrl")
                .androidAppScheme("androidAppScheme")
                .iosAppScheme("iosAppScheme")
                .createdAt("createdAt")
                .build();

        given(memberShipService.kakaoPayReady(any(KakaoPayReadyRequest.class))).willReturn(response);
        willDoNothing().given(memberShipService).savePayReadyData(any(CreateSubsDto.class));
        //when,then
        mockMvc.perform(post("/membership/kakao-pay/ready")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(createSubsRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("kakaoPaySubs-ready",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .requestFields(
                                        fieldWithPath("price").type(JsonFieldType.NUMBER)
                                                .description("총 금액"),
                                        fieldWithPath("name").type(JsonFieldType.STRING)
                                                .description("주문 내역")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                .description("HTTP 응답"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("메시지"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                                                .description("응답 데이터"),
                                        fieldWithPath("data.tid").type(JsonFieldType.STRING)
                                                .description("tid"),
                                        fieldWithPath("data.next_redirect_pc_url").type(JsonFieldType.STRING)
                                                .description("결제 고유 번호 (20자)"),
                                        fieldWithPath("data.next_redirect_app_url").type(JsonFieldType.STRING)
                                                .description("APP용 인증 리다이렉트 URL"),
                                        fieldWithPath("data.next_redirect_mobile_url").type(JsonFieldType.STRING)
                                                .description("모바일 웹용 인증 리다이렉트 URL"),
                                        fieldWithPath("data.android_app_scheme").type(JsonFieldType.STRING)
                                                .description("카카오페이 결제 화면으로 이동하는 Android 앱 스킴"),
                                        fieldWithPath("data.ios_app_scheme").type(JsonFieldType.STRING)
                                                .description("카카오페이 결제 화면으로 이동하는 iOS 앱 스킴"),
                                        fieldWithPath("data.created_at").type(JsonFieldType.STRING)
                                                .description("결제 준비 요청 시간")
                                ).requestSchema(Schema.schema("PostMembershipKakaoPayReadyReq"))
                                .responseSchema(Schema.schema("PostMembershipKakaoPayReadyReq"))
                                .build()
                        )
                ));
    }


    @Test
    @DisplayName("SID를 받기위한 0회차 결제 승인")
    @WithMockCustomUser
    public void kakaoPaymentApproved() throws Exception {
        //given
        Amount amount = Amount.builder().totalAmount(100L)
                .taxFreeAmount(0L)
                .discountAmount(0L).build();

        CardInfo cardInfo = CardInfo.builder().cardIssuerName("신한")
                .cardPurchaseName("신한")
                .cardInstallMonth("0425").build();

        LocalDateTime approvedAt = LocalDateTime.of(2024, 2, 06, 4, 03);

        KakaoPayApproveResponse response = KakaoPayApproveResponse.builder()
                .aid("aid")
                .tid("tid")
                .cid("cid")
                .sid("sid")
                .partnerOrderId("partnerOrderId")
                .partnerUserId("1")
                .paymentMethodType("paymentMethodType")
                .orderName("orderName")
                .quantity(1L)
                .amount(amount)
                .cardInfo(cardInfo)
                .approvedAt(approvedAt)
                .build();

        given(memberShipService.kakaoPayApprove(anyString(), anyString())).willReturn(response);

        //        willDoNothing().given(memberShipService).saveApprovalResponse(response);
        //        willDoNothing().given(memberShipService).changeMemberShip(Long.parseLong(response.getPartnerUserId()));
        //when,then
        String orderNo = "123456"; // 예시로 사용할 주문 번호를 지정합니다.

        mockMvc.perform(get("/membership/kakao-pay/approval/{orderNo}", orderNo)
                        .param("pg_token", "pgTokenParam")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("kakaoPaySubs-Approved",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .pathParameters(
                                        parameterWithName("orderNo")
                                                .description("주문 번호")
                                )
                                .queryParameters(
                                        parameterWithName("pg_token").description("토큰값")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                .description("HTTP 응답"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("메시지"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                                                .description("응답 데이터"),
                                        fieldWithPath("data.aid").type(JsonFieldType.STRING)
                                                .description("aid"),
                                        fieldWithPath("data.tid").type(JsonFieldType.STRING)
                                                .description("tid"),
                                        fieldWithPath("data.cid").type(JsonFieldType.STRING)
                                                .description("cid"),
                                        fieldWithPath("data.sid").type(JsonFieldType.STRING)
                                                .description("sid"),
                                        fieldWithPath("data.partner_order_id").type(JsonFieldType.STRING)
                                                .description("partnerOrderId"),
                                        fieldWithPath("data.partner_user_id").type(JsonFieldType.STRING)
                                                .description("partnerUserId"),
                                        fieldWithPath("data.payment_method_type").type(JsonFieldType.STRING)
                                                .description("지불 방법"),
                                        fieldWithPath("data.item_name").type(JsonFieldType.STRING)
                                                .description("아이템 이름"),
                                        fieldWithPath("data.quantity").type(JsonFieldType.NUMBER)
                                                .description("quantity"),
                                        fieldWithPath("data.amount").type(JsonFieldType.OBJECT)
                                                .description("가격"),
                                        fieldWithPath("data.amount.total").type(JsonFieldType.NUMBER)
                                                .description("총 가격"),
                                        fieldWithPath("data.amount.discount").type(JsonFieldType.NUMBER)
                                                .description("할인 가격"),
                                        fieldWithPath("data.amount.tax_free").type(JsonFieldType.NUMBER)
                                                .description("면세가"),
                                        fieldWithPath("data.card_info").type(JsonFieldType.OBJECT)
                                                .description("결제 카드 정보"),
                                        fieldWithPath("data.card_info.kakaopay_issuer_corp").type(JsonFieldType.STRING)
                                                .description("발행사"),
                                        fieldWithPath("data.card_info.kakaopay_purchase_corp").type(JsonFieldType.STRING)
                                                .description("매입사"),
                                        fieldWithPath("data.card_info.install_month").type(JsonFieldType.STRING)
                                                .description("년월일"),
                                        fieldWithPath("data.approved_at").type(JsonFieldType.STRING)
                                                .description("승인 날짜")
                                ).requestSchema(Schema.schema("GetMembershipKakaoPayApprovalReq"))
                                .responseSchema(Schema.schema("GetMembershipKakaoPayApprovalRes"))
                                .build()
                        )
                ));
    }
}
