package com.objects.marketbridge.common.security.service;

import com.objects.marketbridge.common.security.constants.SecurityConst;
import com.objects.marketbridge.common.security.constants.SecurityErrConst;
import com.objects.marketbridge.common.security.domain.CustomUserDetails;
import com.objects.marketbridge.common.security.domain.TokenType;
import com.objects.marketbridge.common.security.dto.JwtTokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.objects.marketbridge.common.security.constants.SecurityConst.*;
import static com.objects.marketbridge.common.security.domain.TokenType.AccessToken;
import static com.objects.marketbridge.common.security.domain.TokenType.RefreshToken;

/**
 * Spring security와 JWT 토큰을 사용하여 인증과 권한 부여를 처리하는 클래스
 */
@Slf4j
@Component
public class JwtTokenProviderImpl implements JwtTokenProvider {

    private final Key KEY;
    private final JwtTokenService jwtTokenService;

    public JwtTokenProviderImpl(@Value(SecurityConst.SECRET_KEY) String secretKey, @Autowired JwtTokenService jwtTokenService
    ) {
        this.jwtTokenService = jwtTokenService;
        // base64 문자열을 byte 배열로 변환
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        // jjwt라이브러리의 Keys 클래스는 토큰 서명 및 검증에 필요한 키를 생성하는 다양한 메서드를 제공함.
        // hmacShaKeyFor 메서드는 HMAC SHA-256키를 생성 함.
        this.KEY = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * AccessToken과 RefreshToken을 생성함.
     */
    @Override
    public JwtTokenDto generateToken(CustomUserDetails principal) {
        Long userId = principal.getId();
        String role = getRole(principal.getAuthorities());

        String accessToken = issueToken(userId, role, ACCESS_TOKEN_EXPIRE_TIME);
        String refreshToken = issueToken(userId, role, REFRESH_TOKEN_EXPIRE_TIME);

        saveTokenToRedis(userId, accessToken, refreshToken);

        return createJwtTokenDto(accessToken, refreshToken);
    }

    /**
     * 사용자의 권한 문자열을 가져오는 메서드.
     */
    @Override
    public String getRole(Collection<? extends GrantedAuthority> authorities) {
        /*
         * authorities는 부여된 권한 목록을 반환. GrantedAuthority의 컬렉션.
         * GrantedAuthority는 권한 정보를 나타내는 인터페이스
         * getAuthority() 메서드는 권한을 나타내는 문자열을 반환.
         * --> Spring security의 User객체(UserDetails의 구현체)의 roles 메서드에 의해 ROLE_ prefix가 없을 경우 붙여 줌. "USER -> ROLE_USER"
         * ----> UserDetails는 사용자정보를 나타내는 인터페이스
         * ----> User는 UserDetails의 구현체로 사용자의 계정, 비밀번호 만료나, 계정이 잠겨있는지에 대한 정보를 가지고 있음.
         */
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    /**
     * 토큰으로 인증 정보 반환
     * @param token 유효성 검증이 통과된 토큰
     * @return Authentication 인터페이스는 사용자의 신원 정보와 권한 정보를 가지고 있음.
     */
    @Override
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        // claims.get(SecurityConst.AUTH) 토큰 정보에 있는 권한 리스트를 가져온다.
        if (claims.get(SecurityConst.AUTH) == null) {
            throw new RuntimeException(SecurityErrConst.PERMISSION_TOKEN_ERR);

        }

        // claim에서 권한 정보 추출
        Collection<? extends GrantedAuthority> authorities = extractAuthoritiesFromToken(claims);
        List<String> roles = new ArrayList<>(Arrays.asList(claims.get(SecurityConst.AUTH, String.class).split(",")));
        Long userId = Long.parseLong(claims.getSubject());

        CustomUserDetails principal = new CustomUserDetails(userId, null,null, roles);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 토큰의 유효성을 검증하는 메서드
     */
    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;

        } catch (SecurityException e) {
            log.info(e.getMessage());
            throw new SecurityException(e.getMessage());
        } catch (MalformedJwtException e) {
            log.info(e.getMessage());
            throw new MalformedJwtException(e.getMessage());
        } catch (ExpiredJwtException e) {
            log.info(e.getMessage());
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info(e.getMessage());
            throw new UnsupportedJwtException(e.getMessage());
        } catch (IllegalStateException e) {
            log.info(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * 토근을 발급하는 메서드
     */
    @Override
    public String issueToken(Long userId, String role, Long expiration) {
        /*
         * .setSubject() jwt의 주제(주로 name)
         * .claim() token 안에 포함되는 정보 - iss(발급자), exp(만료시간), sub(주제) 등등... 이외에도 name, role 등이 있음.
         * .setIssuedAt 발급시간
         * .setExpiration() 만료시간
         * .signWith() JWT 서명에 사용되는 비밀키
         * .compact() JWT를 문자열로 반환
         */
        long expirationTime = Instant.now().toEpochMilli() + expiration;
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim(SecurityConst.AUTH, role)
                .setExpiration(new Date(expirationTime))
                .signWith(KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 로그아웃 시 토큰을 레디스에서 삭제하는 메서드
     */
    @Override
    public void deleteToken(Long userId) {
        jwtTokenService.deleteToken(userId, AccessToken);
        jwtTokenService.deleteToken(userId, RefreshToken);
    }

    /**
     * 레디스에 등록된 토큰인지 확인하는 메서드
     */
    @Override
    public boolean checkTokenRegistration(String uri, String token, Authentication authentication) {

        Long userId = getCustomUserDetails(authentication).getId();
        TokenType tokenType = uri.equals(RE_ISSUE_URI) ? RefreshToken : AccessToken;

        boolean isExists = jwtTokenService.isExistToken(userId, tokenType, token);;

        if (!isExists)  {
            deleteToken(userId);
            return false;
        }

        return true;
    }

    /* --------------------------------------------------- private --------------------------------------------------- */

    /**
     * 인증 사용자를 가져오는 메서드
     */
    private CustomUserDetails getCustomUserDetails(Authentication authentication) {
        return (CustomUserDetails) authentication.getPrincipal();
    }

    /**
     * 토큰에서 권한정보를 추출하여 권한 문자열 컬렉션으로 만드는 메서드
     * @param claims 토큰의 정보
     * @return GrantedAuthority 권한 정보 컬렉션
     */
    private Collection<? extends GrantedAuthority> extractAuthoritiesFromToken(Claims claims) {
        return Arrays.stream(claims.get(SecurityConst.AUTH).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * 토큰을 복호화(해독)하여 claim(토큰정보)를 추출
     * @param token 토큰
     * @return Claims 토큰 정보 객체
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * 토큰으로 JwtTokenDto 객체 생성
     */
    private JwtTokenDto createJwtTokenDto(String accessToken, String refreshToken) {
        return JwtTokenDto.builder()
                .grantType(SecurityConst.BEARER)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 레디스에 토큰 저장
     */
    private void saveTokenToRedis(Long userId, String accessToken, String refreshToken) {
        jwtTokenService.saveToken(userId, AccessToken, accessToken, ACCESS_TOKEN_EXPIRE_TIME);
        jwtTokenService.saveToken(userId, RefreshToken, refreshToken, REFRESH_TOKEN_EXPIRE_TIME);
    }

}