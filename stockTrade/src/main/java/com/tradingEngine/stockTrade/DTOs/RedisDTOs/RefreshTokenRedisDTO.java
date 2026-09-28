package com.tradingEngine.stockTrade.DTOs.RedisDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;
import java.time.Instant;

@Setter
@Getter
public class RefreshTokenRedisDTO implements Serializable {

    private Long id;
    private String token;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant expiry;
    private String username;

    public RefreshTokenRedisDTO(Long id, String token, Instant expiry, String username) {
        this.id = id;
        this.token = token;
        this.expiry = expiry;
        this.username = username;
    }
    public RefreshTokenRedisDTO(){}

}
