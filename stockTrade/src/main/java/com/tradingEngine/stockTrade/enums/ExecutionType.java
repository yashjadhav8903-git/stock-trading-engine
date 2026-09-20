package com.tradingEngine.stockTrade.enums;

/**
 *  LIMIT -->  An order to buy or sell only at a specific price or better.
 *  MARKET --> An order to buy or sell instantly at the best available current market price.
 *  STOP_LOSS  --> An automated order designed to limit your losses. It acts as a hidden trigger that turns into a Market Order when a certain price is hit.
 *  STOP_LIMIT --> A precise safety net that requires two inputs: a Trigger Price and a Limit Price
 */

public enum ExecutionType {

    LIMIT,
    MARKET,
    STOP_LOSS,
    STOP_LIMIT

}

