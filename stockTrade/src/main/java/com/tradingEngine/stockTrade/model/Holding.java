package com.tradingEngine.stockTrade.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * inventory ki source-of-truth sirf holdings table hoti hai ❤️
 */
public class Holding {

    private Long id;
    private Long userId;
    private String symbol;
    private int quantity;
    private int reservedQuantity;
    private BigDecimal avgPrice;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setReservedQuantity(int reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
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



    public Holding(Long id, Long userId, int quantity, int reservedQuantity, BigDecimal avgPrice, String symbol) {
        this.id = id;
        this.userId = userId;
        this.quantity = quantity;
        this.reservedQuantity = reservedQuantity;
        this.avgPrice = avgPrice;
        this.symbol = symbol;

    }

    public Holding() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Holding holding = (Holding) o;
        return Objects.equals(id, holding.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
