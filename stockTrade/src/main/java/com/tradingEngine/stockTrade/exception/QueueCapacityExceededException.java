package com.tradingEngine.stockTrade.exception;

public class QueueCapacityExceededException extends RuntimeException{

    public QueueCapacityExceededException(String message) {
        super(message);
    }
}
