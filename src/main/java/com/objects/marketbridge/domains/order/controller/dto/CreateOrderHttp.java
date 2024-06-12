package com.objects.marketbridge.domains.order.controller.dto;

import com.objects.marketbridge.common.customvalidator.annotation.ValidCouponExpired;
import com.objects.marketbridge.common.customvalidator.annotation.ValidDateTimeFormat;
import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.kakao.dto.KakaoPayReadyRequest;
import com.objects.marketbridge.common.kakao.dto.KakaoPayReadyResponse;
import com.objects.marketbridge.domains.order.service.dto.CreateOrderDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.COUPON_CONDITION_VIOLATION;
import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.INVALID_INPUT_VALUE;

@Getter
@Slf4j
public class CreateOrderHttp {

    // Request 클래스
    @Getter
    @NoArgsConstructor
    public static class Request {

        @NotNull
        private Long totalAmount;

        @NotNull
        private Long totalDiscountAmount; // 쿠폰 할인 만 적용

        @NotNull
        private Long realAmount;

        @NotNull
        private Long addressId;

        @NotNull
        private String orderName;

        @NotNull
        @Valid
        private List<CreateOrderHttp.Request.ProductInfo> productValues;

        @Builder
        public Request(Long totalAmount, Long totalDiscountAmount, Long realAmount, Long addressId, String orderName, List<CreateOrderHttp.Request.ProductInfo> productValues) {

            this.totalAmount = totalAmount;
            this.totalDiscountAmount = totalDiscountAmount;
            this.realAmount = realAmount;
            this.addressId = addressId;
            this.orderName = orderName;
            this.productValues = productValues;
        }

        public void valid() {
            validCouponActivation();
            validCouponCondition();
            validDiscountAmount();
            validTotalAmount();
        }

        private void validDiscountAmount() {
            Long totalDiscount = productValues.stream()
                    .filter(CreateOrderHttp.Request.ProductInfo::getHasCouponUsed)
                    .mapToLong(CreateOrderHttp.Request.ProductInfo::getCouponPrice)
                    .sum();

            if (!totalDiscount.equals(totalDiscountAmount)) {
                throw CustomLogicException.createBadRequestError(INVALID_INPUT_VALUE, "총 할인 금액이 일치하지 않습니다");
            }
        }

        private void validTotalAmount() {
            if (totalAmount != totalDiscountAmount + realAmount) {
                throw CustomLogicException.createBadRequestError(INVALID_INPUT_VALUE, "총 주문 금액이 일치하지 않습니다");
            }
        }
        private void validCouponActivation() {
            productValues.stream()
                    .filter(p -> !p.getHasCouponUsed())
                    .filter(p -> isCouponInfoProvided(p.getCouponId(), p.getCouponPrice(), p.getCouponMinimumPrice(), p.getCouponEndDate()))
                    .findFirst()
                    .ifPresent(p -> {
                        throw CustomLogicException.createBadRequestError(INVALID_INPUT_VALUE, "사용하지 않은 쿠폰의 관련 정보가 입력되었습니다.");
                    });
        }
        private boolean isCouponInfoProvided(Long couponId, Long couponPrice, Long couponMinimumPrice, String couponEndDate) {
            return couponId != null || couponPrice != null || couponMinimumPrice != null || couponEndDate != null;
        }

        private void validCouponCondition() {
            productValues.stream()
                    .filter(ProductInfo::getHasCouponUsed)
                    .filter(p -> p.getPrice() * p.getQuantity() < p.getCouponMinimumPrice())
                    .findFirst()
                    .ifPresent(p -> {
                        throw CustomLogicException.createBadRequestError(COUPON_CONDITION_VIOLATION);
                    });
        }

        public CreateOrderDto toDto(String orderNo, String tid, Long memberId) {
            return CreateOrderDto.builder()
                    .orderNo(orderNo)
                    .memberId(memberId)
                    .tid(tid)
                    .addressId(addressId)
                    .orderName(orderName)
                    .totalOrderPrice(totalAmount)
                    .realOrderPrice(realAmount)
                    .totalDiscountPrice(totalDiscountAmount)
                    .productValues(productValues.stream().map(ProductInfo::toDto).collect(Collectors.toList()))
                    .build();
        }

