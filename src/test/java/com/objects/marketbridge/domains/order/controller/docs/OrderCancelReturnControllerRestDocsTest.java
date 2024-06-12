package com.objects.marketbridge.domains.order.controller.docs;


import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objects.marketbridge.common.security.annotation.WithMockCustomUser;
import com.objects.marketbridge.domains.order.controller.OrderCancelReturnController;
import com.objects.marketbridge.domains.order.infra.dtio.GetCancelReturnListDtio;
import com.objects.marketbridge.domains.order.service.port.OrderDetailDtoRepository;
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
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.objects.marketbridge.domains.order.domain.StatusCodeType.ORDER_CANCEL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@WebMvcTest(OrderCancelReturnController.class)
@ExtendWith(RestDocumentationExtension.class)
public class OrderCancelReturnControllerRestDocsTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OrderDetailDtoRepository orderDetailDtoRepository;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }

    @Test
    @DisplayName("주문 취소/반품 리스트 반환 API")
    @WithMockCustomUser
    public void getCancelReturnList() throws Exception {
        // given
        LocalDateTime orderDate1 = LocalDateTime.of(2024, 2, 8, 11, 39);
        LocalDateTime cancelDate1 = LocalDateTime.of(2024, 2, 8, 11, 40);
        GetCancelReturnListDtio.Response content1 = GetCancelReturnListDtio.Response.builder()
                .orderDate(orderDate1)
                .cancelReceiptDate(cancelDate1)
                .orderDetailInfo(
                        GetCancelReturnListDtio.OrderDetailInfo.builder()
                                .orderNo("1")
                                .productId(1L)
                                .productNo("1")
                                .name("빵빵이키링")
                                .price(1000L)
                                .quantity(2L)
                                .orderStatus(ORDER_CANCEL.getCode())
                                .build()
                )
                .build();

        LocalDateTime orderDate2 = LocalDateTime.of(2024, 2, 8, 11, 39);
        LocalDateTime cancelDate2 = LocalDateTime.of(2024, 2, 8, 11, 40);
        GetCancelReturnListDtio.Response content2 = GetCancelReturnListDtio.Response.builder()
                .orderDate(orderDate2)
                .cancelReceiptDate(cancelDate2)
                .orderDetailInfo(
                        GetCancelReturnListDtio.OrderDetailInfo.builder()
                                .orderNo("1")
                                .productId(2L)
                                .productNo("2")
                                .name("옥지얌키링")
                                .price(2000L)
                                .quantity(4L)
                                .orderStatus(ORDER_CANCEL.getCode())
                                .build()
                )
                .build();

        List<GetCancelReturnListDtio.Response> contents = List.of(content1, content2);

        PageRequest pageRequest = PageRequest.of(0, 5);
        given(orderDetailDtoRepository.findCancelReturnListDtio(anyLong(), any(PageRequest.class)))
                .willReturn(new PageImpl<>(contents, pageRequest, 2));

        // when // then
        mockMvc.perform(
                        get("/orders/cancel-return/list")
                                .accept(MediaType.APPLICATION_JSON)
                                .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                                .param("page", "0")
                                .param("size", "5")

                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("order-cancel-return-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .queryParameters(
                                                parameterWithName("page")
                                                        .description("페이지 번호"),
                                                parameterWithName("size")
                                                        .description("사이즈 크기")
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
                                                fieldWithPath("data.content[].cancelReceiptDate").type(JsonFieldType.STRING)
                                                        .description("주문 취소 날짜"),
                                                fieldWithPath("data.content[].orderDate").type(JsonFieldType.STRING)
                                                        .description("주문 날짜"),
                                                fieldWithPath("data.content[].orderDetailInfo.orderNo").type(JsonFieldType.STRING)
                                                        .description("주문 번호"),
                                                fieldWithPath("data.content[].orderDetailInfo.productId").type(JsonFieldType.NUMBER)
                                                        .description("상품 Id"),
                                                fieldWithPath("data.content[].orderDetailInfo.productNo").type(JsonFieldType.STRING)
                                                        .description("상품 번호"),
                                                fieldWithPath("data.content[].orderDetailInfo.name").type(JsonFieldType.STRING)
                                                        .description("상품 이름"),
                                                fieldWithPath("data.content[].orderDetailInfo.price").type(JsonFieldType.NUMBER)
                                                        .description("상품 가격"),
                                                fieldWithPath("data.content[].orderDetailInfo.quantity").type(JsonFieldType.NUMBER)
                                                        .description("상품 주문 수량"),
                                                fieldWithPath("data.content[].orderDetailInfo.orderStatus").type(JsonFieldType.STRING)
                                                        .description("주문 상태"),
                                                fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER)
                                                        .description("페이지 번호"),
                                                fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER)
                                                        .description("페이지당 요소 수"),
                                                fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                                                        .description("정렬이 비어 있는지 여부"),
                                                fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN)
                                                        .description("정렬이 되어 있는지 여부"),
                                                fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                                        .description("정렬이 되어 있지 않은지 여부"),
                                                fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER)
                                                        .description("페이지 오프셋"),
                                                fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN)
                                                        .description("페이지가 있는지 여부"),
                                                fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN)
                                                        .description("페이지가 없는지 여부"),
                                                fieldWithPath("data.last").type(JsonFieldType.BOOLEAN)
                                                        .description("마지막 페이지 여부"),
                                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER)
                                                        .description("전체 페이지 수"),
                                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER)
                                                        .description("전체 요소 수"),
                                                fieldWithPath("data.size").type(JsonFieldType.NUMBER)
                                                        .description("페이지 크기"),
                                                fieldWithPath("data.number").type(JsonFieldType.NUMBER)
                                                        .description("페이지 번호"),
                                                fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN)
                                                        .description("정렬이 비어 있는지 여부"),
                                                fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN)
                                                        .description("정렬이 되어 있는지 여부"),
                                                fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN)
                                                        .description("정렬이 되어 있지 않은지 여부"),
                                                fieldWithPath("data.first").type(JsonFieldType.BOOLEAN)
                                                        .description("첫 페이지 여부"),
                                                fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER)
                                                        .description("현재 페이지의 요소 수"),
                                                fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN)
                                                        .description("컨텐츠가 비어 있는지 여부")
                                        )
                                        .responseSchema(Schema.schema("GetOrdersCancelReturnListRes"))
                                        .build()
                        )
                ));
    }

}
