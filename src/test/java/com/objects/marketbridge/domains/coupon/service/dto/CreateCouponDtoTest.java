package com.objects.marketbridge.domains.coupon.service.dto;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CreateCouponDtoTest {

    @DisplayName("CreateCouponDto -> Coupon 엔티티 로 변환 할 수 있다")
    @Test
    void toEntity(){
        //given
        CreateCouponDto couponDto = CreateCouponDto.builder()
                .price(1000L)
                .name("1000원 할인 쿠폰")
                .count(9999L)
                .productGroupId(111111L)
                .startDate(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
                .endDate(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                .minimumPrice(15000L)
                .build();

        //when
        Coupon coupon = couponDto.toEntity();

        //then
        assertThat(coupon).hasFieldOrPropertyWithValue("price", 1000L);
        assertThat(coupon).hasFieldOrPropertyWithValue("name", "1000원 할인 쿠폰");
        assertThat(coupon).hasFieldOrPropertyWithValue("count", 9999L);
        assertThat(coupon).hasFieldOrPropertyWithValue("productGroupId", 111111L);
        assertThat(coupon).hasFieldOrPropertyWithValue("startDate", LocalDateTime.of(2024, 1, 1, 12, 0, 0));
        assertThat(coupon).hasFieldOrPropertyWithValue("endDate", LocalDateTime.of(2025, 1, 1, 12, 0, 0));
        assertThat(coupon).hasFieldOrPropertyWithValue("minimumPrice", 15000L);
    }

}