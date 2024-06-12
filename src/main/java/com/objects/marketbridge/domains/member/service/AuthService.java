package com.objects.marketbridge.domains.member.service;

import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.domains.member.domain.Member;
import com.objects.marketbridge.domains.member.dto.SignUpDto;
import com.objects.marketbridge.domains.member.service.port.AuthRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.DUPLICATE_ERROR;

@Slf4j
@Service
@Builder
@RequiredArgsConstructor
public class AuthService {
    private final MemberService memberService;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(SignUpDto signUpDto) {

        boolean isDuplicateEmail = memberService.isDuplicateEmail(signUpDto.getEmail()).isChecked();

        if (isDuplicateEmail) throw CustomLogicException.createBadRequestError(DUPLICATE_ERROR);

        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
        Member member = signUpDto.toEntity(encodedPassword);

        memberService.save(member);
    }
}
