package com.tradingEngine.stockTrade.matchingEngine;

import com.tradingEngine.stockTrade.DTOs.OrderDTOs.OrderNotificationDTO;
import com.tradingEngine.stockTrade.Locks.SymbolLockRegistry;
//import com.tradingEngine.stockTrade.Redis.RedisPublisher;
import com.tradingEngine.stockTrade.Redis.RedisService;
import com.tradingEngine.stockTrade.bookOrderEngine.OrderBook;
import com.tradingEngine.stockTrade.enums.ExecutionType;
import com.tradingEngine.stockTrade.enums.OrderStatus;
import com.tradingEngine.stockTrade.exception.EngineBusyException;
import com.tradingEngine.stockTrade.exception.InvalidTradePriceException;
import com.tradingEngine.stockTrade.exception.QueueCapacityExceededException;
import com.tradingEngine.stockTrade.model.Order;
import com.tradingEngine.stockTrade.model.Trade;
import com.tradingEngine.stockTrade.repository.HoldingRepository;
import com.tradingEngine.stockTrade.repository.OrderRepository;
import com.tradingEngine.stockTrade.repository.StockRepository;
import com.tradingEngine.stockTrade.repository.UserRepository;
import com.tradingEngine.stockTrade.service.TradeBatchProcessor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * MatchingEngine ki Responsibility
 *      OrderBook
 *          |
 *      Check Match
 *          |
 *      Create trade
 *          |
 *      Update Orders
 *          |
 *      Update Portfolio
 *
 *      Ab MARKET cases:
 * MARKET BUY vs LIMIT SELL  --> Trade price: sell price
 * LIMIT BUY vs MARKET SELL  --> Trade price : buy price  --> dono me trade hamesha limit pr hoga ❤️
 *
 */

@Service
public class MatchingLogic {

    private static final AtomicLong orderIdGenerator = new AtomicLong(1);
    private static final Logger log = LoggerFactory.getLogger(MatchingLogic.class);
    private final TradeBatchProcessor tradeBatchProcessor;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final HoldingRepository holdingRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RedisService redisService;
//    private final RedisPublisher redisPublisher;
    private final SymbolLockRegistry symbolLockRegistry;


    public MatchingLogic(TradeBatchProcessor tradeBatchProcessor,OrderRepository orderRepository,UserRepository userRepository,StockRepository stockRepository,
                         HoldingRepository holdingRepository,SimpMessagingTemplate simpMessagingTemplate,RedisService redisService,
                         SymbolLockRegistry symbolLockRegistry) {
        this.tradeBatchProcessor = tradeBatchProcessor;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.holdingRepository = holdingRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.redisService = redisService;
//        this.redisPublisher = redisPublisher;
        this.symbolLockRegistry = symbolLockRegistry;
    }



    public boolean canMatch(OrderBook orderBook){

        log.info("Enter in CanMatch service");

        // Direct peek() result ko local variable mein capture karke null check karo.
        Order buyOrders = orderBook.getBestBuyOrder();
        Order sellOrders = orderBook.getBestSellOrder();


        log.info("BUY {}",buyOrders);
        log.info("SELL {}",sellOrders);


        if(buyOrders == null || sellOrders == null){
            log.warn("NULL RETURN | buyOrders = {}, sellOrders = {}", buyOrders, sellOrders);
            return false;
        }

        log.info("BUY TYPE = " + buyOrders.getExecutionType());
        log.info("SELL TYPE = " + sellOrders.getExecutionType());

        // MARKET BUY or // MARKET SELL
        if(buyOrders.getExecutionType() == ExecutionType.MARKET || sellOrders.getExecutionType() == ExecutionType.MARKET){
            return true;
        }

        // LIMIT orders ke liye hi null check
        if(buyOrders.getPrice() == null || sellOrders.getPrice() == null ) {
            return false;
        }

        // LIMIT vs LIMIT
        return buyOrders
                .getPrice()
                .compareTo(sellOrders.getPrice())
                >= 0;

        /*
         * Case 1: (buyPrice)2000 >= (sellPrice)1000 -> true : trade done
         * Case 2: (buyPrice)1600 >= (sellPrice)2000 -> false : trade not done
         */


    }


