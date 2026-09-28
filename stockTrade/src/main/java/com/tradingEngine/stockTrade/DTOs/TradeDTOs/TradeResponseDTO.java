package com.tradingEngine.stockTrade.DTOs.TradeDTOs;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@Getter
@Setter
public class TradeResponseDTO implements Serializable {

    private Long id;
    private Long buyerId;
    private Long sellerId;
    private int quantity;
    private BigDecimal price;
    private String symbol;
    private LocalDateTime tradeTime;

    public TradeResponseDTO() {}


}
