package com.objects.marketbridge.domains.member.domain;

import com.objects.marketbridge.common.utils.DateTimeHolder;
import com.objects.marketbridge.domains.coupon.domain.Coupon;
import com.objects.marketbridge.domains.coupon.domain.MemberCoupon;
import com.objects.marketbridge.domains.member.mock.FakeMemberRepository;
import com.objects.marketbridge.domains.member.service.port.MemberRepository;
import com.objects.marketbridge.domains.order.mock.TestDateTimeHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MemberCouponTest {

    MemberRepository memberRepository = new FakeMemberRepository();

    @AfterEach
    void clear() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("MemberCoupon 을 생성 할 수 있다")
    @Test
    void create(){
        //given
        Coupon coupon = Coupon.builder().price(1000L).endDate(LocalDateTime.of(2024, 1, 1, 12, 0, 0)).build();
        Member member = Member.builder().name("홍길동").build();

        //when
        MemberCoupon memberCoupon = MemberCoupon.create(member, coupon);

        //then
        assertThat(memberCoupon).extracting(mc -> mc.getCoupon(), mc -> mc.getMember()).contains(coupon, member);
    }
    @Test
    @DisplayName("사용여부와 사용시간이 초기화 되어야 한다.")
    public void changeUsageInfo1() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 16, 6, 34);
        DateTimeHolder dateTimeHolder = TestDateTimeHolder.builder()
                .now(localDateTime)
                .build();
        MemberCoupon usedCoupon = createMemberCoupon(localDateTime, true);

        // when
        usedCoupon.changeUsageInfo(dateTimeHolder);
    
        // then
        assertThat(usedCoupon).extracting("usedDate", "isUsed")
                .containsExactly(localDateTime, false);
    }

    @Test
    @DisplayName("dateTimeHolder가 null이라면 사용 날짜는 초기화 되어야 한다.")
    public void changeUsageInfo2() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 16, 6, 34);
        MemberCoupon usedCoupon = createMemberCoupon(localDateTime, true);

        // when
        usedCoupon.changeUsageInfo((DateTimeHolder) null);

        // then
        assertThat(usedCoupon).extracting("usedDate", "isUsed")
                .containsExactly(null, false);
    }

    private static MemberCoupon createMemberCoupon(LocalDateTime localDateTime, boolean isUsed) {
        return MemberCoupon.builder()
                .usedDate(localDateTime)
                .isUsed(isUsed)
                .build();
    }

    @DisplayName("MemberCoupon 을 특정 회원을 기준으로 필터링 할 수 있다")
    @Test
    void filterByMemberId(){
        //given
        Member member1 = Member.builder().name("홍길동").build();
        Member member2 = Member.builder().name("김길동").build();
        Member member3 = Member.builder().name("박길동").build();

        MemberCoupon memberCoupon = MemberCoupon.builder().member(member1).build();

        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);
        Member savedMember3 = memberRepository.save(member3);

        //when
        Boolean result1 = memberCoupon.filterByMemberId(savedMember1.getId());
        Boolean result2 = memberCoupon.filterByMemberId(savedMember2.getId());
        Boolean result3 = memberCoupon.filterByMemberId(savedMember3.getId());

        //then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
        assertThat(result3).isFalse();
    }

    @DisplayName("회원이 가지고 있는 쿠폰의 최소 가격을 조회할 수 있다.")
    @Test
    void getMinimumPrice(){
        //given
        MemberCoupon memberCoupon = MemberCoupon.builder().build();
        Coupon coupon = Coupon.builder().minimumPrice(10000L).build();
        coupon.addMemberCoupon(memberCoupon);

        //when
        Long minimumPrice = memberCoupon.getMinimumPrice();

        //then
        assertThat(minimumPrice).isEqualTo(10000L);
    }
}