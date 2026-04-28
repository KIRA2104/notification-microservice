package com.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${rate.limit.max-requests:100}")
    private int maxRequests;

    @Value("${rate.limit.window-seconds:60}")
    private int windowSeconds;

    /**
     * Returns true if the app is within the rate limit, false if exceeded.
     */
    public boolean isAllowed(String appName) {
        String key = "rate_limit:" + appName;

        Long currentCount = redisTemplate.opsForValue().increment(key);

        // Set expiry only on the first request in the window
        if (currentCount != null && currentCount == 1) {
            redisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS);
        }

        if (currentCount != null && currentCount > maxRequests) {
            log.warn("Rate limit exceeded for app: {} | Count: {}", appName, currentCount);
            return false;
        }

        return true;
    }

    /**
     * Returns the current request count for an app (for dashboard display).
     */
    public long getCurrentCount(String appName) {
        String key = "rate_limit:" + appName;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0;
    }
}
