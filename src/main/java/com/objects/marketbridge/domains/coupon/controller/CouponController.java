package com.objects.marketbridge.domains.coupon.controller;

import com.objects.marketbridge.common.responseobj.ApiResponse;
import com.objects.marketbridge.common.security.annotation.AuthMemberId;
import com.objects.marketbridge.common.security.annotation.UserAuthorize;
import com.objects.marketbridge.domains.coupon.controller.dto.CreateCouponHttp;
import com.objects.marketbridge.domains.coupon.controller.dto.GetCouponHttp;
import com.objects.marketbridge.domains.coupon.service.CreateCouponService;
import com.objects.marketbridge.domains.coupon.service.GetCouponService;
import com.objects.marketbridge.domains.coupon.service.dto.GetCouponDto;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class CouponController {

    private final GetCouponService getCouponService;
    private final CreateCouponService createCouponService;

    @Builder
    public CouponController(GetCouponService getCouponService, CreateCouponService createCouponService) {
        this.getCouponService = getCouponService;
        this.createCouponService = createCouponService;
    }

    @GetMapping("/coupons")
    @UserAuthorize
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<GetCouponHttp.Response> findCouponsForProductGroup(
            @RequestParam Long productGroupId) {

        return ApiResponse.ok(convertToResponse(getCouponService.findCouponsForProductGroup(productGroupId)));
    }

    @GetMapping("/coupons/{couponId}")
    @UserAuthorize
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<GetCouponHttp.Response> findCoupon(
            @PathVariable Long couponId) {

        List<GetCouponDto> couponDtos = List.of(getCouponService.find(couponId));
        return ApiResponse.ok(convertToResponse(couponDtos));
    }

    private GetCouponHttp.Response convertToResponse(List<GetCouponDto> couponDtos) {

        if (couponDtos.isEmpty()) {
            return GetCouponHttp.Response.create();
        }
        List<GetCouponHttp.Response.CouponInfo> couponInfos = couponDtos.stream()
                .map(GetCouponHttp.Response.CouponInfo::of) // GetCouponDto -> GetCouponHttp.Response.CouponInfo
                .collect(Collectors.toList());

        return GetCouponHttp.Response.create(couponInfos);
    }

    // TODO : 테스트 코드 작성
    @PostMapping("/coupons")
    @UserAuthorize
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<GetCouponHttp.Response> createCoupon(
            @RequestBody CreateCouponHttp.Request request) {
        createCouponService.create(request.toDto());
        return ApiResponse.create();
    }
}
