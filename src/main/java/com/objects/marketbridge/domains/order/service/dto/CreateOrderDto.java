package com.objects.marketbridge.domains.order.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateOrderDto {

    private String tid;
    private Long memberId;
    private Long addressId;
    private String orderName;
    private String orderNo;
    private Long totalOrderPrice;
    private Long realOrderPrice;
    private Long totalDiscountPrice;
    private List<CreateOrderDto.ProductDto> productValues;

    @Builder
    public CreateOrderDto(String tid, Long memberId, Long addressId, String orderName, String orderNo, Long totalOrderPrice, Long realOrderPrice, Long totalDiscountPrice, List<CreateOrderDto.ProductDto> productValues) {
        this.tid = tid;
        this.memberId = memberId;
        this.addressId = addressId;
        this.orderName = orderName;
        this.orderNo = orderNo;
        this.totalOrderPrice = totalOrderPrice;
        this.realOrderPrice = realOrderPrice;
        this.totalDiscountPrice = totalDiscountPrice;
        this.productValues = productValues;
    }

    @Getter
    @NoArgsConstructor
    public static class ProductDto{

        Long productId;
        Long price;
        Long quantity;
        Boolean hasCouponUsed;
        Long couponId;
        Long couponPrice;
        Long couponMinimumPrice;
        String couponEndDate;
        String deliveredDate; // yyyy-MM-dd HH:mm:ss

        @Builder
        public ProductDto(Long productId, Long price, Long quantity, Boolean hasCouponUsed, Long couponId, Long couponPrice, Long couponMinimumPrice, String couponEndDate, String deliveredDate) {
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
    }
}
