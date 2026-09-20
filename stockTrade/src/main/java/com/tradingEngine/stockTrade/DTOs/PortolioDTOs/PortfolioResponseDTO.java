package com.tradingEngine.stockTrade.DTOs.PortolioDTOs;

import java.math.BigDecimal;

public class PortfolioResponseDTO {

    private Long userId;
    private String symbol;
    private int quantity;
    private BigDecimal avgPrice;
    private BigDecimal currentPrice;
    private BigDecimal currentValue;
    private BigDecimal totalInvestment;
    private BigDecimal pnl;  // pnl means Profit and lost
    //  Basic PnL Formulas and Calculation :
    /**
     * 1. Long Position (Buying): -> (exit price - entry price) * quantity
     * 2. Short Position (Selling): -> (entry price - exit price) * quantity
     */

    public PortfolioResponseDTO(Long userId,String symbol, int quantity, BigDecimal avgPrice, BigDecimal currentPrice) {
        this.userId = userId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.avgPrice = avgPrice;
        this.currentPrice = currentPrice;


        // Auto-calculate derived metrics inside constructor
        this.totalInvestment = (avgPrice != null) ? avgPrice.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO;
        this.currentValue = (currentPrice != null) ? currentPrice.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO;
        this.pnl = this.currentValue.subtract(this.totalInvestment);

    }

    public PortfolioResponseDTO() {

    }



    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(BigDecimal avgPrice) {
        this.avgPrice = avgPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public BigDecimal getTotalInvestment() {
        return totalInvestment;
    }

    public void setTotalInvestment(BigDecimal totalInvestment) {
        this.totalInvestment = totalInvestment;
    }

    public BigDecimal getPnl() {
        return pnl;
    }

    public void setPnl(BigDecimal pnl) {
        this.pnl = pnl;
    }
}
