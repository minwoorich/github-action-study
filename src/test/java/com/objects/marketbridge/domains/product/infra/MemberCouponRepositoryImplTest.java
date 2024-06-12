package com.objects.marketbridge.domains.product.infra;

import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.coupon.service.port.CouponRepository;
import com.objects.marketbridge.domains.coupon.service.port.MemberCouponRepository;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberCouponRepositoryImplTest {

    @Autowired MemberCouponRepository memberCouponRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CouponRepository couponRepository;

    @DisplayName("memberId 와 couponId 로 MemberCoupon 을 조회할 수 있다. (member,coupon fetch join(o)")
    @Test
    void findByMemberIdAndCouponId() {
        Member member = memberRepository.save(Member.builder().build());

        Coupon coupon = Coupon.builder().build();
        coupon.addMemberCoupon(MemberCoupon.builder().coupon(coupon).member(member).build());
        couponRepository.save(coupon);

        // when
        MemberCoupon memberCoupon = memberCouponRepository.findByMemberIdAndCouponId(member.getId(), coupon.getId());

        //then
        assertThat(memberCoupon)
                .extracting(mc -> mc.getMember().getId(), mc -> mc.getCoupon().getId())
                .contains(member.getId(), coupon.getId());
    }
}