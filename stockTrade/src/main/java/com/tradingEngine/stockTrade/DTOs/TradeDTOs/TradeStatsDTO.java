package com.tradingEngine.stockTrade.DTOs.TradeDTOs;

import java.io.Serializable;
import java.math.BigDecimal;

public class TradeStatsDTO implements Serializable {

    private Long tradeCount;
    private BigDecimal highestTradePrice;
    private int tradeVolume;
    private BigDecimal lowestTradePrice;

    public TradeStatsDTO() {}

    public Long getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(Long tradeCount) {
        this.tradeCount = tradeCount;
    }

    public BigDecimal getHighestTradePrice() {
        return highestTradePrice;
    }

    public void setHighestTradePrice(BigDecimal highestTradePrice) {
        this.highestTradePrice = highestTradePrice;
    }

    public int getTradeVolume() {
        return tradeVolume;
    }

    public void setTradeVolume(int tradeVolume) {
        this.tradeVolume = tradeVolume;
    }

    public BigDecimal getLowestTradePrice() {
        return lowestTradePrice;
    }

    public void setLowestTradePrice(BigDecimal lowestTradePrice) {
        this.lowestTradePrice = lowestTradePrice;
    }



    public TradeStatsDTO(Long tradeCount, BigDecimal highestTradePrice, int tradeVolume, BigDecimal lowestTradePrice) {
        this.tradeCount = tradeCount;
        this.highestTradePrice = highestTradePrice;
        this.tradeVolume = tradeVolume;
        this.lowestTradePrice = lowestTradePrice;

    }
}
