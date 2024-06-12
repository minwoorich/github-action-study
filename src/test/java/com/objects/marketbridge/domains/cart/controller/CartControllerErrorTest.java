package com.objects.marketbridge.domains.cart.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.security.annotation.WithMockCustomUser;
import com.objects.marketbridge.domains.cart.controller.dto.CreateCartHttp;
import com.objects.marketbridge.domains.cart.controller.dto.UpdateCartHttp;
import com.objects.marketbridge.domains.cart.service.AddToCartService;
import com.objects.marketbridge.domains.cart.service.DeleteCartService;
import com.objects.marketbridge.domains.cart.service.GetCartListService;
import com.objects.marketbridge.domains.cart.service.UpdateCartService;
import com.objects.marketbridge.domains.cart.service.dto.CreateCartDto;
import com.objects.marketbridge.domains.cart.service.dto.UpdateCartDto;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@WebMvcTest(CartController.class)
@ExtendWith(RestDocumentationExtension.class)
class CartControllerErrorTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean AddToCartService addToCartService;
    @MockBean GetCartListService getCartListService;
    @MockBean UpdateCartService updateCartService;
    @MockBean DeleteCartService deleteCartService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentationContextProvider) {

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();
    }

    @DisplayName("[POST/carts] 상품을 중복해서 장바구니에 담을경우")
    @Test
    @WithMockCustomUser
    void addToCart1() throws Exception {
        // given
        CreateCartHttp.Request request = CreateCartHttp.Request.create(1L, 1L, false);

        doThrow(CustomLogicException.createBadRequestError(DUPLICATE_OPERATION, "이미 장바구니에 담긴 상품입니다", LocalDateTime.now()))
                .when(addToCartService).add(any(CreateCartDto.class));

        // when
        MockHttpServletRequestBuilder requestBuilder =
                post("/carts")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("cart-add-duplicated-error",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @DisplayName("[POST/carts] 품절 된 상품을 장바구니에 담으려 할 경우")
    @Test
    @WithMockCustomUser
    void addToCart2() throws Exception {
        // given
        CreateCartHttp.Request request = CreateCartHttp.Request.create(1L, 1L, false);

        doThrow(CustomLogicException.createBadRequestError(OUT_OF_STOCK))
                .when(addToCartService).add(any(CreateCartDto.class));

        // when
        MockHttpServletRequestBuilder requestBuilder =
                post("/carts")
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("cart-add-outofstock-error",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @DisplayName("[PATCH/carts{cartId}] 리소스를 찾을수 없다")
    @Test
    @WithMockCustomUser
    void update1() throws Exception {
        // given
        UpdateCartHttp.Request request = UpdateCartHttp.Request.builder().quantity(10L).build();

        doThrow(CustomLogicException.createBadRequestError(RESOUCRE_NOT_FOUND))
                .when(updateCartService).update(any(UpdateCartDto.class));
        String cartId = "1";
        // when
        MockHttpServletRequestBuilder requestBuilder =
                patch("/carts/{cartId}", Long.parseLong(cartId))
                        .header(HttpHeaders.AUTHORIZATION, "bearer AccessToken")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("cart-update-resource-notfound-error",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }
}