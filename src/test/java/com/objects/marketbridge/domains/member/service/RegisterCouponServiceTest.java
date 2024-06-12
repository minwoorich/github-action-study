package com.objects.marketbridge.domains.member.service;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.exception.exceptions.ErrorCode;
import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.coupon.mock.FakeCouponRepository;
import com.objects.marketbridge.domains.coupon.mock.FakeMemberCouponRepository;
import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import com.objects.marketbridge.domains.coupon.service.port.MemberCouponRepository;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.mock.FakeMemberRepository;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RegisterCouponServiceTest {

    CouponRepository couponRepository = new FakeCouponRepository();
    MemberRepository memberRepository = new FakeMemberRepository();
    MemberCouponRepository memberCouponRepository = new FakeMemberCouponRepository();

    RegisterCouponService registerCouponService = RegisterCouponService.builder()
            .couponRepository(couponRepository)
            .memberRepository(memberRepository)
            .memberCouponRepository(memberCouponRepository)
            .build();

    @AfterEach
    void clear() {
        couponRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        memberCouponRepository.deleteAllInBatch();
    }
    @Test
    @DisplayName("회원이 쿠폰을 다운로드 받을 수 있다")
    void registerCouponToMember() {
        // given
        Member member = memberRepository.save(Member.builder().name("홍길동").build());
        Coupon coupon = couponRepository.save(Coupon.builder().price(1000L).count(100L).build());
        // when
        Coupon findCoupon = registerCouponService.registerCouponToMember(member.getId(), coupon.getId());

        // then
        assertThat(findCoupon.getMemberCoupons())
                .extracting(
                        mc -> mc.getMember(),
                        mc -> mc.getCoupon().getCount(),
                        mc -> mc.getCoupon().getPrice())
                .contains(
                        Tuple.tuple(member, 99L, 1000L));
    }

    @DisplayName("쿠폰 수량이 모두 소진 된 경우 에러를 발생시킨다")
    @Test
    void registerCouponToMember_error_COUPON_OUT_OF_STOCK(){
        //given
        Member member = memberRepository.save(Member.builder().name("홍길동").build());
        Coupon coupon = couponRepository.save(Coupon.builder().price(1000L).count(0L).build());

        //when
        Throwable thrown = catchThrowable(() -> registerCouponService.registerCouponToMember(member.getId(), coupon.getId()));

        //then
        assertThat(thrown)
                .isInstanceOf(CustomLogicException.class)
                .hasMessage(ErrorCode.COUPON_OUT_OF_STOCK.getMessage());
    }

    @DisplayName("이미 등록된 쿠폰일 경우 에러를 발생 시킨다")
    @Test
    void registerCouponToMember_error_DUPLICATE_ERROR(){
        //given
        Member member = memberRepository.save(Member.builder().name("홍길동").build());

        MemberCoupon memberCoupon = MemberCoupon.builder().member(member).build();
        Coupon coupon = Coupon.builder().price(1000L).count(0L).build();
        coupon.addMemberCoupon(memberCoupon);

        Coupon savedCoupon = couponRepository.save(coupon);
        memberCouponRepository.save(memberCoupon);

        //when
        Throwable thrown = catchThrowable(() -> registerCouponService.registerCouponToMember(member.getId(), savedCoupon.getId()));

        //then
        assertThat(thrown)
                .isInstanceOf(CustomLogicException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_ERROR)
                .hasMessage("이미 등록된 쿠폰 입니다");
    }
}