package com.objects.marketbridge.common.security.infra;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash("JwtToken")
public class JwtTokenRedisEntity {
    @Id
    private final String email;

    @Indexed
    private final String token;

    @Builder
    public JwtTokenRedisEntity(String email, String token) {
        this.email = email;
        this.token = token;
    }
}
