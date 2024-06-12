package com.objects.marketbridge.common.security.service;

import com.objects.marketbridge.common.security.domain.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    private ValueOperations<String, String> getValueOperations() { return redisTemplate.opsForValue(); }

    public void saveToken(Long userId, TokenType tokenType, String token, long expirationTime) {
        getValueOperations().set(userId+":"+tokenType.toString(), token, expirationTime, TimeUnit.MILLISECONDS);
    }

    public void deleteToken(Long userId, TokenType tokenType) {
        redisTemplate.delete(userId+":"+tokenType.toString());
    }

    public boolean isExistToken(Long userId, TokenType tokenType, String token) {
        String value = getValueOperations().get(userId+":"+tokenType.toString());
        return token.equals(value);
    }
}
