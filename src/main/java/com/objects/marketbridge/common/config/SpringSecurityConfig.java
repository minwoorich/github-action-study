package com.objects.marketbridge.common.config;

import com.objects.marketbridge.common.security.filter.ExceptionHandlerFilter;
import com.objects.marketbridge.common.security.filter.JwtAuthenticationFilter;
import com.objects.marketbridge.common.security.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 요청에 대한 보안 설정
     * HttpSecurity Spring Security의 클래스 웹 기반 보안을 설정하는데 사용
     * SecurityFilterChain은 특정 경로에 대한 접근 권한, 인증 정책, 필터 추가를 통해 토큰 기반의 인증을 처리
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        /* .httpBasic() 비활성화 - 사용자의 인증 정보가 JWT 토큰에 포함되어 있기 때문에 별도의 인증방식이 필요없음.
         * .csrf() 비활성화 - 주로 세션 기반의 인증에서 필요하기 때문에 JWT 토큰의 경우 토큰 자체에 서명이 포함되어 있어서 이를 통해 위변조방지가 가능.
         * .sessionManagement() - JWT 토큰 자체에 인증정보를 가지고 있어서 서버는 세션을 유지할 필요가 없음.
         * .authorizeHttpRequests - 요청들에 대한 인가 규칙
         * --> .requestMatchers(url1, url2....).permitAll() 인증없이 허용
         * --> .requestMatchers(url1, url2....).hasAuthority("USER") USER 역할의 사용자만 허용
         * --> .anyRequest().authenticated() 규칙이 명시되지 않는 모든 요청은 인증된 사용자만 허용
         * .addFilterBefore - 사용자 지정으로 정의된 JwtAuthenticationFilter 필터를 추가
         * UsernamePasswordAuthenticationFilter 필터 전에 실행되도록 설정
         * --> spring security에서 제공한 사용자가 이름과 비밀번호로 인증을 시도하는데 사용되는 필터
         * --> HTTP POST 요청에 반응, 해당 요청에서 사용자 이름과 비번을 추출하여 UsernamePasswordAuthenticationToken 객체 생성
         * --> AuthenticationManager에 추출된 토큰을 전달하여 사용자 인증, 인증 성공, 실패등을 처리
         * ----> AuthenticationManager는 사용자의 인증 정보로 실제 사용자를 인증하는 역할을 수행
         */
        return httpSecurity
                .httpBasic(HttpBasicConfigurer::disable)
                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowedOriginPatterns(Collections.singletonList("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(configurer-> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/auth/re-issue","/auth/sign-out").hasAuthority("USER"))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration), jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ExceptionHandlerFilter(), JwtAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * @return PasswordEncoder 사용자의 비밀번호를 안전하게 저장하고 검증하기 위한 인터페이스.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        /*
         * 여러 해싱 알고리즘 중 하나를 선택해서 사용자의 비밀번호를 검증.(다양한 해시 알고리즘 선택 가능)
         * 비밀번호가 저장될 때 해당 알고리즘에 따라 다르게 해싱됨.
         * Default로 Bcrypt를 사용.
         */
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