    public void processOrders(OrderBook orderBook){

        // 1. Pehle dekho Top Order me symbol kya hai
        Order topOrder =  orderBook.getBestBuyOrder() != null
                ? orderBook.getBestBuyOrder()
                : orderBook.getBestSellOrder();

        // Agar OrderBook bilkul khali hai toh return ho jao
        if (topOrder == null) {
            log.warn("OrderBook empty hai, matching process skip kar rahe hain.");
            return;
        }
        // EXACT SAME Lock object yahan se milega!
        ReentrantLock lock = symbolLockRegistry.getLock(topOrder.getSymbol());

        try {

            boolean acquired = lock.tryLock(50, TimeUnit.MILLISECONDS);

            if(!acquired){
                // Agar 50ms mein bhi lock nahi mila, toh safe side Exception throw kar do
                throw new EngineBusyException("Matching engine busy, try again!");
            }

            try {
                log.info("Enter the Processing Orders for OrderBook {}", orderBook);

                while (canMatch(orderBook)) {

                    log.info("✅ MATCH FOUND");

                    Order buyOrders = orderBook.getBestBuyOrder();
                    Order sellOrders = orderBook.getBestSellOrder();

                    // SELF TRADE CHECK
                    if(buyOrders.getUserId().equals(sellOrders.getUserId())){
                        log.warn("Self-trade detected for userId: {}. Cancelling incoming order.", buyOrders.getUserId());

                        // Priority Queue infinite loop avoid karne ke liye:
                        // Top unmatched self-order ko remove/cancel status set kar do
                        orderBook.removeBestBuyOrder();
                        buyOrders.setOrderStatus(OrderStatus.CANCELLED);
                        orderRepository.updateOrder(
                                buyOrders.getId(),
                                OrderStatus.CANCELLED,
                                buyOrders.getQuantity()
                        );
                        continue; // Agle candidate order par jump karo (User 1 ka Sell order match ho sake)
                    }

                    Trade trade =
                            executeTrade(orderBook);

                    boolean enqueued = tradeBatchProcessor.enqueueTrade(trade);

                    if (!enqueued) {
                        throw new QueueCapacityExceededException("Trade Buffer Overflow! High Load Detected.");
                    }


                    log.info("✅ Trade Saved : " + trade.getStockId());
                }
            } finally {
                lock.unlock();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting for lock", e);
        }
    }

    public Trade executeTrade(OrderBook orderBook) {

        log.info("Enter in executeTrade service");

        // best order nikalo
        Order bestBuyOrder = orderBook.getBestBuyOrder();
        Order bestSellOrder = getBestSellOrder(orderBook, bestBuyOrder);

        // trade quantity
        int TradeQuantity = Math.min(
                bestBuyOrder.getQuantity(),
                bestSellOrder.getQuantity()
        );

        // trade price decision
        BigDecimal tradePrice = getTradePrice(bestBuyOrder, bestSellOrder);
        log.info("Decide Trade Price : {} rs", tradePrice);

        // Trade Settlement -> Matching hone par buyer ke account se reserved_balance permanently deduct karna aur stock holdings credit karna.
        BigDecimal tradeAmount =  tradePrice.multiply(BigDecimal.valueOf(TradeQuantity));

        // 1. Settlement: Buyer Cash Deduct (Reserved Balance se)
        userRepository.deductReservedCash(bestBuyOrder.getUserId(), tradeAmount);

        // 2. Seller ke account mein cash credit karo
        userRepository.addCash(bestSellOrder.getUserId(), tradeAmount);

        // 3. Stock Transfer
        // Buyer ko stock do
        holdingRepository.addOrUpdateHolding(bestBuyOrder.getUserId(),bestBuyOrder.getSymbol(),TradeQuantity,tradePrice);

        // Seller ka locked stock minus karo
        holdingRepository.deductReservedStock(bestSellOrder.getUserId(),bestSellOrder.getSymbol(),TradeQuantity);

        // 4. Real-Time LTP Update in DB // -> LTP Real-time Update: Trade execute hote hi stocks.current_price ko updated traded price par persist karna.
        stockRepository.updateCurrentPrice(bestBuyOrder.getSymbol(), tradePrice);

        log.info("TRADE EXECUTED: Symbol {} | Qty {} @ Price {} | LTP Updated!",
                bestBuyOrder.getSymbol(), TradeQuantity, tradePrice);

        // Publish to Redis Channel -> ke liye 😅
//        String symbol = bestBuyOrder.getSymbol();

        // REDIS FAST CACHE UPDATE:
        redisService.updateLTP(bestBuyOrder.getSymbol(), tradePrice);

//        // 2. Publish to Redis Channel
//        redisPublisher.publish("ticker." + symbol, tradePrice.toString());
//
//        // 2. Buyer Private Notification
//        OrderNotificationDTO buyerNotification = new OrderNotificationDTO(
//                bestBuyOrder.getUserId(),
//                symbol,
//                "BUY",
//                TradeQuantity,
//                tradePrice,
//                "FILLED",
//                "Order executed successfully!"
//        );
//        redisPublisher.publish("orders." + bestBuyOrder.getUserId(), buyerNotification);
//
//        // 3. Seller Private Notification
//        OrderNotificationDTO sellerNotification = new OrderNotificationDTO(
//                bestSellOrder.getUserId(),
//                symbol,
//                "SELL",
//                TradeQuantity,
//                tradePrice,
//                "FILLED",
//                "Order executed successfully!"
//        );
//        redisPublisher.publish("orders." + bestSellOrder.getUserId(), sellerNotification);


        // trade Object / trade creation
        Trade trade = getTrade(bestBuyOrder,bestSellOrder);
        trade.setPrice(tradePrice);
        trade.setQuantity(TradeQuantity);

        log.info("Trade Executed| Trade Created and Data Added | Thread : tradePrice {} 💸, and quantity {} ", tradePrice, TradeQuantity);


        // Remaining Quantity
        // buyer
        bestBuyOrder.setQuantity(
                bestBuyOrder.getQuantity() - TradeQuantity
        );
        // seller
        bestSellOrder.setQuantity(
                bestSellOrder.getQuantity() - TradeQuantity
        );

        //Status Update
        updateOrderStatus(bestBuyOrder);
        updateOrderStatus(bestSellOrder);


        // update status in DB
        orderRepository.updateOrder(
                bestBuyOrder.getId(),
                bestBuyOrder.getOrderStatus(),
                bestBuyOrder.getQuantity()
        );

        orderRepository.updateOrder(
                bestSellOrder.getId(),
                bestSellOrder.getOrderStatus(),
                bestSellOrder.getQuantity()
        );

        //Remove Filled Orders -> Remove Order if quantity is ZERO OR LESS (<= 0)
        if(bestBuyOrder.getQuantity() <= 0){
            orderBook.removeBestBuyOrder();
        }
        if(bestSellOrder.getQuantity() <= 0){
            orderBook.removeBestSellOrder();
        }

        return trade;
    }

    private void updateOrderStatus(Order order){

        if(order.getQuantity() <= 0){
            order.setOrderStatus(
                    OrderStatus.FILLED
            );
        } else{
            order.setOrderStatus(
                    OrderStatus.PARTIALLY_FILLED
            );
        }
    }





    // helper method to check validation
    private static  Order getBestSellOrder(OrderBook orderBook, Order bestBuyOrder) {

        Order bestSellOrder = orderBook.getBestSellOrder();

        if(bestBuyOrder.getExecutionType() == ExecutionType.MARKET || bestSellOrder.getExecutionType() == ExecutionType.MARKET){
            return bestSellOrder;
        }

        // Buyer ka offer Selling Price se kam hai
        if(bestBuyOrder.getPrice().compareTo(bestSellOrder.getPrice()) < 0) {
            throw new InvalidTradePriceException(
                    "Trade Execution Failed! Buyer price (" + bestBuyOrder.getPrice() +
                            ") is lower than Seller asking price (" + bestSellOrder.getPrice() + ")"
            );
        }
        return bestSellOrder;
    }


    // helper method trade price decision
    //  *      Ab MARKET cases:
    // * MARKET BUY vs LIMIT SELL  --> Trade price: sell price
    // * LIMIT BUY vs MARKET SELL  --> Trade price : buy price  --> dono me trade hamesha limit pr hoga ❤️
    private static BigDecimal getTradePrice(Order bestBuyOrder, Order bestSellOrder) {
        BigDecimal tradePrice;
        // MARKET BUY vs LIMIT SELL  --> Trade price: sell price
        if(bestBuyOrder.getExecutionType() == ExecutionType.MARKET){
            tradePrice = bestSellOrder.getPrice();

            //LIMIT BUY vs MARKET SELL  --> Trade price : buy price
        } else if (bestSellOrder.getExecutionType() == ExecutionType.MARKET) {
            tradePrice = bestBuyOrder.getPrice();
        } else {
            // LIMIT BUY vs LIMIT BUY
            tradePrice = bestSellOrder.getPrice();
        }
        return tradePrice;
    }

    // helper
    private static Trade getTrade(Order bestBuyOrder, Order bestSellOrder) {
        Trade trade = new Trade();


        trade.setId(orderIdGenerator.getAndIncrement());
        trade.setStockId(bestBuyOrder.getId());
        trade.setBuyerId(bestBuyOrder.getUserId());
        trade.setSellerId(bestSellOrder.getUserId());
        trade.setSymbol(bestSellOrder.getSymbol());
        trade.setTradeTime(LocalDateTime.now());

        log.info("TRADE BUYER ID {} " , trade.getBuyerId());
        log.info("TRADE SELLER ID {} " , trade.getSellerId());

        return trade;
    }
}