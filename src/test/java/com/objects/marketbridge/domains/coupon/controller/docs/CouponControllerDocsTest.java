package com.objects.marketbridge.domains.coupon.controller.docs;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objects.marketbridge.common.security.annotation.WithMockCustomUser;
import com.objects.marketbridge.domains.coupon.controller.CouponController;
import com.objects.marketbridge.domains.coupon.service.CreateCouponService;
import com.objects.marketbridge.domains.coupon.service.GetCouponService;
import com.objects.marketbridge.domains.coupon.service.dto.GetCouponDto;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@WebMvcTest(CouponController.class)
@ExtendWith(RestDocumentationExtension.class)
public class CouponControllerDocsTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean GetCouponService getCouponService;
    @MockBean CreateCouponService createCouponService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }

    @DisplayName("[API] GET /coupons?productGroupId 테스트")
    @Test
    @WithMockCustomUser
    void findCouponsForProduct() throws Exception {
        // given
        Long productGroupId = 111111L;

        List<GetCouponDto> couponDtos = List.of(GetCouponDto.builder()
                .productGroupId(111111L)
                .couponId(1L)
                .couponName("1000원 할인 쿠폰")
                .price(1000L)
                .count(9999L)
                .minimumPrice(15000L)
                .startDate(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
                .endDate(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                .build());
        given(getCouponService.findCouponsForProductGroup(anyLong())).willReturn(couponDtos);

        // when
        MockHttpServletRequestBuilder requestBuilder =
                get("/coupons")
                        .param("productGroupId", String.valueOf(productGroupId))
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("coupon-get-by-productgroupid",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .queryParameters(
                                                parameterWithName("productGroupId").description("상품 그룹 아이디")
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

                                                fieldWithPath("data.hasCoupons").type(JsonFieldType.BOOLEAN)
                                                        .description("등록된 쿠폰 여부"),

                                                fieldWithPath("data.couponInfos[].couponId").type(JsonFieldType.NUMBER)
                                                        .description("쿠폰 아이디"),
                                                fieldWithPath("data.couponInfos[].couponName").type(JsonFieldType.STRING)
                                                        .description("쿠폰 이름"),
                                                fieldWithPath("data.couponInfos[].couponPrice").type(JsonFieldType.NUMBER)
                                                        .description("쿠폰 가격"),
                                                fieldWithPath("data.couponInfos[].count").type(JsonFieldType.NUMBER)
                                                        .description("총 쿠폰 수량 (판매자가 등록한 총 쿠폰 수량)"),
                                                fieldWithPath("data.couponInfos[].minimumPrice").type(JsonFieldType.NUMBER)
                                                        .description("최소 구매 조건 금액"),
                                                fieldWithPath("data.couponInfos[].startDate").type(JsonFieldType.STRING)
                                                        .description("쿠폰 시작기한 (yyyy-MM-dd HH:mm:ss) "),
                                                fieldWithPath("data.couponInfos[].endDate").type(JsonFieldType.STRING)
                                                        .description("쿠폰 만료기한 (yyyy-MM-dd HH:mm:ss) ")
                                        )
                                        .responseSchema(Schema.schema("GetCouponsProductGroupIdRes"))
                                        .build()
                        )
                ));
    }

    @DisplayName("[API] GET /coupons/{couponId} 테스트")
    @Test
    @WithMockCustomUser
    void findCoupon() throws Exception {
        // given
        Long couponId = 1L;

        GetCouponDto couponDto = GetCouponDto.builder()
                .couponId(couponId)
                .productGroupId(111111L)
                .couponName("1000원 할인 쿠폰")
                .price(1000L)
                .count(9999L)
                .minimumPrice(15000L)
                .startDate(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
                .endDate(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                .build();

        given(getCouponService.find(anyLong())).willReturn(couponDto);

        // when
        ResultActions actions = mockMvc.perform(get("/coupons/{couponId}", couponId)
                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        actions
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("coupon-get-by-couponId",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .pathParameters(
                                                parameterWithName("couponId").description("쿠폰 아이디")
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

                                                fieldWithPath("data.hasCoupons").type(JsonFieldType.BOOLEAN)
                                                        .description("등록된 쿠폰 여부"),

                                                fieldWithPath("data.couponInfos[].couponId").type(JsonFieldType.NUMBER)
                                                        .description("쿠폰 아이디"),
                                                fieldWithPath("data.couponInfos[].couponName").type(JsonFieldType.STRING)
                                                        .description("쿠폰 이름"),
                                                fieldWithPath("data.couponInfos[].couponPrice").type(JsonFieldType.NUMBER)
                                                        .description("쿠폰 가격"),
                                                fieldWithPath("data.couponInfos[].count").type(JsonFieldType.NUMBER)
                                                        .description("총 쿠폰 수량 (판매자가 등록한 총 쿠폰 수량)"),
                                                fieldWithPath("data.couponInfos[].minimumPrice").type(JsonFieldType.NUMBER)
                                                        .description("최소 구매 조건 금액"),
                                                fieldWithPath("data.couponInfos[].startDate").type(JsonFieldType.STRING)
                                                        .description("쿠폰 시작기한 (yyyy-MM-dd HH:mm:ss) "),
                                                fieldWithPath("data.couponInfos[].endDate").type(JsonFieldType.STRING)
                                                        .description("쿠폰 만료기한 (yyyy-MM-dd HH:mm:ss) ")
                                        )
                                        .responseSchema(Schema.schema("GetCouponsCouponIdRes"))
                                        .build()
                        )
                ));
    }
}
