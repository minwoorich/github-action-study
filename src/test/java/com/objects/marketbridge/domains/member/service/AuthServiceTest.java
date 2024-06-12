package com.objects.marketbridge.domains.member.service;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.domain.MembershipType;
import com.objects.marketbridge.domains.member.dto.SignUpDto;
import com.objects.marketbridge.domains.member.service.AuthService;
import com.objects.marketbridge.domains.member.service.MemberService;
import com.objects.marketbridge.domains.member.mock.FakeAuthRepository;

import com.objects.marketbridge.domains.member.mock.FakeMemberRepository;
import com.objects.marketbridge.domains.member.mock.FakePasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
//@SpringBootTest
//@Transactional
@ActiveProfiles("test")
class AuthServiceTest {

    AuthService authService;
    MemberService memberService;

    @BeforeEach
    void init() {
        FakeMemberRepository fakeMemberRepository = new FakeMemberRepository();
        memberService = MemberService.builder()
                .memberRepository(fakeMemberRepository)
                .build();

        FakeAuthRepository fakeAuthRepository = new FakeAuthRepository();
        authService = AuthService.builder()
                .authRepository(fakeAuthRepository)
                .memberService(memberService)
                .passwordEncoder(new FakePasswordEncoder())
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
        fakeAuthRepository.addData(member1);
        fakeAuthRepository.addData(member2);
    }

    @Test
    @DisplayName("회원가입 완료")
    public void signUp_new() throws BadRequestException {
        //given
        SignUpDto signUpDto = SignUpDto.builder()
                .email("member3@example.com")
                .name("테스트3")
                .password("03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4")
                .phoneNo("01000000003")
                .isAgree(true)
                .build();

        //when
        authService.signUp(signUpDto);

        //then
        //회원 가입이 됬으면 저장된 이메일이 있는지 확인.
        boolean duplicateEmail = memberService.isDuplicateEmail(signUpDto.getEmail()).isChecked();
        assertThat(duplicateEmail).isTrue();
    }

    @Test
    @DisplayName("중복 된 이메일로 회원가입 시도시 Exception 발생")
    public void signUp_dup() {
        //given
        String email = "member1@example.com";
        SignUpDto signUpDto = SignUpDto.builder().email(email).build();

        //when

        //then
        assertThatThrownBy(() -> {
            authService.signUp(signUpDto);
        }).isInstanceOf(CustomLogicException.class);
    }

}