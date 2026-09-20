package com.tradingEngine.stockTrade.DTOs.TradeStatsDTOs;

import java.io.Serializable;

public class TradeStatsCountDTO implements Serializable {

    private Long totalTradeCount;

    public Long getTotalTradeCount() {
        return totalTradeCount;
    }

    public void setTotalTradeCount(Long totalTradeCount) {
        this.totalTradeCount = totalTradeCount;
    }

    public TradeStatsCountDTO(Long totalTradeCount) {
        this.totalTradeCount = totalTradeCount;
    }

    public TradeStatsCountDTO() {}
}
