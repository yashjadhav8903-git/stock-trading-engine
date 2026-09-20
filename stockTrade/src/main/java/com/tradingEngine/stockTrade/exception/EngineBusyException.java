package com.tradingEngine.stockTrade.exception;

public class EngineBusyException extends RuntimeException{
    public EngineBusyException(String message){
        super(message);
    }
}
