package com.tradingEngine.stockTrade.DTOs.StocksDTOs;

import java.math.BigDecimal;

public class StocksRequestDTO {

    private String symbol;
    private String companyName;
    private BigDecimal currentPrice;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        if(symbol != null){
            this.symbol = symbol.toUpperCase();
        }
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
}
