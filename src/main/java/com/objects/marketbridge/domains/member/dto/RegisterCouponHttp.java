package com.objects.marketbridge.domains.member.dto;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Getter
@Slf4j
@NoArgsConstructor
public class RegisterCouponHttp {

    @Getter
    @NoArgsConstructor
    public static class Response{
        private String name;
        private Long price;
        private Long productGroupId;
        private Long count;
        private Long minimumPrice;
        private LocalDateTime startDate;
        private LocalDateTime endDate;

        @Builder
        private Response(String name, Long price, Long productGroupId, Long count, Long minimumPrice, LocalDateTime startDate, LocalDateTime endDate) {
            this.name = name;
            this.price = price;
            this.productGroupId = productGroupId;
            this.count = count;
            this.minimumPrice = minimumPrice;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public static RegisterCouponHttp.Response of(Coupon coupon) {
            return Response.builder()
                    .name(coupon.getName())
                    .price(coupon.getPrice())
                    .productGroupId(coupon.getProductGroupId())
                    .count(coupon.getCount())
                    .minimumPrice(coupon.getMinimumPrice())
                    .startDate(coupon.getStartDate())
                    .endDate(coupon.getEndDate())
                    .build();
        }
    }
}
