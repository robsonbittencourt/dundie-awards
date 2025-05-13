package com.ninjaone.dundie_awards.infrastructure.repository.employee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.time.Duration.ofMinutes;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@Component
class DundiesCache {

    private static final Logger log = LoggerFactory.getLogger(DundiesCache.class);

    private static final String CACHE_NAME = "totalDundies";

    @Value("${app.dundie-count-cache-minutes}")
    private int cacheTime;

    @Autowired
    private StringRedisTemplate redisTemplate;

    Optional<Long> getCounter() {
        try {
            String value = redisTemplate.opsForValue().get(CACHE_NAME);
            return ofNullable(value).map(Long::parseLong);
        } catch (RedisConnectionFailureException ex) {
            warningLog();
            return empty();
        }
    }

    void resetCounter() {
        try {
            redisTemplate.delete(CACHE_NAME);
        } catch (RedisConnectionFailureException ex) {
            warningLog();
        }
    }

    void updateCounter(long value) {
        try {
            redisTemplate.opsForValue().set(CACHE_NAME, String.valueOf(value), ofMinutes(cacheTime));
        } catch (RedisConnectionFailureException ex) {
            warningLog();
        }
    }

    private void warningLog() {
        log.warn("Redis is down.");
    }

}
