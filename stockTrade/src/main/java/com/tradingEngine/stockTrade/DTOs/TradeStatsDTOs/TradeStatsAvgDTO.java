package com.tradingEngine.stockTrade.DTOs.TradeStatsDTOs;

import java.io.Serializable;
import java.math.BigDecimal;

public class TradeStatsAvgDTO implements Serializable {

    private BigDecimal avgTradePrice;

    public TradeStatsAvgDTO() {}

    public TradeStatsAvgDTO(BigDecimal avgTradePrice) {
        this.avgTradePrice = avgTradePrice;
    }

    public BigDecimal getAvgTradePrice() {
        return avgTradePrice;
    }

    public void setAvgTradePrice(BigDecimal avgTradePrice) {
        this.avgTradePrice = avgTradePrice;
    }
}
