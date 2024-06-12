package com.objects.marketbridge.domains.coupon.service.dto;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreateCouponDto {
    private String name;
    private Long price;
    private Long productGroupId;
    private Long count;
    private Long minimumPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder
    private CreateCouponDto(String name, Long price, Long productGroupId, Long count, Long minimumPrice, LocalDateTime startDate, LocalDateTime endDate) {
        this.name = name;
        this.price = price;
        this.productGroupId = productGroupId;
        this.count = count;
        this.minimumPrice = minimumPrice;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Coupon toEntity() {
        return Coupon.create(name, productGroupId, price, count, minimumPrice, startDate, endDate);
    }
}
