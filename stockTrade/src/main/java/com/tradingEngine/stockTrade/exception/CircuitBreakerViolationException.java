package com.tradingEngine.stockTrade.exception;

public class CircuitBreakerViolationException extends RuntimeException{

    public CircuitBreakerViolationException(String message){
        super(message);
    }
}
