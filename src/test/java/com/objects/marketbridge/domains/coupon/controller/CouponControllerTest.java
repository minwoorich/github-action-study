package com.objects.marketbridge.domains.coupon.controller;

import com.objects.marketbridge.common.responseobj.ApiResponse;
import com.objects.marketbridge.domains.coupon.controller.dto.GetCouponHttp;
import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.coupon.mock.FakeCouponRepository;
import com.objects.marketbridge.domains.coupon.service.GetCouponService;
import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.mock.FakeMemberRepository;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.product.domain.Product;
import com.objects.marketbridge.domains.product.mock.FakeProductRepository;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponControllerTest {

    CouponRepository couponRepository = new FakeCouponRepository();
    ProductRepository productRepository = new FakeProductRepository();
    MemberRepository memberRepository = new FakeMemberRepository();

    GetCouponService getCouponService = GetCouponService.builder().couponRepository(couponRepository).build();
    CouponController couponController = CouponController.builder().getCouponService(getCouponService).build();


    @AfterEach
    void clear() {
        productRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        couponRepository.deleteAllInBatch();
    }

    @DisplayName("상품에 등록된 모든 쿠폰들을 조회할 수 있다.")
    @Test
    void findCouponsForProductGroup(){
        //given
        Member member = memberRepository.save(Member.builder().name("홍길동").build());
        Product product = productRepository.save(Product.builder().productNo("111111 - 111111").name("신발").build());

        MemberCoupon memberCoupon1 = MemberCoupon.builder().member(member).isUsed(false).build();
        MemberCoupon memberCoupon2 = MemberCoupon.builder().member(member).isUsed(false).build();
        MemberCoupon memberCoupon3 = MemberCoupon.builder().member(member).isUsed(false).build();

        Coupon coupon1 = Coupon.builder().price(1000L).productGroupId(111111L).build();
        Coupon coupon2 = Coupon.builder().price(2000L).productGroupId(111111L).build();
        Coupon coupon3 = Coupon.builder().price(3000L).productGroupId(111111L).build();

        coupon1.addMemberCoupon(memberCoupon1);
        coupon2.addMemberCoupon(memberCoupon2);
        coupon3.addMemberCoupon(memberCoupon3);
        couponRepository.saveAll(List.of(coupon1, coupon2, coupon3));

        //when
        ApiResponse<GetCouponHttp.Response> response = couponController.findCouponsForProductGroup(111111L);

        //then
        assertThat(response.getCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getData().getHasCoupons()).isTrue();
        assertThat(response.getData().getCouponInfos()).hasSize(3);
        assertThat(response.getData().getCouponInfos())
                .extracting(GetCouponHttp.Response.CouponInfo::getCouponPrice)
                .containsExactly(1000L, 2000L, 3000L);
    }

    @DisplayName("상품에 등록된 쿠폰이 없으면 빈 배열을 반환한다.")
    @Test
    void findCouponsForProductGroup_empty(){
        //given
        // 등록된 쿠폰이 없음

        //when
        ApiResponse<GetCouponHttp.Response> response = couponController.findCouponsForProductGroup(1L);

        //then
        assertThat(response.getCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getData().getHasCoupons()).isFalse();
        assertThat(response.getData().getCouponInfos()).isEmpty();
    }

    @DisplayName("쿠폰 아이디로 쿠폰을 조회할 수 있다")
    @Test
    void findCoupon(){
        //given
        Coupon coupon = couponRepository.save(Coupon.builder().price(1000L).build());

        //when
        ApiResponse<GetCouponHttp.Response> response = couponController.findCoupon(coupon.getId());

        //then
        assertThat(response.getCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getData().getHasCoupons()).isTrue();
        assertThat(response.getData().getCouponInfos()).hasSize(1);
        assertThat(response.getData().getCouponInfos())
                .extracting(
                        GetCouponHttp.Response.CouponInfo::getCouponPrice,
                        GetCouponHttp.Response.CouponInfo::getCouponId)
                .containsExactly(Tuple.tuple(1000L, 1L));
    }
}