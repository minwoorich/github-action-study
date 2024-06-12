package com.objects.marketbridge.domains.member.dto;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;


class RegisterCouponHttpTest {

    @DisplayName("Coupon -> RegisterCouponHttp.Response 로 변화할 수 있다")
    @Test
    void of(){
        //given
        Coupon coupon = Coupon.builder()
                .count(100L)
                .price(1000L)
                .endDate(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
                .minimumPrice(15000L)
                .name("1000원 할인 쿠폰")
                .productGroupId(111111L)
                .build();

        //when
        RegisterCouponHttp.Response response = RegisterCouponHttp.Response.of(coupon);

        //then
        Assertions.assertThat(response).hasFieldOrPropertyWithValue("count", 100L);
        Assertions.assertThat(response).hasFieldOrPropertyWithValue("price", 1000L);
        Assertions.assertThat(response).hasFieldOrPropertyWithValue("endDate", LocalDateTime.of(2024, 1, 1, 12, 0, 0));
        Assertions.assertThat(response).hasFieldOrPropertyWithValue("minimumPrice", 15000L);
        Assertions.assertThat(response).hasFieldOrPropertyWithValue("name", "1000원 할인 쿠폰");
        Assertions.assertThat(response).hasFieldOrPropertyWithValue("productGroupId", 111111L);
    }
}