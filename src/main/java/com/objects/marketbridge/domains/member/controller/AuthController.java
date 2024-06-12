package com.objects.marketbridge.domains.member.controller;

import com.objects.marketbridge.common.responseobj.ApiResponse;
import com.objects.marketbridge.common.security.annotation.AuthMemberId;
import com.objects.marketbridge.common.security.annotation.GetAuthentication;
import com.objects.marketbridge.common.security.domain.CustomUserDetails;
import com.objects.marketbridge.common.security.dto.JwtTokenDto;
import com.objects.marketbridge.common.security.service.JwtTokenProvider;
import com.objects.marketbridge.domains.member.constant.MemberConst;
import com.objects.marketbridge.domains.member.dto.SignUpDto;
import com.objects.marketbridge.domains.member.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> signUp(@Valid @RequestBody SignUpDto signUpDto) {
        authService.signUp(signUpDto);
        return ApiResponse.create();
    }

    @PostMapping("/sign-in")
    public ApiResponse<JwtTokenDto> signIn(@GetAuthentication CustomUserDetails principal) {
        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateToken(principal);
        return ApiResponse.ok(jwtTokenDto);
    }

    @DeleteMapping("/sign-out")
    public ApiResponse<Void> signOut(@AuthMemberId Long memberId) {
        jwtTokenProvider.deleteToken(memberId);
        return ApiResponse.of(HttpStatus.OK, MemberConst.SIGNED_OUT_SUCCESSFULLY, null);
    }

    @PutMapping("/re-issue")
    public ApiResponse<JwtTokenDto> reIssueToken(@GetAuthentication CustomUserDetails principal) {
        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateToken(principal);
        return ApiResponse.ok(jwtTokenDto);
    }
}
