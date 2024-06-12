package com.objects.marketbridge.domains.order.controller.docs;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objects.marketbridge.common.kakao.KakaoPayConfig;
import com.objects.marketbridge.common.kakao.KakaoPayService;
import com.objects.marketbridge.common.kakao.dto.KakaoPayReadyResponse;
import com.objects.marketbridge.common.kakao.enums.CardCoType;
import com.objects.marketbridge.common.responseobj.PageResponse;
import com.objects.marketbridge.common.security.annotation.WithMockCustomUser;
import com.objects.marketbridge.domains.member.domain.AddressValue;
import com.objects.marketbridge.domains.order.controller.OrderController;
import com.objects.marketbridge.domains.order.controller.dto.CreateOrderHttp;
import com.objects.marketbridge.domains.order.controller.dto.select.GetOrderDetailHttp;
import com.objects.marketbridge.domains.order.controller.dto.select.GetOrderHttp;
import com.objects.marketbridge.domains.order.controller.dto.select.OrderDetailInfo;
import com.objects.marketbridge.domains.order.controller.dto.select.PaymentInfo;
import com.objects.marketbridge.domains.order.domain.StatusCodeType;
import com.objects.marketbridge.domains.order.service.CreateOrderService;
import com.objects.marketbridge.domains.order.service.GetOrderService;
import com.objects.marketbridge.domains.order.service.dto.CreateOrderDto;
import com.objects.marketbridge.domains.order.service.port.OrderDtoRepository;
import com.objects.marketbridge.domains.payment.domain.PaymentType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.objects.marketbridge.domains.order.controller.dto.select.GetOrderHttp.Condition;
import static org.mockito.ArgumentMatchers.*;
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

