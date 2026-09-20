package com.tradingEngine.stockTrade.DTOs.OrderDTOs;

import com.tradingEngine.stockTrade.enums.ExecutionType;
import com.tradingEngine.stockTrade.enums.OrderStatus;
import com.tradingEngine.stockTrade.enums.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OpenOrderResponseDTO {

    private Long orderId;
    private Long UserId;
    private String symbol;
    private int quantity;
    private BigDecimal price;
    private OrderStatus orderStatus;
    private OrderType orderType;
    private ExecutionType executionType;
    private LocalDateTime createdAt;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return UserId;
    }

    public void setUserId(Long userId) {
        UserId = userId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public ExecutionType getExecutionType() {
        return executionType;
    }

    public void setExecutionType(ExecutionType executionType) {
        this.executionType = executionType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
