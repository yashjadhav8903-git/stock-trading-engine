package com.tradingEngine.stockTrade.model;

import com.tradingEngine.stockTrade.enums.ExecutionType;
import com.tradingEngine.stockTrade.enums.OrderStatus;
import com.tradingEngine.stockTrade.enums.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Order {

    private Long id;
    private Long StockId;
    private Long UserId;
    private String symbol;
    private int quantity;
    private BigDecimal price;
    private OrderStatus orderStatus;
    private OrderType orderType;
    private ExecutionType  executionType;
    private LocalDateTime createdAt;





    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStockId() {
        return StockId;
    }

    public void setStockId(Long stockId) {
        StockId = stockId;
    }

    public Long getUserId() {
        return UserId;
    }

    public void setUserId(Long userId) {
        UserId = userId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ExecutionType getExecutionType() {
        return executionType;
    }

    public void setExecutionType(ExecutionType executionType) {
        this.executionType = executionType;
    }

    public Order(Long id, Long stockId, Long userId, String symbol, int quantity, BigDecimal price, OrderStatus orderStatus,
                 OrderType orderType, ExecutionType executionType, LocalDateTime createdAt) {
        this.id = id;
        StockId = stockId;
        UserId = userId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.orderStatus = orderStatus;
        this.orderType = orderType;
        this.executionType = executionType;
        this.createdAt = createdAt;
    }

    public Order() {}





    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id != null && id.equals(order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