@Slf4j
@ActiveProfiles("test")
@WebMvcTest(OrderController.class)
@ExtendWith(RestDocumentationExtension.class)
public class OrderControllerRestDocsTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean CreateOrderService createOrderService;
    @MockBean KakaoPayConfig kakaoPayConfig;
    @MockBean KakaoPayService kakaoPayService;
    @MockBean GetOrderService getOrderService;
    @MockBean OrderDtoRepository orderDtoRepository;


    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }

    @DisplayName("[API] POST/orders")
    @Test
    @WithMockCustomUser
    void createOrder() throws Exception {

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
        willDoNothing().given(createOrderService).create(any(CreateOrderDto.class));

        // when
        MockHttpServletRequestBuilder requestBuilder =
                post("/orders")
                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                .content(objectMapper.writeValueAsString(createOrderRequest))
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("order-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .requestFields(
                                                fieldWithPath("totalAmount").type(JsonFieldType.NUMBER)
                                                        .description("총 주문 금액 (쿠폰 할인 적용 전)"),
                                                fieldWithPath("totalDiscountAmount").type(JsonFieldType.NUMBER)
                                                        .description("할인 금액"),
                                                fieldWithPath("realAmount").type(JsonFieldType.NUMBER)
                                                        .description("실 결제 금액 (할인 적용 후)"),
                                                fieldWithPath("addressId").type(JsonFieldType.NUMBER)
                                                        .description("배송지 ID"),
                                                fieldWithPath("orderName").type(JsonFieldType.STRING)
                                                        .description("주문 이름"),
                                                fieldWithPath("productValues").type(JsonFieldType.ARRAY)
                                                        .description("주문 상품 정보"),

                                                fieldWithPath("productValues[].productId").type(JsonFieldType.NUMBER)
                                                        .description("상품 아이디"),
                                                fieldWithPath("productValues[].price").type(JsonFieldType.NUMBER)
                                                        .description("상품 가격"),
                                                fieldWithPath("productValues[].quantity").type(JsonFieldType.NUMBER)
                                                        .description("상품 수량"),
                                                fieldWithPath("productValues[].hasCouponUsed").type(JsonFieldType.BOOLEAN)
                                                        .description("쿠폰 사용 여부"),
                                                fieldWithPath("productValues[].couponId").type(JsonFieldType.NUMBER)
                                                        .description("사용한 쿠폰 아이디 (사용 안했으면 null 보내야함)"),
                                                fieldWithPath("productValues[].couponPrice").type(JsonFieldType.NUMBER)
                                                        .description("쿠폰 가격 (사용 안했으면 null 보내야함)").optional(),
                                                fieldWithPath("productValues[].couponMinimumPrice").type(JsonFieldType.NUMBER)
                                                        .description("쿠폰 최소 주문 금액 (사용 안했으면 null 보내야함)"),
                                                fieldWithPath("productValues[].couponEndDate").type(JsonFieldType.STRING)
                                                        .description("쿠폰 유효기간 (yyyy-MM-ddTHH:mm:ss) (사용 안했으면 null 보내야함)"),
                                                fieldWithPath("productValues[].deliveredDate").type(JsonFieldType.STRING)
                                                        .description("예상 배송 날짜 (yyyy-MM-dd HH:mm:ss)")
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
                                                fieldWithPath("data.nextRedirectPcUrl").type(JsonFieldType.STRING)
                                                        .description("결제 고유 번호 (20자)"),
                                                fieldWithPath("data.nextRedirectAppUrl").type(JsonFieldType.STRING)
                                                        .description("APP용 인증 리다이렉트 URL"),
                                                fieldWithPath("data.nextRedirectMobileUrl").type(JsonFieldType.STRING)
                                                        .description("모바일 웹용 인증 리다이렉트 URL"),
                                                fieldWithPath("data.androidAppScheme").type(JsonFieldType.STRING)
                                                        .description("카카오페이 결제 화면으로 이동하는 Android 앱 스킴"),
                                                fieldWithPath("data.iosAppScheme").type(JsonFieldType.STRING)
                                                        .description("카카오페이 결제 화면으로 이동하는 iOS 앱 스킴"),
                                                fieldWithPath("data.createdAt").type(JsonFieldType.STRING)
                                                        .description("결제 준비 요청 시간 (주문생성시간X)")
                                        )
                                        .requestSchema(Schema.schema("PostOrdersReq"))
                                        .responseSchema(Schema.schema("PostOrdersRes"))
                                        .build()
                        )
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


    @DisplayName("전체 주문들을 조회 할 수 있다")
    @Test
    @WithMockCustomUser
    void getOrders() throws Exception {

        // given
        int pageNumber = 0;
        int pageSize = 1;
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        OrderDetailInfo orderDetailInfo = createOrderDetailInfo();
        GetOrderHttp.Response response = createResponse(orderDetailInfo);

        given(getOrderService.search(any(Pageable.class), any(Condition.class))).willReturn(new PageResponse<>(new PageImpl<>(List.of(response), pageRequest, 1L)));

        // when
        MockHttpServletRequestBuilder requestBuilder = get("/orders")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt,DESC")
                .param("year", "2024")
                .param("keyword", "자전거")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken");

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("order-list",
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .queryParameters(
                                                parameterWithName("page").description("페이지 번호"),
                                                parameterWithName("size").description("페이지 사이즈").optional(),
                                                parameterWithName("sort").description("정렬기준,정렬순서 ex) createdAt,DESC").optional(),
                                                parameterWithName("year").description("연도").optional(),
                                                parameterWithName("keyword").description("검색 키워드").optional()
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

                                                fieldWithPath("data.content[].orderNo").type(JsonFieldType.STRING)
                                                        .description("주문 번호"),
                                                fieldWithPath("data.content[].orderId").type(JsonFieldType.NUMBER)
                                                        .description("주문 아이디"),
                                                fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING)
                                                        .description("주문 생성 일자"),

                                                fieldWithPath("data.content[].orderDetailInfos[]").type(JsonFieldType.ARRAY)
                                                        .description("상세 주문 리스트"),
                                                fieldWithPath("data.content[].orderDetailInfos[].orderNo").type(JsonFieldType.STRING)
                                                        .description("부모 주문 번호"),
                                                fieldWithPath("data.content[].orderDetailInfos[].orderDetailId").type(JsonFieldType.NUMBER)
                                                        .description("상세 주문 아이디(PK)"),
                                                fieldWithPath("data.content[].orderDetailInfos[].productId").type(JsonFieldType.NUMBER)
                                                        .description("상품 아이디(PK)"),
                                                fieldWithPath("data.content[].orderDetailInfos[].quantity").type(JsonFieldType.NUMBER)
                                                        .description("상품 수량"),
                                                fieldWithPath("data.content[].orderDetailInfos[].price").type(JsonFieldType.NUMBER)
                                                        .description("(1개당) 상품 가격"),
                                                fieldWithPath("data.content[].orderDetailInfos[].statusCode").type(JsonFieldType.STRING)
                                                        .description("주문 상태"),
                                                fieldWithPath("data.content[].orderDetailInfos[].deliveredDate").type(JsonFieldType.STRING)
                                                        .description("배송 일자"),
                                                fieldWithPath("data.content[].orderDetailInfos[].productThumbImageUrl").type(JsonFieldType.STRING)
                                                        .description("상품 썸네일 이미지 URL"),
                                                fieldWithPath("data.content[].orderDetailInfos[].productName").type(JsonFieldType.STRING)
                                                        .description("상품 이름"),
                                                fieldWithPath("data.content[].orderDetailInfos[].optionNames[]").type(JsonFieldType.ARRAY)
                                                        .description("상품 옵션 리스트"),
                                                fieldWithPath("data.content[].orderDetailInfos[].isOwn").type(JsonFieldType.BOOLEAN)
                                                        .description("마켓브릿지 상품인지 입점 판매자 상품인지 구분하는 값"),

                                                fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN)
                                                        .description("정렬이 되었는지 안되었는지 판별 하는 값"),
                                                fieldWithPath("data.sort.direction").type(JsonFieldType.STRING)
                                                        .description("정렬 순서"),
                                                fieldWithPath("data.sort.orderProperty").type(JsonFieldType.STRING)
                                                        .description("정렬 기준"),

                                                fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER)
                                                        .description("현재 페이지 (0 부터 시작)"),
                                                fieldWithPath("data.size").type(JsonFieldType.NUMBER)
                                                        .description("페이지 사이즈"),
                                                fieldWithPath("data.first").type(JsonFieldType.BOOLEAN)
                                                        .description("현재 페이지가 첫 페이지인지"),
                                                fieldWithPath("data.last").type(JsonFieldType.BOOLEAN)
                                                        .description("현재 페이지가 마지막 페이지인지"),
                                                fieldWithPath("data.totalElement").type(JsonFieldType.NUMBER)
                                                        .description("총 데이터 수"),
                                                fieldWithPath("data.totalPage").type(JsonFieldType.NUMBER)
                                                        .description("총 페이지 수")
                                        )
                                        .responseSchema(Schema.schema("GetOrdersRes"))
                                        .build()
                        )

                ));
    }

    private GetOrderHttp.Response createResponse(OrderDetailInfo orderDetailInfo) {
        return GetOrderHttp.Response.builder()
                .orderId(1L)
                .orderNo("orderNo")
                .createdAt("2023-12-31 12:00:00")
                .orderDetailInfos(List.of(orderDetailInfo))
                .build();
    }

    @DisplayName("상세 주문을 조회할 수 있다")
    @Test
    @WithMockCustomUser
    void getOrderDetails() throws Exception {

        //given
        GetOrderDetailHttp.Response response = GetOrderDetailHttp.Response.builder()
                .orderNo("orderNo")
                .createdAt("2024-01-01 12:00:00")
                .orderDetailInfos(List.of(createOrderDetailInfo()))
                .addressValue(createAddressValue())
                .paymentInfo(createPaymentInfo())
                .build();

        given(getOrderService.getOrderDetails(anyLong())).willReturn(response);

        //when,
        MockHttpServletRequestBuilder requestBuilder = get("/orders/{orderId}", "1")
                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                .accept(MediaType.APPLICATION_JSON);
        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("order-detail-list",
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .pathParameters(
                                                parameterWithName("orderId").description("주문 아이디")
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


                                                fieldWithPath("data.createdAt").type(JsonFieldType.STRING)
                                                        .description("주문 일자 (yyyy-MM-dd HH:mm:ss)"),
                                                fieldWithPath("data.orderNo").type(JsonFieldType.STRING)
                                                        .description("주문 번호"),

                                                fieldWithPath("data.orderDetailInfos[]").type(JsonFieldType.ARRAY)
                                                        .description("주문 상세 정보"),
                                                fieldWithPath("data.orderDetailInfos[].orderNo").type(JsonFieldType.STRING)
                                                        .description("주문 번호"),
                                                fieldWithPath("data.orderDetailInfos[].orderDetailId").type(JsonFieldType.NUMBER)
                                                        .description("주문 상세 아이디(PK)"),
                                                fieldWithPath("data.orderDetailInfos[].productId").type(JsonFieldType.NUMBER)
                                                        .description("상품 아이디(PK)"),
                                                fieldWithPath("data.orderDetailInfos[].quantity").type(JsonFieldType.NUMBER)
                                                        .description("상품 수량"),
                                                fieldWithPath("data.orderDetailInfos[].price").type(JsonFieldType.NUMBER)
                                                        .description("상품 가격"),
                                                fieldWithPath("data.orderDetailInfos[].statusCode").type(JsonFieldType.STRING)
                                                        .description("주문 상태"),
                                                fieldWithPath("data.orderDetailInfos[].deliveredDate").type(JsonFieldType.STRING)
                                                        .description("배송 도착 일자 (yyyy-MM-dd)"),
                                                fieldWithPath("data.orderDetailInfos[].productThumbImageUrl").type(JsonFieldType.STRING)
                                                        .description("상품 썸네일 URL"),
                                                fieldWithPath("data.orderDetailInfos[].productName").type(JsonFieldType.STRING)
                                                        .description("상품 이름"),
                                                fieldWithPath("data.orderDetailInfos[].optionNames[]").type(JsonFieldType.ARRAY)
                                                        .description("상품 옵션 리스트"),
                                                fieldWithPath("data.orderDetailInfos[].isOwn").type(JsonFieldType.BOOLEAN)
                                                        .description("마켓브릿지 상품인지, 입점판매자 상품인지 구분하는 값"),

                                                fieldWithPath("data.addressValue").type(JsonFieldType.OBJECT)
                                                        .description("배송지 정보"),
                                                fieldWithPath("data.addressValue.phoneNo").type(JsonFieldType.STRING)
                                                        .description("구매자 전화번호"),
                                                fieldWithPath("data.addressValue.name").type(JsonFieldType.STRING)
                                                        .description("구매자 이름"),
                                                fieldWithPath("data.addressValue.city").type(JsonFieldType.STRING)
                                                        .description("도시명"),
                                                fieldWithPath("data.addressValue.street").type(JsonFieldType.STRING)
                                                        .description("도로명주소"),
                                                fieldWithPath("data.addressValue.zipcode").type(JsonFieldType.STRING)
                                                        .description("우편번호"),
                                                fieldWithPath("data.addressValue.detail").type(JsonFieldType.STRING)
                                                        .description("상세주소"),
                                                fieldWithPath("data.addressValue.alias").type(JsonFieldType.STRING)
                                                        .description("배송지 별칭"),

                                                fieldWithPath("data.paymentInfo").type(JsonFieldType.OBJECT)
                                                        .description("결제 정보"),
                                                fieldWithPath("data.paymentInfo.paymentMethod").type(JsonFieldType.STRING)
                                                        .description("결제 수단"),
                                                fieldWithPath("data.paymentInfo.cardIssuerName").type(JsonFieldType.STRING)
                                                        .description("카드사 (현금 결제일 경우 null)"),
                                                fieldWithPath("data.paymentInfo.totalAmount").type(JsonFieldType.NUMBER)
                                                        .description("총 주문 금액 (할인 미적용 금액)"),
                                                fieldWithPath("data.paymentInfo.discountAmount").type(JsonFieldType.NUMBER)
                                                        .description("총 할인 금액"),
                                                fieldWithPath("data.paymentInfo.deliveryFee").type(JsonFieldType.NUMBER)
                                                        .description("배송비")
                                        )
                                        .responseSchema(Schema.schema("GetOrdersOrderIdRes"))
                                        .build()
                        )
                ));
    }

    private PaymentInfo createPaymentInfo() {
        return PaymentInfo.builder()
                .paymentMethod(PaymentType.CARD.toString())
                .cardIssuerName(CardCoType.KAKAOBANK.toString())
                .totalAmount(10000L)
                .discountAmount(500L)
                .deliveryFee(0L)
                .build();
    }

    private AddressValue createAddressValue() {
        return AddressValue.builder()
                .phoneNo("01012345678")
                .name("홍길동")
                .city("서울")
                .street("세종대로 234-1")
                .zipcode("09909")
                .detail("민들레 아파트 110동 3443호")
                .alias("우리집")
                .build();
    }

    private OrderDetailInfo createOrderDetailInfo() {
        return OrderDetailInfo.builder()
                .orderNo("orderNo")
                .orderDetailId(1L)
                .productId(1L)
                .quantity(1L)
                .price(10000L)
                .statusCode(StatusCodeType.PAYMENT_COMPLETED.getCode())
                .deliveredDate("2024.01.01 HH:mm:ss")
                .productThumbImageUrl("thumbImageUrl")
                .productName("티셔츠")
                .optionNames(List.of("빨강", "XL"))
                .isOwn(true)
                .build();
    }
}
