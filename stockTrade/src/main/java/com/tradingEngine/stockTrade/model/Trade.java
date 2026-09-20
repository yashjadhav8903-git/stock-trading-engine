package com.tradingEngine.stockTrade.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Trade {

    private Long id;
    private Long UserId;
    private Long buyerId;
    private Long sellerId;
    private Long StockId;
    private int quantity;
    private BigDecimal price;
    private String symbol;
    private LocalDateTime tradeTime;

    public Long getUserId() {
        return UserId;
    }

    public void setUserId(Long userId) {
        UserId = userId;
    }

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

    public Long getStockId() {
        return StockId;
    }

    public void setStockId(Long stockId) {
        StockId = stockId;
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

    public Trade(Long id, Long userId, Long buyerId, Long sellerId, Long stockId, int quantity, BigDecimal price, String symbol, LocalDateTime tradeTime) {
        this.id = id;
        UserId = userId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        StockId = stockId;
        this.quantity = quantity;
        this.price = price;
        this.symbol = symbol;
        this.tradeTime = tradeTime;
    }

    public Trade() {}
}
