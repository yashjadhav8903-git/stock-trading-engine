//package com.tradingEngine.stockTrade.Redis;
//
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//public class RedisPublisher {
//    // publisher class
//
//    private final RedisTemplate<String,Object> redisTemplate;
//
//    public RedisPublisher(RedisTemplate<String,Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    public void publish(String channel,Object message){
//        redisTemplate.convertAndSend(channel, message);
//    }
//}
