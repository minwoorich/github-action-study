package com.objects.marketbridge.common.security.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtTokenDto {

    private String grantType;

    private String accessToken;

    private String refreshToken;

    @Builder
    public JwtTokenDto(String grantType, String accessToken, String refreshToken) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
