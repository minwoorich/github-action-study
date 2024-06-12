package com.objects.marketbridge.domains.coupon.infra;

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

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FakeMemberCouponRepositoryTest {

    MemberCouponRepository memberCouponRepository = new FakeMemberCouponRepository();
    MemberRepository memberRepository = new FakeMemberRepository();
    CouponRepository couponRepository = new FakeCouponRepository();

    @AfterEach
    void clear() {
        memberCouponRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        couponRepository.deleteAllInBatch();
    }


    @DisplayName("MemberCouponId 로 MemberCoupon 을 조회할 수 있다.")
    @Test
    void findById() {
        // given
        LocalDateTime testLocalDateTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
        boolean isUsed = false;
        MemberCoupon memberCoupon = memberCouponRepository.save(
                MemberCoupon.builder()
                        .isUsed(isUsed)
                        .endDate(testLocalDateTime)
                        .build());
        // when
        MemberCoupon findMemberCoupon = memberCouponRepository.findById(memberCoupon.getId());

        //then
        assertThat(findMemberCoupon).hasFieldOrPropertyWithValue("isUsed", isUsed);
        assertThat(findMemberCoupon).hasFieldOrPropertyWithValue("endDate", testLocalDateTime);
    }

    @DisplayName("MemberCoupon 리스트를 한번에 저장 할 수 있다")
    @Test
    void saveAll() {
        // given

        LocalDateTime localDateTime1 = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
        LocalDateTime localDateTime2 = LocalDateTime.of(2025, 1, 1, 12, 0, 0);
        LocalDateTime localDateTime3 = LocalDateTime.of(2026, 1, 1, 12, 0, 0);

        MemberCoupon memberCoupon1 = memberCouponRepository.save(
                MemberCoupon.builder()
                        .isUsed(false)
                        .endDate(localDateTime1)
                        .build());

        MemberCoupon memberCoupon2 = memberCouponRepository.save(
                MemberCoupon.builder()
                        .isUsed(false)
                        .endDate(localDateTime2)
                        .build());

        MemberCoupon memberCoupon3 = memberCouponRepository.save(
                MemberCoupon.builder()
                        .isUsed(false)
                        .endDate(localDateTime3)
                        .build());

        // when
        memberCouponRepository.saveAll(List.of(memberCoupon1, memberCoupon2, memberCoupon3));
        List<MemberCoupon> memberCoupons = memberCouponRepository.findAll();

        //then
        assertThat(memberCoupons)
                .extracting(mc -> mc.getIsUsed(), mc -> mc.getEndDate())
                .contains(Tuple.tuple(false, localDateTime1),
                        Tuple.tuple(false, localDateTime2),
                        Tuple.tuple(false, localDateTime3));
    }

    @DisplayName("memberId 와 couponId 로 MemberCoupon 엔티티를 조회할 수 있다")
    @Test
    void findByMemberIdAndCouponId() {
        // given
        Member member = Member.builder().id(1L).build();
        Coupon coupon = Coupon.builder().id(1L).build();
        memberCouponRepository.save(MemberCoupon.builder().coupon(coupon).member(member).build());

        // when
        MemberCoupon memberCoupon = memberCouponRepository.findByMemberIdAndCouponId(member.getId(), coupon.getId());

        //then
        assertThat(memberCoupon)
                .extracting(MemberCoupon::getMember, MemberCoupon::getCoupon)
                .contains(member, coupon);
    }

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

        memberCouponRepository.saveAll(List.of(memberCoupon1, memberCoupon2, memberCoupon3));

        // when
        List<MemberCoupon> memberCoupons = memberCouponRepository.findByMemberId(member.getId());

        //then
        assertThat(memberCoupons).hasSize(3);
        assertThat(memberCoupons).extracting(
                mc -> mc.getMember().getName(),
                        mc -> mc.getCoupon().getPrice())
                .contains(Tuple.tuple("홍길동", 1000L),
                        Tuple.tuple("홍길동", 2000L),
                        Tuple.tuple("홍길동", 3000L));
    }
}
