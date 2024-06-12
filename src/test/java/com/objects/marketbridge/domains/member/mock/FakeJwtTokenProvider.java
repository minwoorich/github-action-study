package com.objects.marketbridge.domains.member.mock;

import com.objects.marketbridge.common.security.domain.CustomUserDetails;
import com.objects.marketbridge.common.security.dto.JwtTokenDto;
import com.objects.marketbridge.common.security.service.JwtTokenProvider;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class FakeJwtTokenProvider implements JwtTokenProvider {
    @Override
    public JwtTokenDto generateToken(CustomUserDetails principal) {
        return JwtTokenDto.builder().grantType("bearer")
                .accessToken("AccessToken")
                .refreshToken("RefreshToken")
                .build();
    }

    @Override
    public String getRole(Collection<? extends GrantedAuthority> authorities) {
        return null;
    }

    @Override
    public Authentication getAuthentication(String token) {
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        return false;
    }

    @Override
    public String issueToken(Long userId, String role, Long expiration) {
        return null;
    }

    @Override
    public void deleteToken(Long userId) {

    }

    @Override
    public boolean checkTokenRegistration(String uri, String token, Authentication authentication) {
        return false;
    }
}
