package com.objects.marketbridge.domains.coupon.service.dto;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetCouponDto {

    private Long couponId;
    private Long productGroupId;
    private String couponName;
    private Long price;
    private Long count;
    private Long minimumPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder
    private GetCouponDto(Long couponId, String couponName, Long productGroupId, Long price, Long count, Long minimumPrice, LocalDateTime startDate, LocalDateTime endDate) {
        this.couponId = couponId;
        this.couponName = couponName;
        this.productGroupId = productGroupId;
        this.price = price;
        this.count = count;
        this.minimumPrice = minimumPrice;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static GetCouponDto of(Coupon coupon) {
        return GetCouponDto.builder()
                .couponId(coupon.getId())
                .productGroupId(coupon.getProductGroupId())
                .couponName(coupon.getName())
                .price(coupon.getPrice())
                .count(coupon.getCount())
                .minimumPrice(coupon.getMinimumPrice())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .build();
    }
}
