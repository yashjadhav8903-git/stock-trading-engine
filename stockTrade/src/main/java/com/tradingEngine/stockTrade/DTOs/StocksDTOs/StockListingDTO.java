package com.tradingEngine.stockTrade.DTOs.StocksDTOs;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;


public class StockListingDTO {

    @NotBlank(message = "Symbol can't be blank")
    private String symbol;

    @NotBlank(message = "CompanyName can't be blank")
    @JsonAlias({"companyName", "companyname", "company_name"})
    @JsonProperty("companyName")
    private String companyName;

    @NotNull(message = "Initial price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private BigDecimal initialPrice;

    @NotNull(message = "Total quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer totalQuantity;

    public StockListingDTO(String symbol, String companyName, BigDecimal initialPrice, Integer totalQuantity) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.initialPrice = initialPrice;
        this.totalQuantity = totalQuantity;
    }
    public StockListingDTO() {}

    public @NotBlank(message = "Symbol can't be blank") String getSymbol() {
        return symbol;
    }

    public void setSymbol(@NotBlank(message = "Symbol can't be blank") String symbol) {
        this.symbol = symbol;
    }

    public @NotBlank(message = "CompanyName can't be blank") String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(@NotBlank(message = "CompanyName can't be blank") String companyName) {
        this.companyName = companyName;
    }

    public @NotNull(message = "Initial price is required") @Min(value = 1, message = "Price must be greater than 0") BigDecimal getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(@NotNull(message = "Initial price is required") @Min(value = 1, message = "Price must be greater than 0") BigDecimal initialPrice) {
        this.initialPrice = initialPrice;
    }

    public @NotNull(message = "Total quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(@NotNull(message = "Total quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}
