package com.ninjaone.dundie_awards.infrastructure.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class AwardsRedisCache {

    private static final String CACHE_NAME = "totalAwards";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Long increment() {
        return redisTemplate.opsForValue().increment(CACHE_NAME);
    }

    public Long getCounter() {
        String value = redisTemplate.opsForValue().get(CACHE_NAME);
        return value != null ? Long.parseLong(value) : 0L;
    }

    public void resetCounter() {
        redisTemplate.delete(CACHE_NAME);
    }

    public void updateCounter(long value) {
        redisTemplate.opsForValue().set(CACHE_NAME, String.valueOf(value));
    }
}
