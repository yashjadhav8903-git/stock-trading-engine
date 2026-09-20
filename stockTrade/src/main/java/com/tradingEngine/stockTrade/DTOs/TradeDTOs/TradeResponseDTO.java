package com.tradingEngine.stockTrade.DTOs.TradeDTOs;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class TradeResponseDTO implements Serializable {

    private Long id;
    private Long buyerId;
    private Long sellerId;
    private int quantity;
    private BigDecimal price;
    private String symbol;
    private LocalDateTime tradeTime;

    public TradeResponseDTO() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public LocalDateTime getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(LocalDateTime tradeTime) {
        this.tradeTime = tradeTime;
    }
}
