package com.tradingEngine.stockTrade.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Stock {

    private Long id;
    private String symbol;
    private String companyName;
    private BigDecimal currentPrice;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        companyName = companyName;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Stock(Long id, String symbol, String companyName, BigDecimal currentPrice) {
        this.id = id;
        this.symbol = symbol;
        this.companyName = companyName;
        this.currentPrice = currentPrice;
    }

    public Stock(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return Objects.equals(symbol, stock.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}
