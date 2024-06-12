package com.objects.marketbridge.common.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.objects.marketbridge.common.security.constants.SecurityConst;
import com.objects.marketbridge.common.security.constants.SecurityErrConst;
import com.objects.marketbridge.common.security.service.JwtTokenProvider;
import com.objects.marketbridge.domains.member.dto.SignInDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.objects.marketbridge.common.security.constants.SecurityConst.SIGN_IN_URI;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * 모든 요청에서 토큰이 있는 지 확인하여 인증 처리
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (requestURI.equals(SIGN_IN_URI)) {
            SignInDto signInDto = objectMapper.readValue(extractRequestBody(request), SignInDto.class);
            registerAuthentication(signInDto.getEmail(), signInDto.getPassword());

        } else {
            // resolveToken() request 객체에서 token을 추출
            String token = resolveToken(request);

            // 토큰이 존재하고, 검증상태가 유효하면
            if (token != null && jwtTokenProvider.validateToken(token)) {

                // token을 해석하여 사용자 정보를 가져옴
                Authentication authentication = jwtTokenProvider.getAuthentication(token);

                // 유효하지만 레디스에 저장이 되지 않은 토큰이면 에러(재발급 요청이면 refreshtoken을 검증);
                if (!jwtTokenProvider.checkTokenRegistration(requestURI, token, authentication)) {
                    throw new BadCredentialsException(SecurityErrConst.AUTHENTICATION_TOKEN_ERR);
                }

                // spring security에서 현재 스레드의 보안 컨텍스트에 새로운 인증 정보를 설정
                setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractRequestBody(HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
    }

    private void registerAuthentication(String email, String password) throws IOException {
        //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(email, password, null);

        //token에 담은 검증을 위한 AuthenticationManager로 전달
        Authentication authentication = authenticationManager.authenticate(authToken);

        setAuthentication(authentication);

    }

    private void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * request의 Header의 Authorization에서 bearerToken을 가져와 bearer prefix를 제거하고 토큰만 반환
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(SecurityConst.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(SecurityConst.BEARER)) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }
}
