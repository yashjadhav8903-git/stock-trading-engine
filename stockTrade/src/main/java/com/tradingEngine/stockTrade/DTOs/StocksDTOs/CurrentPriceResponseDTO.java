package com.tradingEngine.stockTrade.DTOs.StocksDTOs;

import java.math.BigDecimal;

public class CurrentPriceResponseDTO {

    private String symbol;
    private BigDecimal currentPrice;

    public CurrentPriceResponseDTO(String symbol, BigDecimal currentPrice) {
        this.symbol = symbol;
        this.currentPrice = currentPrice;
    }

    public CurrentPriceResponseDTO() {}

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
}
