package com.objects.marketbridge.domains.coupon.service;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.coupon.mock.FakeCouponRepository;
import com.objects.marketbridge.domains.coupon.service.dto.GetCouponDto;
import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.mock.FakeMemberRepository;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.product.mock.FakeProductRepository;
import com.objects.marketbridge.domains.product.service.port.ProductRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class GetCouponServiceTestWithFake {

    CouponRepository couponRepository = new FakeCouponRepository();
    ProductRepository productRepository = new FakeProductRepository();
    MemberRepository memberRepository = new FakeMemberRepository();

    GetCouponService getCouponService = GetCouponService.builder().couponRepository(couponRepository).build();

    @AfterEach
    void clear() {
        productRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        couponRepository.deleteAllInBatch();
    }


    @DisplayName("상품에 등록된 모든 쿠폰들을 조회할 수 있다.")
    @Test
    void findCouponsForProduct(){
        //given
        Member member = memberRepository.save(Member.builder().name("홍길동").build());

        MemberCoupon memberCoupon1 = MemberCoupon.builder().member(member).isUsed(false).build();
        MemberCoupon memberCoupon2 = MemberCoupon.builder().member(member).isUsed(false).build();
        MemberCoupon memberCoupon3 = MemberCoupon.builder().member(member).isUsed(false).build();

        Coupon coupon1 = Coupon.builder().price(1000L).productGroupId(111111L).build();
        coupon1.addMemberCoupon(memberCoupon1);
        Coupon coupon2 = Coupon.builder().price(2000L).productGroupId(111111L).build();
        coupon2.addMemberCoupon(memberCoupon2);
        Coupon coupon3 = Coupon.builder().price(3000L).productGroupId(111111L).build();
        coupon3.addMemberCoupon(memberCoupon3);

        couponRepository.saveAll(List.of(coupon1, coupon2, coupon3));

        //when
        List<GetCouponDto> couponDtos = getCouponService.findCouponsForProductGroup(111111L);

        //then
        assertThat(couponDtos).hasSize(3);
        assertThat(couponDtos)
                .extracting(c -> c.getPrice(), c -> c.getProductGroupId())
                .contains(
                        Tuple.tuple(1000L, 111111L),
                        Tuple.tuple(2000L, 111111L),
                        Tuple.tuple(3000L, 111111L)
                );
    }

    @DisplayName("상품에 등록된 쿠폰이 없을 경우 couponInfos 에 빈 리스트가 저장된다.")
    @Test
    void findCouponsForProduct_empty(){
        //given
        //when
        List<GetCouponDto> couponDtos = getCouponService.findCouponsForProductGroup(111111L);

        //then
        assertThat(couponDtos).isEmpty();
        assertThat(couponDtos).isNotNull();
        assertThat(couponDtos).isInstanceOf(List.class);
    }

    @DisplayName("쿠폰 아이디로 쿠폰을 조회할 수 있다")
    @Test
    void find() {
        // given
        Coupon coupon = Coupon.builder().price(1000L).productGroupId(111111L).build();
        Coupon savedCoupon = couponRepository.save(coupon);

        // when
        GetCouponDto result = getCouponService.find(savedCoupon.getId());

        //then
        assertThat(result)
                .extracting("couponId", "price", "productGroupId")
                .contains(1L, 1000L, 111111L);
    }

    @DisplayName("해당하는 쿠폰이 없으면 에러를 발생 시킨다")
    @Test
    void find_error() {
        // given
        Long wrongCouponId = 1L;
        // when
        Throwable thrown = catchThrowable(() -> getCouponService.find(wrongCouponId));

        //then
        assertThat(thrown)
                .isInstanceOf(JpaObjectRetrievalFailureException.class)
                .hasMessage("해당 쿠폰 엔티티가 존재하지 않습니다. 입력 id = " + wrongCouponId);
    }
}