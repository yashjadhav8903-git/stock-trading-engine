package com.tradingEngine.stockTrade.Redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String LTP_KEY_PREFIX = "LTP:";

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 1. Cache Last Traded Price (LTP)
    public void updateLTP(String symbol, BigDecimal price) {
        String key = LTP_KEY_PREFIX + symbol.toUpperCase();
        redisTemplate.opsForValue().set(key,
                price.toString()
                , 2, TimeUnit.HOURS);
    }


    // 2 get Cached LTP
    public String getLTP(String symbol) {
        String key = LTP_KEY_PREFIX + symbol.toUpperCase();
        Object price = redisTemplate.opsForValue().get(key);
        return price != null ? price.toString() : "0.00";
    }
}
