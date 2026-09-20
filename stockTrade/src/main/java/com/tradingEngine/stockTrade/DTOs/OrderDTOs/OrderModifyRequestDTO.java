package com.tradingEngine.stockTrade.DTOs.OrderDTOs;

import java.math.BigDecimal;

public class OrderModifyRequestDTO {
    private BigDecimal newPrice;
    private Integer newQuantity;

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    public Integer getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(Integer newQuantity) {
        this.newQuantity = newQuantity;
    }
}
