package com.tradingEngine.stockTrade.DTOs.OrderDTOs;

import java.math.BigDecimal;

public class OrderNotificationDTO {
    private Long userId;
    private String symbol;
    private String orderType;
    private Integer quantity;
    private BigDecimal price;
    private String orderStatus;
    private String message;


    public OrderNotificationDTO(Long userId, String symbol, String orderType, Integer quantity, BigDecimal price, String orderStatus, String message) {
        this.userId = userId;
        this.symbol = symbol;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.orderStatus = orderStatus;
        this.message = message;
    }

    public OrderNotificationDTO() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
