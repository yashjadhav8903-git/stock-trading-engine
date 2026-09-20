package com.tradingEngine.stockTrade.enums;

/**
 * PENDING --> The system has received your order request, but it has not reached the exchange order book yet. (request receive toh huwi lekin order book tak nhi gayi)
 * PARTIALLY_FILLED  --> Only a part of your total requested order quantity has been buy or sell. (remaining share ex 100 you buy 60 remaining 40)
 * FILLED --> Your order has been completely executed. All requested shares or tokens have been successfully buy or sell.
 * CANCELLED  --> The order was permanently killed before it could be fully executed.
 * OPEN --> Your order is successfully sitting on the exchange order book, waiting for a buyer or seller to match your price.
 * COMPLETED --> The entire lifecycle of the order is finished and closed
 */

public enum OrderStatus {

    PENDING,
    PARTIALLY_FILLED,
    FILLED,
    CANCELLED,
    OPEN,
    COMPLETED
}
