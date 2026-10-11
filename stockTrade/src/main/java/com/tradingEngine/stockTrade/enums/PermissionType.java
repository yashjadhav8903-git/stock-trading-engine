package com.tradingEngine.stockTrade.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionType {

    ORDER_CREATE("Order:Create"),
    ORDER_MODIFY("Order:Modify"),
    ORDER_CANCEL("Order:Cancel"),
    ORDER_READ("Order:Read"),
    PORTFOLIO_READ("Portfolio:Read"),
    TRADE_READ("Trade:Read"),
    STOCK_LIST("Stock:List"),
    STOCK_VIEW("Stock:View"),
    ACCOUNT_LOCK("Account:Lock"),
    TRADE_CASH("Trade:Cash"),
    TRADE_VIEW("Trade:View");

    private final String permission;
}
