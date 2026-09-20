package com.tradingEngine.stockTrade.exception;

public class InvalidTradePriceException extends RuntimeException{

    public InvalidTradePriceException(String message) {
        super(message);
    }
}
