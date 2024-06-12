package com.objects.marketbridge.domains.coupon.service.dto;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

class GetCouponDtoTest {

    @DisplayName("Coupon 에서 GetCouponDto 를 생성 할 수 있다.")
    @Test
    void of(){
        //given
        Coupon coupon = Coupon.builder().productGroupId(111111L).price(1000L).build();

        //when
        GetCouponDto result = GetCouponDto.of(coupon);

        //then
        assertThat(result).hasFieldOrProperty("productGroupId");
        assertThat(result).hasFieldOrProperty("couponName");
        assertThat(result).hasFieldOrProperty("price");
        assertThat(result).hasFieldOrProperty("count");
        assertThat(result).hasFieldOrProperty("minimumPrice");
        assertThat(result).hasFieldOrProperty("startDate");
        assertThat(result).hasFieldOrProperty("endDate");

        assertThat(result).extracting(GetCouponDto::getPrice, GetCouponDto::getProductGroupId)
                .containsExactly(1000L, 111111L);
    }

}