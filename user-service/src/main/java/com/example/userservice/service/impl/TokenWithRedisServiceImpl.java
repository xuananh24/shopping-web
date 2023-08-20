package com.example.userservice.service.impl;

import com.example.userservice.service.TokenWithRedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenWithRedisServiceImpl implements TokenWithRedisService {
    private final RedisTemplate redisTemplate;

    public TokenWithRedisServiceImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveToken(String refreshToken, String accessToken) {
        redisTemplate.opsForList().leftPush(refreshToken, accessToken);
    }

    @Override
    public List<String> getToken(String refreshToken) {
        return redisTemplate.opsForList().range(refreshToken, 0, -1);
    }
}
