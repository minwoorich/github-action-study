package com.objects.marketbridge.domains.member.dto;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GetMemberCouponHttpTest {

    @DisplayName("GetMemberCouponHttp.Response 를 생성할 수 있다")
    @Test
    void of() {
        // given
        Coupon coupon1 = Coupon.builder()
                .id(1L)
                .name("1000원 할인 쿠폰")
                .price(1000L)
                .productGroupId(111111L)
                .count(9999L)
                .minimumPrice(15000L)
                .startDate(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
                .endDate(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                .build();

        Coupon coupon2 = Coupon.builder()
                .id(2L)
                .name("2000원 할인 쿠폰")
                .price(2000L)
                .productGroupId(111111L)
                .count(9999L)
                .minimumPrice(15000L)
                .startDate(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
                .endDate(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                .build();

        Coupon coupon3 = Coupon.builder()
                .id(3L)
                .name("3000원 할인 쿠폰")
                .price(3000L)
                .productGroupId(111111L)
                .count(9999L)
                .minimumPrice(15000L)
                .startDate(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
                .endDate(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                .build();

        List<Coupon> coupons = List.of(coupon1, coupon2, coupon3);

        // when
        List<GetMemberCouponHttp.Response.CouponInfo> couponInfos = GetMemberCouponHttp.Response.CouponInfo.of(coupons);

        //then
        assertThat(couponInfos)
                .extracting(GetMemberCouponHttp.Response.CouponInfo::getCouponId,
                        GetMemberCouponHttp.Response.CouponInfo::getName,
                        GetMemberCouponHttp.Response.CouponInfo::getPrice,
                        GetMemberCouponHttp.Response.CouponInfo::getProductGroupId,
                        GetMemberCouponHttp.Response.CouponInfo::getCount,
                        GetMemberCouponHttp.Response.CouponInfo::getMinimumPrice,
                        GetMemberCouponHttp.Response.CouponInfo::getStartDate,
                        GetMemberCouponHttp.Response.CouponInfo::getEndDate)
                .contains(
                        Tuple.tuple(1L, "1000원 할인 쿠폰", 1000L, 111111L, 9999L, 15000L, LocalDateTime.of(2024, 1, 1, 12, 0, 0), LocalDateTime.of(2025, 1, 1, 12, 0, 0)),
                        Tuple.tuple(2L, "2000원 할인 쿠폰", 2000L, 111111L, 9999L, 15000L, LocalDateTime.of(2024, 1, 1, 12, 0, 0), LocalDateTime.of(2025, 1, 1, 12, 0, 0)),
                        Tuple.tuple(3L, "3000원 할인 쿠폰", 3000L, 111111L, 9999L, 15000L, LocalDateTime.of(2024, 1, 1, 12, 0, 0), LocalDateTime.of(2025, 1, 1, 12, 0, 0)));
    }

    @DisplayName("GetMemberCouponHttp.Response 를 생성 할 수 있다")
    @Test
    void create() {
        // given
        Long memberId = 1L;
        List<GetMemberCouponHttp.Response.CouponInfo> couponInfos = List.of(GetMemberCouponHttp.Response.CouponInfo.builder()
                .couponId(1L)
                .name("1000원 할인 쿠폰")
                .price(1000L)
                .build());

        // when
        GetMemberCouponHttp.Response response = GetMemberCouponHttp.Response.create(couponInfos, memberId, true);

        //then
        assertThat(response).hasFieldOrPropertyWithValue("hasCoupons", true);
        assertThat(response).hasFieldOrPropertyWithValue("memberId", memberId);
        assertThat(response).hasFieldOrPropertyWithValue("couponInfos", couponInfos);
    }
}