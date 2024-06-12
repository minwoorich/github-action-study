package com.objects.marketbridge.domains.member.service;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.coupon.mock.FakeCouponRepository;
import com.objects.marketbridge.domains.coupon.mock.FakeMemberCouponRepository;
import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import com.objects.marketbridge.domains.coupon.service.port.MemberCouponRepository;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.mock.FakeMemberRepository;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class GetMemberCouponServiceTestWithFake {

    MemberCouponRepository memberCouponRepository = new FakeMemberCouponRepository();
    MemberRepository memberRepository = new FakeMemberRepository();
    CouponRepository couponRepository = new FakeCouponRepository();
    GetMemberCouponService getMemberCouponService = GetMemberCouponService.builder().memberCouponRepository(memberCouponRepository).build();

    @DisplayName("사용자가 가지고 있는 모든 쿠폰을 조회할 수 있다")
    @Test
    void findCouponsForMember() {
        // given
        Member member1 = memberRepository.save(Member.builder().build());
        Member member2 = memberRepository.save(Member.builder().build());

        Coupon coupon1 = couponRepository.save(Coupon.builder().price(1000L).build());
        Coupon coupon2 = couponRepository.save(Coupon.builder().price(2000L).build());
        Coupon coupon3 = couponRepository.save(Coupon.builder().price(3000L).build());
        Coupon coupon4 = couponRepository.save(Coupon.builder().price(10000L).build());
        Coupon coupon5 = couponRepository.save(Coupon.builder().price(20000L).build());
        Coupon coupon6 = couponRepository.save(Coupon.builder().price(30000L).build());

        memberCouponRepository.save(MemberCoupon.builder().isUsed(false).member(member1).coupon(coupon1).build());
        memberCouponRepository.save(MemberCoupon.builder().isUsed(false).member(member1).coupon(coupon2).build());
        memberCouponRepository.save(MemberCoupon.builder().isUsed(false).member(member1).coupon(coupon3).build());
        memberCouponRepository.save(MemberCoupon.builder().isUsed(false).member(member2).coupon(coupon4).build());
        memberCouponRepository.save(MemberCoupon.builder().isUsed(false).member(member2).coupon(coupon5).build());
        memberCouponRepository.save(MemberCoupon.builder().isUsed(false).member(member2).coupon(coupon6).build());

        // when
        List<Coupon> coupons = getMemberCouponService.findCouponsForMember(member1.getId());

        //then
        assertThat(coupons).hasSize(3);
        assertThat(coupons).extracting(c -> c.getPrice()).contains(1000L, 2000L, 3000L);
    }

    @DisplayName("사용자가 가지고 있는 쿠폰 중, 사용 하지 않은 쿠폰들만 조회할 수 있어야한다.")
    @Test
    void findCouponsForMember_unused() {
        // given
        Member member1 = memberRepository.save(Member.builder().build());

        Coupon coupon1 = couponRepository.save(Coupon.builder().price(1000L).build());
        Coupon coupon2 = couponRepository.save(Coupon.builder().price(2000L).build());
        Coupon coupon3 = couponRepository.save(Coupon.builder().price(3000L).build());

        memberCouponRepository.save(MemberCoupon.builder().member(member1).isUsed(false).coupon(coupon1).build());
        memberCouponRepository.save(MemberCoupon.builder().member(member1).isUsed(true).coupon(coupon2).build());
        memberCouponRepository.save(MemberCoupon.builder().member(member1).isUsed(false).coupon(coupon3).build());

        // when
        List<Coupon> coupons = getMemberCouponService.findCouponsForMember(member1.getId());

        //then
        assertThat(coupons).hasSize(2);
        assertThat(coupons).extracting(Coupon::getPrice).contains(1000L, 3000L);
    }
}