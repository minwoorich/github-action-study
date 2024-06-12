package com.objects.marketbridge.domains.coupon.service;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.mock.FakeCouponRepository;
import com.objects.marketbridge.domains.coupon.service.dto.CreateCouponDto;
import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;


class CreateCouponServiceTestWithFake {

    CouponRepository couponRepository = new FakeCouponRepository();
    CreateCouponService createCouponService = CreateCouponService.builder().couponRepository(couponRepository).build();

    @AfterEach
    void clear() {
        couponRepository.deleteAllInBatch();
    }
    @DisplayName("쿠폰을 생성할 수 있다")
    @Test
    void create(){
        //given
        CreateCouponDto createCouponDto = CreateCouponDto.builder()
                .name("1000원 할인 쿠폰")
                .price(1000L)
                .productGroupId(111111L)
                .count(9999L)
                .minimumPrice(10000L)
                .startDate(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
                .endDate(LocalDateTime.of(2025, 1, 1, 12, 0, 0))
                .build();

        //when
        createCouponService.create(createCouponDto);
        Coupon coupon = couponRepository.findById(1L);

        //then
        assertThat(coupon).hasFieldOrPropertyWithValue("name", "1000원 할인 쿠폰");
        assertThat(coupon).hasFieldOrPropertyWithValue("price", 1000L);
        assertThat(coupon).hasFieldOrPropertyWithValue("productGroupId", 111111L);
        assertThat(coupon).hasFieldOrPropertyWithValue("count", 9999L);
        assertThat(coupon).hasFieldOrPropertyWithValue("minimumPrice", 10000L);
        assertThat(coupon).hasFieldOrPropertyWithValue("startDate", LocalDateTime.of(2024, 1, 1, 12, 0, 0));
        assertThat(coupon).hasFieldOrPropertyWithValue("endDate", LocalDateTime.of(2025, 1, 1, 12, 0, 0));
    }

}