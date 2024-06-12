package com.objects.marketbridge.common.security.service;

import com.objects.marketbridge.common.security.domain.CustomUserDetails;
import com.objects.marketbridge.common.security.dto.JwtTokenDto;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface JwtTokenProvider {

    JwtTokenDto generateToken(CustomUserDetails principal);

    String getRole(Collection<? extends GrantedAuthority> authorities);

    Authentication getAuthentication(String token);

    boolean validateToken(String token);

    String issueToken(Long userId, String role, Long expiration);

    void deleteToken(Long userId);

    boolean checkTokenRegistration(String uri, String token, Authentication authentication);
}
