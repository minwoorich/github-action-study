package com.objects.marketbridge.domains.cart.controller;

import com.objects.marketbridge.domains.cart.controller.dto.CreateCartHttp;
import com.objects.marketbridge.domains.cart.controller.dto.DeleteCartHttp;
import com.objects.marketbridge.domains.cart.controller.dto.UpdateCartHttp;
import com.objects.marketbridge.domains.cart.service.AddToCartService;
import com.objects.marketbridge.domains.cart.service.DeleteCartService;
import com.objects.marketbridge.domains.cart.service.GetCartListService;
import com.objects.marketbridge.domains.cart.service.UpdateCartService;
import com.objects.marketbridge.domains.cart.service.dto.GetCartDto;
import com.objects.marketbridge.common.responseobj.ApiResponse;
import com.objects.marketbridge.common.responseobj.SliceResponse;
import com.objects.marketbridge.common.security.annotation.AuthMemberId;
import com.objects.marketbridge.common.security.annotation.UserAuthorize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final AddToCartService addToCartService;
    private final GetCartListService getCartListService;
    private final UpdateCartService updateCartService;
    private final DeleteCartService deleteCartService;

    @PostMapping("/carts")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<String> addToCart(
            @Validated @RequestBody CreateCartHttp.Request request,
            @AuthMemberId Long memberId) {

        addToCartService.add(request.toDto(memberId));

        return ApiResponse.create();
    }

    @GetMapping("/carts")
    public ApiResponse<SliceResponse<GetCartDto>> getCartItems(
            @PageableDefault(value = 5, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable,
            @AuthMemberId Long memberId) {

        return ApiResponse.ok(getCartListService.get(pageable, memberId));
    }

    @GetMapping("/carts/count")
    public ApiResponse<Long> countCartItems(
            @AuthMemberId Long memberId) {

        return ApiResponse.ok(getCartListService.countAll(memberId));
    }

    @PatchMapping("/carts/{cartId}")
    @UserAuthorize
    public ApiResponse<String> updateCartItems(
            @PathVariable(name = "cartId") Long cartId,
            @Validated @RequestBody UpdateCartHttp.Request request) {

        updateCartService.update(request.toDto(cartId));
        return ApiResponse.ok("update successful");
    }

    @DeleteMapping("/carts")
    @UserAuthorize
    public ApiResponse<String> deleteCartItem(
            @Validated @RequestBody DeleteCartHttp.Request request) {

        deleteCartService.delete(request.getSelectedCartIds());
        return ApiResponse.ok("delete successful");
    }
}
