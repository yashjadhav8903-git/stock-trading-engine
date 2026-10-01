package com.tradingEngine.stockTrade.RateLimiter;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    private final RedissonClient redissonClient;

    public RateLimiterService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public boolean isRequestAllowed(String key, Long limit, Long duration, RateIntervalUnit unit){

        // RRateLimiter need key
        RRateLimiter limiter = redissonClient.getRateLimiter("Rate::Limiter:" + key);

        // are instance ke liye
        limiter.trySetRate(RateType.OVERALL, limit, duration, unit);

        // unWanted key clean in redis
        limiter.expireAsync(Duration.ofMinutes(10));

        // ager bucket me token hai toh allowed nhi toh false
        return limiter.tryAcquire(1);
    }
}
