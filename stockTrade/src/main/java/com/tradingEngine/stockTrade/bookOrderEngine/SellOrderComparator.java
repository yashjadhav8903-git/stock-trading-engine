package com.tradingEngine.stockTrade.bookOrderEngine;

import com.tradingEngine.stockTrade.enums.ExecutionType;
import com.tradingEngine.stockTrade.model.Order;

import java.util.Comparator;

public class SellOrderComparator implements Comparator<Order> {

    @Override
    public int compare(Order o1, Order o2) {

        if(o1.getExecutionType() == ExecutionType.MARKET) {
            return 1;
        }

        if(o2.getExecutionType() == ExecutionType.MARKET) {
            return -1;
        }


        // 1. Primary Check: Low Price = High Priority (Min-Heap)
        int priceCompare = o1.getPrice().compareTo(o2.getPrice()); // Min-Heap for sale
        if (priceCompare != 0) {
            return priceCompare;
        }
        // 2. Secondary Check (Tie-Breaker): Pehle aaya Order (Chota ID) = High Priority (FIFO)
        return Long.compare(o1.getId(), o2.getId()); // FIFO
    }
}
