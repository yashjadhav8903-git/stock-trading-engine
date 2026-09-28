package com.tradingEngine.stockTrade.Redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingEngine.stockTrade.DTOs.RedisDTOs.RefreshTokenRedisDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RedisRefreshTokenService {

    public final RedisTemplate<String,Object> redisTemplate;
    private final ObjectMapper objectMapper;
    public final String REDIS_PREFIX = "REFRESH_TOKEN:";



    //get Key
    private String getKey(String token){
        return REDIS_PREFIX + token;
    }


    // save in redis
    @Transactional
    public void saveRefreshToken(RefreshTokenRedisDTO  refreshTokenRedisDTO){

        String key = getKey(refreshTokenRedisDTO.getToken());
        Duration duration = Duration.between(Instant.now(),refreshTokenRedisDTO.getExpiry());

        if(!duration.isNegative() && !duration.isZero()) {
            redisTemplate.opsForValue()
                    .set(
                            key,
                            refreshTokenRedisDTO,
                            duration
                    );
        }
    }

    // getToken from redis
    @Transactional
    public RefreshTokenRedisDTO getRefreshTokenFromRedis(String token){

        Object object = redisTemplate.opsForValue()
                .get(getKey(token));

        if(object == null){
            return null;
        }

        if (object instanceof RefreshTokenRedisDTO) {
            return (RefreshTokenRedisDTO) object;
        }

        return objectMapper.convertValue(object, RefreshTokenRedisDTO.class);

    }


    // delete from redis
    @Transactional
    public void deleteFromRedis(String token){
        String key = getKey(token);
        redisTemplate.delete(key);
    }
}
