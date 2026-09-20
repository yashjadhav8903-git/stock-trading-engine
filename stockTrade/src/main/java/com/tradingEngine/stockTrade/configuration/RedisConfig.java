package com.tradingEngine.stockTrade.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.tradingEngine.stockTrade.Redis.RedisSubscriber;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 1. Spring Boot 3 Jackson ObjectMapper Setup
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


        // 2. Modern Non-Deprecated Bind
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // 3. Set Key & Value Serializers
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;

    }

//    @Bean
//    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory,
//                                                    RedisSubscriber redisSubscriber) {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(redisConnectionFactory);
//
//        // "ticker.*" pattern wale sabhi channels listen karo
//        container.addMessageListener(redisSubscriber,new PatternTopic("ticker.*"));
//        container.addMessageListener(redisSubscriber, new PatternTopic("orders.*"));
//        return container;
//
//    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        // 1. Spring Boot 3 Jackson ObjectMapper Setup
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.EVERYTHING, // <-- EVERYTHING set karein
                JsonTypeInfo.As.PROPERTY
        );
        // 2. Modern Non-Deprecated Bind
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));  // java object -> JSON format me convert hoga , Redis me readable format me store hoga.


        // 3. Custom Per-Cache TTL Configurations
        // Global Stats and Trades: Frequent Batch Updates ki wajeh se inko fast expire (5-10 sec) karenge
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put("statsRepository", defaultConfig.entryTtl(Duration.ofSeconds(5)));
        cacheConfigurations.put("statsCount", defaultConfig.entryTtl(Duration.ofSeconds(5)));
        cacheConfigurations.put("statsAvg", defaultConfig.entryTtl(Duration.ofSeconds(5)));
        cacheConfigurations.put("topStock", defaultConfig.entryTtl(Duration.ofSeconds(5)));
        cacheConfigurations.put("tradeRecord", defaultConfig.entryTtl(Duration.ofSeconds(5)));
        cacheConfigurations.put("globalTrades", defaultConfig.entryTtl(Duration.ofSeconds(3)));

        // User Trades: Isko thoda zayda TTL de sakte hain (e.g. 30 sec)
        cacheConfigurations.put("userTrades", defaultConfig.entryTtl(Duration.ofSeconds(30)));


        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations) // Custom TTL map register ho gaya!
                .build();
    }
}