        public KakaoPayReadyRequest toKakaoReadyRequest(String orderNo, Long memberId, String cid, String approvalUrl, String failUrl, String cancelUrl) {
            return KakaoPayReadyRequest.builder()
                    .partnerOrderId(orderNo)
                    .partnerUserId(memberId.toString())
                    .cid(cid)
                    .approvalUrl(approvalUrl)
                    .failUrl(failUrl)
                    .cancelUrl(cancelUrl)
                    .quantity(productValues.stream().mapToLong(ProductInfo::getQuantity).sum())
                    .itemName(orderName)
                    .taxFreeAmount(0L)
                    .totalAmount(realAmount)
                    .build();
        }

        @Getter
        @NoArgsConstructor
        public static class ProductInfo{

            @NotNull
            Long productId;
            @NotNull
            Long price;
            @NotNull
            Long quantity;
            @NotNull
            Boolean hasCouponUsed;

            Long couponId;

            Long couponPrice;

            Long couponMinimumPrice;

            @ValidCouponExpired
            @ValidDateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            String couponEndDate;

            String deliveredDate; // yyyy-MM-dd HH:mm:ss

            @Builder
            private ProductInfo(Long productId, Long price, Long quantity, Boolean hasCouponUsed, Long couponId, Long couponPrice, Long couponMinimumPrice, String couponEndDate, String deliveredDate) {
                this.productId = productId;
                this.price = price;
                this.quantity = quantity;
                this.hasCouponUsed = hasCouponUsed;
                this.couponId = couponId;
                this.couponPrice = couponPrice;
                this.couponMinimumPrice = couponMinimumPrice;
                this.couponEndDate = couponEndDate;
                this.deliveredDate = deliveredDate;
            }

            public CreateOrderDto.ProductDto toDto() {
                return CreateOrderDto.ProductDto.builder()
                        .productId(productId)
                        .price(price)
                        .quantity(quantity)
                        .hasCouponUsed(hasCouponUsed)
                        .couponId(couponId)
                        .couponPrice(couponPrice)
                        .couponMinimumPrice(couponMinimumPrice)
                        .couponEndDate(couponEndDate)
                        .deliveredDate(deliveredDate)
                        .build();
            }
        }
    }

    // Response 클래스
    @Getter
    @NoArgsConstructor
    public static class Response{

        private String tid; // 결제 고유 번호
        private String nextRedirectPcUrl; // 요청한 클라이언트가 PC 웹일 경우
        private String nextRedirectAppUrl;// 요청한 클라이언트가 모바일 앱일 경우
        private String nextRedirectMobileUrl;// 요청한 클라이언트가 모바일 웹일 경우
        private String androidAppScheme; //카카오페이 결제 화면으로 이동하는 Android 앱 스킴(Scheme)
        private String iosAppScheme; // 카카오페이 결제 화면으로 이동하는 iOS 앱 스킴
        private String createdAt;

        @Builder
        private Response(String tid, String nextRedirectPcUrl, String nextRedirectAppUrl, String nextRedirectMobileUrl, String androidAppScheme, String iosAppScheme, String createdAt) {
            this.tid = tid;
            this.nextRedirectPcUrl = nextRedirectPcUrl;
            this.nextRedirectAppUrl = nextRedirectAppUrl;
            this.nextRedirectMobileUrl = nextRedirectMobileUrl;
            this.androidAppScheme = androidAppScheme;
            this.iosAppScheme = iosAppScheme;
            this.createdAt = createdAt;
        }

        public static Response of(KakaoPayReadyResponse kakaoResp) {

            return Response.builder()
                    .tid(kakaoResp.getTid())
                    .nextRedirectPcUrl(kakaoResp.getNextRedirectPcUrl())
                    .nextRedirectAppUrl(kakaoResp.getNextRedirectAppUrl())
                    .nextRedirectMobileUrl(kakaoResp.getNextRedirectMobileUrl())
                    .androidAppScheme(kakaoResp.getAndroidAppScheme())
                    .iosAppScheme(kakaoResp.getIosAppScheme())
                    .createdAt(kakaoResp.getCreatedAt())
                    .build();
        }

    }
}


