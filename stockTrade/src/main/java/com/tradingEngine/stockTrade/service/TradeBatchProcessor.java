package com.tradingEngine.stockTrade.service;

import com.tradingEngine.stockTrade.model.Trade;
import com.tradingEngine.stockTrade.repository.TradeRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
public class TradeBatchProcessor {

    private static final Logger log = LoggerFactory.getLogger(TradeBatchProcessor.class);
    //Thread-Safe In-Memory Queue hai. its capacity 100,000. if DB crash that time queue handle traffic for queue capacity.
    private final BlockingQueue<Trade> tradeQueue = new LinkedBlockingQueue<>(100000);
    private final TradeRepository tradeRepository;
    private final CacheManager cacheManager;

    // Worker Scaling: Pool of worker threads
    private final int WORKER_POLL_SIZE = 3;
    private final ExecutorService workerExecutor = Executors.newFixedThreadPool(WORKER_POLL_SIZE);
    private volatile boolean running = true; // it's for worker thread.
    /**
     * <B>Volatile</B> :->  ka matlab hota hai: "Is variable ki value hamesha direct Main RAM se read karo aur direct RAM par write karo, CPU cache bypass karke."
     * <B>Volatile</B> :-> "Volatile" means: "Always read the value of this variable directly from RAM and write it directly to RAM, bypassing the cache."
     */


    public TradeBatchProcessor(TradeRepository tradeRepository,CacheManager cacheManager) {
        this.tradeRepository = tradeRepository;
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void startWorker() {
        for (int i = 0; i < WORKER_POLL_SIZE; i++) {
            int workerId = i + 1;
            workerExecutor.submit(() -> runWorkerLoop(workerId));
        }
        log.info("{} 🚀 workers Started | Trade Batch DB Worker Threads! ", WORKER_POLL_SIZE);
    }


    // Matching engine bina kisi wait ke instant queue mein Push karega (Microsecond speed)
    public boolean enqueueTrade(Trade trade) {
        boolean added = tradeQueue.offer(trade); // ek O(1) Non-Blocking Operation
        if (added) {
            log.debug("Trade ID: {} enqueued successfully. Current Queue Size: {}", trade.getId(), tradeQueue.size());
        } else {
            log.error("ALERT: Trade Queue IS FULL! Dropped Trade ID: {}", trade.getId());
        }
        return added;
    }



    public void runWorkerLoop(int workerId) {
        // create a list
        List<Trade> batch = new ArrayList<>(100);
        log.info("Worker-{} loop started and listening for trades...", workerId);

        while (running || !tradeQueue.isEmpty()) {
            try{

                // Instant 100 items drain karo O(1) Queue lock se
                tradeQueue.drainTo(batch,100);


                if(batch.isEmpty()){
                    // Agar queue khali hai tabhi poll karo thoda wait karke
                    Trade trade = tradeQueue.poll(100, TimeUnit.MILLISECONDS);
                    if(trade != null) {
                        batch.add(trade);
                    }
                }

                if (!batch.isEmpty()) {
                    flushBatch(batch, workerId);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Worker-{} interrupted, exiting loop...", workerId);
                break;
            } catch (Exception e) {
                log.error("Worker-{} Exception in execution loop: {}", workerId, e.getMessage(), e);
            }
        }

        // Final Drain on Worker Exit
        if(!batch.isEmpty()){
            flushBatch(batch, workerId);
        }
        log.info("Worker-{} stopped safely.", workerId);
    }


    private void flushBatch(List<Trade> batch,int workerId) {
        long start = System.currentTimeMillis();
        int batchSize = batch.size();

        try{

            tradeRepository.addTradesInBatch(batch);

            // Success hone par hi Redis Cache invalidate karo
            clearTradeCaches(batch);
            long duration = System.currentTimeMillis() - start;

            // Jab batchSize > 1 ho tab highlight print karo
            if (batchSize > 1) {
                log.info("🔥 [HEAVY BATCH] Worker-{} flushed {} trades together in {} ms | Queue Size Left: {}",
                        workerId, batchSize, duration, tradeQueue.size());
            } else {
                log.debug("😈👻 Worker-{} inserted  | TradeSize {}  | pushed in {} ms", workerId,batchSize, duration);
            }

            batch.clear(); // jab DB insert SUCCESS ho jaye, tabhi list khali karo!


            log.info("✅ Worker Inserted trades successfully! : WorkerId {}" , workerId);

        } catch(Exception e){
            log.error("Failed to flush batch on Worker-id {} | Size : {} | Exception: {} ", workerId, batchSize, e.getMessage(),e);
        }
    }


    // Graceful System Shutdown Hook
    @PreDestroy
    public void stopWorker() {
        log.warn("⚠️ Shutting down TradeBatchProcessor... Draining remaining {} items from queue.", tradeQueue.size());

        this.running = false;  // Stop accepting new loop entries
        workerExecutor.shutdown();  // shutdown executor

        try{

            // Wait up to 10 seconds for workers to finish draining queue
            if(!workerExecutor.awaitTermination(5,TimeUnit.SECONDS)) {
                log.error("⚠️ Worker threads did not finish in time (5s). Forcing shutdown.");
                workerExecutor.shutdownNow(); // shutdown Now 💀
            } else {
                log.info("✅ All remaining trades successfully flushed to DB. System stopped safely.");
            }

        } catch (InterruptedException e) {
            log.error("Shutdown interrupted! Forcing immediate worker exit.");
            workerExecutor.shutdownNow(); // shutdown Now 💀
        }
    }


    private void clearTradeCaches(List<Trade> batch){

        Cache userTradeCache = cacheManager.getCache("userTrades");
        if(userTradeCache != null){
            Set<Long> userIdsToEvict = new HashSet<>();
            for (Trade trade : batch) {
                userIdsToEvict.add(trade.getBuyerId());
                userIdsToEvict.add(trade.getSellerId());
            }
            for (Long userId : userIdsToEvict) {
                userTradeCache.evict(userId);
            }
        }

    }
}

/*
 * awaitTermination(10, TimeUnit.SECONDS) JVM ko 10 seconds tak wait
 * karwata hai jab tak queue mein baaki bache items DB mein flush na ho jayein. Data loss risk = 0%!
 */