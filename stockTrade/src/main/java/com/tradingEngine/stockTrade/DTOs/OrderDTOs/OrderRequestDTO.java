package com.tradingEngine.stockTrade.DTOs.OrderDTOs;

import com.tradingEngine.stockTrade.enums.ExecutionType;
import com.tradingEngine.stockTrade.enums.OrderType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class OrderRequestDTO {

    @NotNull(message = "User Id is required")
    private Long userId;


    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Min(value = 1,message = "Quantity must be at least 1")
    private Integer quantity;

    @NotBlank(message = "symbol can't be blank")
    private String symbol;

    private OrderType orderType;

    private ExecutionType executionType;




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

    public @NotNull(message = "User Id is required") Long getUserId() {
        return userId;
    }

    public void setUserId(@NotNull(message = "User Id is required") Long userId) {
        this.userId = userId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public @NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer quantity) {
        this.quantity = quantity;
    }

    public @NotBlank(message = "symbol can't be blank") String getSymbol() {
        return symbol;
    }

    public void setSymbol(@NotBlank(message = "symbol can't be blank") String symbol) {
        this.symbol = symbol;
    }
}

