package com.objects.marketbridge.domains.cart.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objects.marketbridge.common.responseobj.SliceResponse;
import com.objects.marketbridge.common.security.annotation.WithMockCustomUser;
import com.objects.marketbridge.domains.cart.controller.dto.CreateCartHttp;
import com.objects.marketbridge.domains.cart.controller.dto.DeleteCartHttp;
import com.objects.marketbridge.domains.cart.controller.dto.UpdateCartHttp;
import com.objects.marketbridge.domains.cart.domain.Cart;
import com.objects.marketbridge.domains.cart.service.AddToCartService;
import com.objects.marketbridge.domains.cart.service.DeleteCartService;
import com.objects.marketbridge.domains.cart.service.GetCartListService;
import com.objects.marketbridge.domains.cart.service.UpdateCartService;
import com.objects.marketbridge.domains.cart.service.dto.GetCartDto;
import com.objects.marketbridge.domains.cart.service.dto.UpdateCartDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@WebMvcTest(CartController.class)
@ExtendWith(RestDocumentationExtension.class)
class CartControllerSuccessTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean AddToCartService addToCartService;
    @MockBean GetCartListService getCartListService;
    @MockBean UpdateCartService updateCartService;
    @MockBean DeleteCartService deleteCartService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }

    @DisplayName("[API] POST/carts 테스트")
    @Test
    @WithMockCustomUser
    void addToCart() throws Exception {
        // given
        CreateCartHttp.Request request = CreateCartHttp.Request.create(1L, 1L, false);
        given(addToCartService.add(request.toDto(anyLong()))).willReturn(any(Cart.class));

        // when
        MockHttpServletRequestBuilder requestBuilder =
                post("/carts")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("cart-add",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .requestFields(
                                                fieldWithPath("productId").type(JsonFieldType.NUMBER)
                                                        .description("상품 아이디"),
                                                fieldWithPath("quantity").type(JsonFieldType.NUMBER)
                                                        .description("상품 수량"),
                                                fieldWithPath("isSubs").type(JsonFieldType.BOOLEAN)
                                                        .description("정기 구독 상품인지 아닌지 구분 하는 값")
                                        )
                                        .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("응답 코드"),
                                        fieldWithPath("status").type(JsonFieldType.STRING)
                                                .description("HTTP 응답"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL)
                                                .description("응답 데이터")
                                        )
                                        .requestSchema(Schema.schema("PostCartsReq"))
                                        .responseSchema(Schema.schema("PostCartsRes"))
                                        .build()
                        )

                ));
    }

    @DisplayName("[API] GET/carts 테스트")
    @Test
    @WithMockCustomUser
    void getCartItems() throws Exception {

        //given
        int pageNumber = 0;
        int pageSize = 1;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        SliceResponse<GetCartDto> sliceResponse = new SliceResponse<>(new SliceImpl<>(createDto(), pageRequest, true));

        given(getCartListService.get(any(Pageable.class), anyLong())).willReturn(sliceResponse);

        //when
        MockHttpServletRequestBuilder requestBuilder =
                get("/carts")
                        .param("page", "0")
                        .param("size", "1")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .accept(MediaType.APPLICATION_JSON);

        //then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("cart-list",
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .queryParameters(
                                                parameterWithName("page").description("페이지 번호"),
                                                parameterWithName("size").description("페이지 사이즈").optional()
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

                                                fieldWithPath("data.content[].cartId").type(JsonFieldType.NUMBER)
                                                        .description("장바구니 아이디"),
                                                fieldWithPath("data.content[].productId").type(JsonFieldType.NUMBER)
                                                        .description("상품 아이디"),
                                                fieldWithPath("data.content[].productNo").type(JsonFieldType.STRING)
                                                        .description("상품 고유 번호"),
                                                fieldWithPath("data.content[].productName").type(JsonFieldType.STRING)
                                                        .description("상품 이름"),
                                                fieldWithPath("data.content[].productPrice").type(JsonFieldType.NUMBER)
                                                        .description("상품 가격 (할인 전)"),
                                                fieldWithPath("data.content[].quantity").type(JsonFieldType.NUMBER)
                                                        .description("상품 수량"),
                                                fieldWithPath("data.content[].discountRate").type(JsonFieldType.NUMBER)
                                                        .description("상품 할인율 (쿠폰할인 제외)"),
                                                fieldWithPath("data.content[].thumbImageUrl").type(JsonFieldType.STRING)
                                                        .description("상품 썸네일 이미지"),
                                                fieldWithPath("data.content[].isOwn").type(JsonFieldType.BOOLEAN)
                                                        .description("마켓브릿지 상품인지 입점 판매자 상품인지 판별하는 값"),
                                                fieldWithPath("data.content[].isSubs").type(JsonFieldType.BOOLEAN)
                                                        .description("정기 구독 상품인지 판별하는 값"),
                                                fieldWithPath("data.content[].stock").type(JsonFieldType.NUMBER)
                                                        .description("상품 재고"),
                                                fieldWithPath("data.content[].deliveryFee").type(JsonFieldType.NUMBER)
                                                        .description("배송비"),
                                                fieldWithPath("data.content[].deliveredDate").type(JsonFieldType.STRING)
                                                        .description("예상 도착 일자(yyyy-MM-dd)"),
                                                fieldWithPath("data.content[].optionNames[]").type(JsonFieldType.ARRAY)
                                                        .description("선택한 옵션 리스트"),

                                                fieldWithPath("data.sort").type(JsonFieldType.OBJECT)
                                                        .description("정렬"),
                                                fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN)
                                                        .description("정렬 되었는지 안 되었는지 판별하는 값"),
                                                fieldWithPath("data.sort.direction").type(JsonFieldType.STRING)
                                                        .description("정렬 순서 (DESC, ASC)"),
                                                fieldWithPath("data.sort.orderProperty").type(JsonFieldType.STRING)
                                                        .description("정렬 기준"),

                                                fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER)
                                                        .description("현재 페이지"),
                                                fieldWithPath("data.size").type(JsonFieldType.NUMBER)
                                                        .description("페이지 사이즈"),
                                                fieldWithPath("data.first").type(JsonFieldType.BOOLEAN)
                                                        .description("첫 페이지 인지 판별 하는 값"),
                                                fieldWithPath("data.last").type(JsonFieldType.BOOLEAN)
                                                        .description("마지막 페이지 인지 판별 하는 값")
                                        )
                                        .responseSchema(Schema.schema("GetCartsRes"))
                                        .build()
                        )
                ));
    }

    private List<GetCartDto> createDto() {
        return List.of(GetCartDto.builder()
                .cartId(1L)
                .productId(1L)
                .productNo("111111111 - 111111111")
                .productName("티셔츠")
                .productPrice(20000L)
                .quantity(1L)
                .discountRate(5L)
                .thumbImageUrl("thumbImageUrl")
                .isOwn(true)
                .isSubs(false)
                .stock(9999L)
                .deliveryFee(0L)
                .deliveredDate("2024.09.09")
                .optionNames(List.of("빨강", "XL"))
                .build());
    }

    @DisplayName("[API] GET/carts/count 테스트")
    @Test
    @WithMockCustomUser
    void countCartItems() throws Exception {
        //given
        given(getCartListService.countAll(anyLong())).willReturn(1L);

        //when
        MockHttpServletRequestBuilder requestBuilder =
                get("/carts/count")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .accept(MediaType.APPLICATION_JSON);

        //then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("cart-count",
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                        .description("응답 코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                        .description("HTTP 응답"),
                                                fieldWithPath("message").type(JsonFieldType.STRING)
                                                        .description("메시지"),
                                                fieldWithPath("data").type(JsonFieldType.NUMBER)
                                                        .description("응답 데이터")
                                        )
                                        .responseSchema(Schema.schema("GetCartsCountRes"))
                                        .build()
                        )

                ));
    }

    @DisplayName("[API] PATCH/carts/{cartId} 테스트")
    @Test
    @WithMockCustomUser
    void updateCartItems() throws Exception {
        //given
        String cartId = "1";
        UpdateCartHttp.Request request = UpdateCartHttp.Request.builder().quantity(1L).build();
        willDoNothing().given(updateCartService).update(any(UpdateCartDto.class));

        //when
        MockHttpServletRequestBuilder requestBuilder =
                patch("/carts/{cartId}", Long.parseLong(cartId))
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);

        //then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("cart-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .pathParameters(
                                                parameterWithName("cartId").description("장바구니 아이디")
                                        )
                                        .requestFields(
                                                fieldWithPath("quantity").type(JsonFieldType.NUMBER)
                                                        .description("장바구니 수량")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                        .description("응답 코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                        .description("HTTP 응답"),
                                                fieldWithPath("message").type(JsonFieldType.STRING)
                                                        .description("메시지"),
                                                fieldWithPath("data").type(JsonFieldType.STRING)
                                                        .description("응답 데이터")
                                        )
                                        .requestSchema(Schema.schema("PatchCartsReq"))
                                        .responseSchema(Schema.schema("PatchCartsRes"))
                                        .build()
                        )

                ));
    }

    @DisplayName("[API] DELETE /carts 테스트")
    @Test
    @WithMockCustomUser
    void deleteCartItems() throws Exception {
        //given
        DeleteCartHttp.Request request = DeleteCartHttp.Request.builder().selectedCartIds(List.of(1L)).build();
        willDoNothing().given(deleteCartService).delete(any(List.class));

        //when
        MockHttpServletRequestBuilder requestBuilder =
                delete("/carts")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON);

        //then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("cart-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .requestFields(
                                                fieldWithPath("selectedCartIds").type(JsonFieldType.ARRAY)
                                                        .description("선택한 장바구니 아이디 리스트")
                                        )
                                        .responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                        .description("응답 코드"),
                                                fieldWithPath("status").type(JsonFieldType.STRING)
                                                        .description("HTTP 응답"),
                                                fieldWithPath("message").type(JsonFieldType.STRING)
                                                        .description("메시지"),
                                                fieldWithPath("data").type(JsonFieldType.STRING)
                                                        .description("응답 데이터")
                                        )
                                        .requestSchema(Schema.schema("DeleteCartsReq"))
                                        .responseSchema(Schema.schema("DeleteCartsRes"))
                                        .build()
                        )
                ));
    }
}