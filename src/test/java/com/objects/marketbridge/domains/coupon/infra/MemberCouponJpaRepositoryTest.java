package com.objects.marketbridge.domains.coupon.infra;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class MemberCouponJpaRepositoryTest {


    @Autowired MemberRepository memberRepository;
    @Autowired CouponRepository couponRepository;
    @Autowired MemberCouponJpaRepository memberCouponJpaRepository;


    @DisplayName("memberId 를 통해 MemberCoupon 리스트를 조회할 수 있다")
    @Test
    void findByMemberId() {
        // given
        Member member = memberRepository.save(Member.builder().name("홍길동").build());
        Coupon coupon1 = couponRepository.save(Coupon.builder().price(1000L).build());
        Coupon coupon2 = couponRepository.save(Coupon.builder().price(2000L).build());
        Coupon coupon3 = couponRepository.save(Coupon.builder().price(3000L).build());

        MemberCoupon memberCoupon1 = MemberCoupon.builder().coupon(coupon1).member(member).build();
        MemberCoupon memberCoupon2 = MemberCoupon.builder().coupon(coupon2).member(member).build();
        MemberCoupon memberCoupon3 = MemberCoupon.builder().coupon(coupon3).member(member).build();

        memberCouponJpaRepository.saveAll(List.of(memberCoupon1, memberCoupon2, memberCoupon3));

        // when
        List<MemberCoupon> memberCoupons = memberCouponJpaRepository.findByMemberId(member.getId());

        //then
        assertThat(memberCoupons).extracting(
                        mc -> mc.getMember().getName(),
                        mc -> mc.getCoupon().getPrice())
                .contains(Tuple.tuple("홍길동", 1000L),
                        Tuple.tuple("홍길동", 2000L),
                        Tuple.tuple("홍길동", 3000L));

    }

    @DisplayName("memberId 와 couponId 로 MemberCoupon 을 조회할 수 있다. (member,coupon fetch join(o)")
    @Test
    void findByMemberIdAndCouponId() {
        Member member = memberRepository.save(Member.builder().build());
        Coupon coupon = couponRepository.save(Coupon.builder().build());
        memberCouponJpaRepository.save(MemberCoupon.builder().coupon(coupon).member(member).build());

        // when
        Optional<MemberCoupon> memberCoupon = memberCouponJpaRepository.findByMemberIdAndCouponId(member.getId(), coupon.getId());

        //then
        assertThat(memberCoupon.get())
                .extracting(mc -> mc.getMember().getId(), mc -> mc.getCoupon().getId())
                .contains(member.getId(), coupon.getId());
    }

}