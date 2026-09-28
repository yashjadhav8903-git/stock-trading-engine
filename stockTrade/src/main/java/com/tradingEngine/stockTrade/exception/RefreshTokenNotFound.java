package com.tradingEngine.stockTrade.exception;

public class RefreshTokenNotFound extends RuntimeException{
    public RefreshTokenNotFound(String message) {
        super(message);
    }
}
