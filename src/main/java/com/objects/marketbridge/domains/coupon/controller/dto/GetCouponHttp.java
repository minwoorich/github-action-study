package com.objects.marketbridge.domains.coupon.controller.dto;

import com.objects.marketbridge.domains.coupon.service.dto.GetCouponDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class GetCouponHttp {

    @Getter
    @NoArgsConstructor
    public static class Response{

        private List<CouponInfo> couponInfos;
        private Boolean hasCoupons;

        @Builder
        private Response(List<CouponInfo> couponInfos, Boolean hasCoupons) {
            this.couponInfos = couponInfos;
            this.hasCoupons = hasCoupons;
        }

        public static GetCouponHttp.Response create(List<GetCouponHttp.Response.CouponInfo> couponInfos) {
            return Response.builder()
                    .hasCoupons(true)
                    .couponInfos(couponInfos)
                    .build();
        }

        public static GetCouponHttp.Response create() {
            return Response.builder()
                    .hasCoupons(false)
                    .couponInfos(new ArrayList<>())
                    .build();
        }

        @Getter
        @NoArgsConstructor
        public static class CouponInfo{

            private String couponName;
            private Long couponPrice;
            private Long minimumPrice;
            private Long count;
            private LocalDateTime startDate;
            private LocalDateTime endDate;
            private Long couponId;

            @Builder
            private CouponInfo(Long couponId, String couponName, Long count, Long couponPrice, Long minimumPrice, LocalDateTime startDate, LocalDateTime endDate) {
                this.couponId = couponId;
                this.count = count;
                this.couponName = couponName;
                this.couponPrice = couponPrice;
                this.minimumPrice = minimumPrice;
                this.startDate = startDate;
                this.endDate = endDate;
            }

            public static GetCouponHttp.Response.CouponInfo of(GetCouponDto dto) {
                return CouponInfo.builder()
                        .couponId(dto.getCouponId())
                        .couponName(dto.getCouponName())
                        .couponPrice(dto.getPrice())
                        .count(dto.getCount())
                        .minimumPrice(dto.getMinimumPrice())
                        .startDate(dto.getStartDate())
                        .endDate(dto.getEndDate())
                        .build();
            }
        }
    }
}
