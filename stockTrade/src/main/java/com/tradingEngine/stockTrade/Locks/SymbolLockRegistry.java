package com.tradingEngine.stockTrade.Locks;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class SymbolLockRegistry {

    private final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    public ReentrantLock getLock(String symbol) {
        return lockMap.computeIfAbsent(
                symbol.toUpperCase(),
                k -> new ReentrantLock(true) // Fair Lock
        );
    }

}
