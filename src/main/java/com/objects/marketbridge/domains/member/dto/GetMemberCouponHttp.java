package com.objects.marketbridge.domains.member.dto;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class GetMemberCouponHttp {

    @Getter
    @NoArgsConstructor
    public static class Response{
        private List<CouponInfo> couponInfos;
        private Long memberId;
        private Boolean hasCoupons;

        @Builder
        private Response(List<CouponInfo> couponInfos, Long memberId, Boolean hasCoupons) {
            this.couponInfos = couponInfos;
            this.memberId = memberId;
            this.hasCoupons = hasCoupons;
        }

        public static GetMemberCouponHttp.Response create(List<CouponInfo> couponInfos, Long memberId, Boolean hasCoupons) {
            return Response.builder()
                    .couponInfos(couponInfos)
                    .memberId(memberId)
                    .hasCoupons(hasCoupons)
                    .build();
        }

        @Getter
        @NoArgsConstructor
        public static class CouponInfo {
            private Long couponId;
            private String name;
            private Long price;
            private Long productGroupId;
            private Long count;
            private Long minimumPrice;
            private LocalDateTime startDate;
            private LocalDateTime endDate;

            @Builder
            private CouponInfo(Long couponId, String name, Long price, Long productGroupId, Long count, Long minimumPrice, LocalDateTime startDate, LocalDateTime endDate) {
                this.couponId = couponId;
                this.name = name;
                this.price = price;
                this.productGroupId = productGroupId;
                this.count = count;
                this.minimumPrice = minimumPrice;
                this.startDate = startDate;
                this.endDate = endDate;
            }

            private static GetMemberCouponHttp.Response.CouponInfo of(Coupon coupon) {
                return CouponInfo.builder()
                        .couponId(coupon.getId())
                        .name(coupon.getName())
                        .price(coupon.getPrice())
                        .productGroupId(coupon.getProductGroupId())
                        .count(coupon.getCount())
                        .minimumPrice(coupon.getMinimumPrice())
                        .startDate(coupon.getStartDate())
                        .endDate(coupon.getEndDate())
                        .build();
            }

            public static List<GetMemberCouponHttp.Response.CouponInfo> of(List<Coupon> coupons) {
                return coupons.stream().map(CouponInfo::of).collect(Collectors.toList());
            }
        }
    }
}
