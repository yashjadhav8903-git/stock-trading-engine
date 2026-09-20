package com.tradingEngine.stockTrade.DTOs.TradeStatsDTOs;

import java.io.Serializable;
import java.math.BigDecimal;

public class TradeStateRepositoryDTO implements Serializable {

    private Long totalTradeCount;
    private Long TradeVolume;
    private BigDecimal avgTradePrice;
    private BigDecimal maxTradePrice;
    private BigDecimal minTradePrice;

    public Long getTotalTradeCount() {
        return totalTradeCount;
    }

    public BigDecimal getMaxTradePrice() {
        return maxTradePrice;
    }

    public void setMaxTradePrice(BigDecimal maxTradePrice) {
        this.maxTradePrice = maxTradePrice;
    }

    public BigDecimal getMinTradePrice() {
        return minTradePrice;
    }

    public void setMinTradePrice(BigDecimal minTradePrice) {
        this.minTradePrice = minTradePrice;
    }

    public void setTotalTradeCount(Long totalTradeCount) {
        this.totalTradeCount = totalTradeCount;
    }

    public Long getTradeVolume() {
        return TradeVolume;
    }

    public void setTradeVolume(Long tradeVolume) {
        TradeVolume = tradeVolume;
    }

    public BigDecimal getAvgTradePrice() {
        return avgTradePrice;
    }

    public void setAvgTradePrice(BigDecimal avgTradePrice) {
        this.avgTradePrice = avgTradePrice;
    }

    public TradeStateRepositoryDTO(Long totalTradeCount, Long tradeVolume, BigDecimal avgTradePrice, BigDecimal maxTradePrice, BigDecimal minTradePrice) {
        this.totalTradeCount = totalTradeCount;
        this.TradeVolume = tradeVolume;
        this.avgTradePrice = avgTradePrice;
        this.maxTradePrice = maxTradePrice;
        this.minTradePrice = minTradePrice;
    }

    public TradeStateRepositoryDTO() {}
}
