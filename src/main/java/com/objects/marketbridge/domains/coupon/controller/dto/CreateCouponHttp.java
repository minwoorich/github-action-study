package com.objects.marketbridge.domains.coupon.controller.dto;


import com.objects.marketbridge.common.customvalidator.annotation.ValidDateTimeFormat;
import com.objects.marketbridge.domains.coupon.service.dto.CreateCouponDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class CreateCouponHttp {

    @Getter
    @NoArgsConstructor
    public static class Request{
        @NotNull
        private String name;

        @NotNull
        private Long price;

        @NotNull
        private Long productGroupId;

        @NotNull
        private Long count;

        @NotNull
        private Long minimumPrice;

        @NotNull
        @ValidDateTimeFormat
        private LocalDateTime startDate; // yyyy-MM-dd HH:mm:ss

        @NotNull
        @ValidDateTimeFormat
        private LocalDateTime endDate; // yyyy-MM-dd HH:mm:ss

        @Builder
        private Request(String name, Long price, Long productGroupId, Long count, Long minimumPrice, LocalDateTime startDate, LocalDateTime endDate) {
            this.name = name;
            this.price = price;
            this.productGroupId = productGroupId;
            this.count = count;
            this.minimumPrice = minimumPrice;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public CreateCouponDto toDto() {
            return CreateCouponDto.builder()
                    .name(name)
                    .price(price)
                    .productGroupId(productGroupId)
                    .count(count)
                    .minimumPrice(minimumPrice)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Response{

    }
}
