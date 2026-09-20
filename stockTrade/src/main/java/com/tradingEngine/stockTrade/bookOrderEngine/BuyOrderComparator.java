package com.tradingEngine.stockTrade.bookOrderEngine;

import com.tradingEngine.stockTrade.enums.ExecutionType;
import com.tradingEngine.stockTrade.model.Order;

import java.util.Comparator;

public class BuyOrderComparator implements Comparator<Order> {

    @Override
    public int compare(Order o1, Order o2) {

        if(o1.getExecutionType() == ExecutionType.MARKET) {
            return -1;
        }

        if(o2.getExecutionType() == ExecutionType.MARKET) {
            return 1;
        }

        // 1. Primary Check: High Price = High Priority (Max-Heap)
        int priceCompare = o2.getPrice().compareTo(o1.getPrice()); // Max-Heap for BUY
        if (priceCompare != 0) {
            return priceCompare;
        }
        // 2. Secondary Check (Tie-Breaker): Pehle aaya Order (Chota ID) = High Priority (FIFO)
        return Long.compare(o1.getId(), o2.getId()); // FIFO (Earlier Order First)
    }
}
