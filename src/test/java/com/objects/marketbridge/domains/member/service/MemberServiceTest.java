package com.objects.marketbridge.domains.member.service;

import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.domain.MembershipType;
import com.objects.marketbridge.domains.member.service.MemberService;
import com.objects.marketbridge.domains.member.mock.FakeMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.* ;

@Slf4j
//@SpringBootTest
//@Transactional
@ActiveProfiles("test")
class MemberServiceTest {


    MemberService memberService;

    @BeforeEach
    void init() {
        FakeMemberRepository fakeMemberRepository = new FakeMemberRepository();
        memberService = MemberService.builder()
                .memberRepository(fakeMemberRepository)
                .build();

        Member member1 = Member.builder()
                .id(1L)
                .email("member1@example.com")
                .name("테스트1")
                .password("03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4")
                .phoneNo("01000000001")
                .isAgree(true)
                .isAlert(true)
                .membership(MembershipType.WOW.toString())
                .build();

        Member member2 = Member.builder()
                .id(2L)
                .email("member2@example.com")
                .name("테스트2")
                .password("03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4")
                .phoneNo("01000000002")
                .isAgree(true)
                .isAlert(true)
                .membership(MembershipType.WOW.toString())
                .build();

        fakeMemberRepository.save(member1);
        fakeMemberRepository.save(member2);
    }

    @Test
    @DisplayName("이메일이 중복이 되었으면 true를 반환한다")
    public void checkDuplicateEmailTrue() {
        //given
        String email = "member1@example.com";

        //when
        boolean isDuplicateEmail = (memberService.isDuplicateEmail(email)).isChecked();

        //then
        assertThat(isDuplicateEmail).isTrue();
    }

    @Test
    @DisplayName("이메일이 중복이 되지 않았으면 false를 반환한다")
    public void checkDuplicateEmailFalse() {
        //given
        String email = "member11@example.com";

        //when
        boolean isDuplicateEmail = (memberService.isDuplicateEmail(email)).isChecked();

        //then
        assertThat(isDuplicateEmail).isFalse();
    }

    @Test
    @Transactional
    @DisplayName("멤버십 변경 API")
    public void testUpdateWowMemberShip(){
//        //given
//        Member member = memberRepository.findByEmail("iiwisii@naver.com").orElseThrow(() -> new EntityNotFoundException("엔티티가 존재하지 않습니다"));
//        String memberShipData = "BASIC";
//        //when
//        memberService.changeMemberShip(member.getId());
//        //then
//        assertThat(member.getMembership()).isEqualTo(memberShipData);
    }


}