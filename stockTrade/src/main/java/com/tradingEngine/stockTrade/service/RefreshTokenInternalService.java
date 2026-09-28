package com.tradingEngine.stockTrade.service;

import com.tradingEngine.stockTrade.DTOs.RedisDTOs.RefreshTokenRedisDTO;
import com.tradingEngine.stockTrade.JPARepository.RefreshTokenJPARepository;
import com.tradingEngine.stockTrade.JPARepository.UserRepositoryJPA;
import com.tradingEngine.stockTrade.Redis.RedisRefreshTokenService;
import com.tradingEngine.stockTrade.exception.RefreshTokenNotFound;
import com.tradingEngine.stockTrade.model.RefreshToken;
import com.tradingEngine.stockTrade.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenInternalService {

    private final RefreshTokenJPARepository refreshTokenJPARepository;
    private final UserRepositoryJPA userRepositoryJPA;
    private final RedisRefreshTokenService  redisRefreshTokenService;


    // refreshToken Days
    private static final Long REFRESH_TOKEN_VALIDITY_DAYS = 7L;


    // create RefreshToken
    @Transactional
    public RefreshTokenRedisDTO createRefreshToken(String username) {
        log.info("Creating Refresh Token for username: {}", username);

        // check user
        User user = userRepositoryJPA.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));


        //build
        Instant expiryDays = Instant.now().plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS);

        // create raw refresh token to send user
        String rawToken = UUID.randomUUID().toString();

        // create refresh token to save DB and Redis
        String hashToken = hashToken(rawToken);

        // build and save that data
        RefreshToken refreshTokenBuild = RefreshToken.builder()
                .user(user)
                .token(hashToken)
                .expiryDate(expiryDays)
                .build();

        // save in DB
        RefreshToken savedToken = refreshTokenJPARepository.save(refreshTokenBuild);

        // map to redis Db
        RefreshTokenRedisDTO redisToken = mapToRedisDTO(savedToken);

        // save in redis
        redisRefreshTokenService.saveRefreshToken(redisToken);
        // 5. Client/Controller ko Return karne se pehle DTO me RAW token set karein
        redisToken.setToken(rawToken);
        return redisToken;
    }


    @Transactional
    public RefreshTokenRedisDTO tokenRotation(RefreshTokenRedisDTO refreshTokenRedisDTO) {

        String oldRawToken = refreshTokenRedisDTO.getToken();
        String oldHashToken = hashToken(oldRawToken);

        // check is valid or not
        expiryCheckToRefreshToken(refreshTokenRedisDTO);

        // delete from redis
        redisRefreshTokenService.deleteFromRedis(refreshTokenRedisDTO.getToken());

        // fetch from DB
        RefreshToken existingToken = (RefreshToken) refreshTokenJPARepository.findByToken(oldHashToken)
                .orElseThrow(() -> new RefreshTokenNotFound("RefreshToken not found in Database"));

        // Naya Raw Token aur Naya Hash
        String newRawToken = UUID.randomUUID().toString();
        String newHashToken = hashToken(newRawToken);

        // add new data in oldToken and update that
        Instant newExpiryTime = Instant.now().plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS);
        existingToken.setToken(newHashToken);
        existingToken.setExpiryDate(newExpiryTime);

        RefreshToken saved = refreshTokenJPARepository.save(existingToken);

        // Redis DTO me naya Hash save
        RefreshTokenRedisDTO
                refreshTokenRedisDTO1 = mapToRedisDTO(saved);

        redisRefreshTokenService.saveRefreshToken(refreshTokenRedisDTO1);
        log.info("Refresh Token rotated successfully for user: {}", refreshTokenRedisDTO1.getUsername());

        refreshTokenRedisDTO1.setToken(newRawToken);
        return refreshTokenRedisDTO1;

    }

    @Transactional
    public void expiryCheckToRefreshToken(RefreshTokenRedisDTO refreshToken) {
        if (refreshToken.getExpiry().isBefore(Instant.now())) {
            //Delete From Redis
            redisRefreshTokenService.deleteFromRedis(refreshToken.getToken());
            // Delete from DB
            refreshTokenJPARepository.deleteByToken(refreshToken.getToken());
        }
    }

    //deleteRefreshToken
    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        String hashToken = hashToken(refreshToken);
        log.info("Revoking Refresh Token");


        // delete token from redis
        redisRefreshTokenService.deleteFromRedis(hashToken);
        // delete token from repo
        refreshTokenJPARepository.deleteByToken(hashToken);
    }


    @Transactional
    public RefreshTokenRedisDTO getTokenFromRedisOrDB(String refreshToken) {

        String hashToken = hashToken(refreshToken);

        // first check in DB
        RefreshTokenRedisDTO refreshTokenFromRedis = redisRefreshTokenService.getRefreshTokenFromRedis(refreshToken);

        if (refreshTokenFromRedis != null) {
            log.info("RefreshToken Hit from Redis");
            // Returning DTO with rawToken so downstream functions keep raw value if needed
            refreshTokenFromRedis.setToken(hashToken);
            return refreshTokenFromRedis;
        }

        log.info("RefreshToken Miss -> Fetching from Database");
        // if token not in redis then fetch from DB
         RefreshToken refreshToken1 = (RefreshToken) refreshTokenJPARepository.findByToken(hashToken)
                 .orElseThrow(() -> new RefreshTokenNotFound("Refresh token not found"));

        RefreshTokenRedisDTO refreshTokenRedisDTO = mapToRedisDTO(refreshToken1);

        expiryCheckToRefreshToken(refreshTokenRedisDTO);

        redisRefreshTokenService.saveRefreshToken(refreshTokenRedisDTO);

        // send user
        refreshTokenRedisDTO.setToken(refreshToken);
        return refreshTokenRedisDTO;
    }




    private RefreshTokenRedisDTO mapToRedisDTO(RefreshToken refreshToken) {
        return new RefreshTokenRedisDTO(
                refreshToken.getId(),
                refreshToken.getToken(),
                refreshToken.getExpiryDate(),
                refreshToken.getUser().getUsername()
        );
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
