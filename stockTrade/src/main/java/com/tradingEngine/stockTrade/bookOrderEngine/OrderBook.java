package com.tradingEngine.stockTrade.bookOrderEngine;

import com.tradingEngine.stockTrade.enums.OrderType;
import com.tradingEngine.stockTrade.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class OrderBook {


    private static final Logger log = LoggerFactory.getLogger(OrderBook.class);
    /**
     * IMPORTANT -> PriorityQueue is not Thread-Safe that's way we use <B>PriorityBlockingQueue</B>.
     * Is class ka main role hai: ->  Price-Time Priority ke basis par Active Buy aur Sell Orders ko In-Memory maintain karna
     */
    private final PriorityBlockingQueue<Order> buyOrders;
    private final PriorityBlockingQueue<Order> sellOrders;




    public OrderBook() {
        this(
                new PriorityBlockingQueue<>(11, new BuyOrderComparator()),
                new PriorityBlockingQueue<>(11, new SellOrderComparator())
        );
    }
    public OrderBook(PriorityBlockingQueue<Order> buyOrders, PriorityBlockingQueue<Order> sellOrders) {
        this.buyOrders = buyOrders;
        this.sellOrders = sellOrders;
    }

    // addOrder
    public void addOrder(Order order) {
        log.info("Adding to Book-Order {}", order);
        if(order.getOrderType() == OrderType.BUY){
            buyOrders.offer(order);
        } else {
            sellOrders.offer(order);
        }
    }

    // remove Order
    public boolean cancelOrder(Order order) {
        if(order.getOrderType() == OrderType.BUY){
           return buyOrders.remove(order);
        } else {
            return sellOrders.remove(order);
        }
    }

    // BestBuyOrder
    public Order getBestBuyOrder() {
        return buyOrders.peek();
    }

    // BestSellOrder
    public Order getBestSellOrder() {
        return sellOrders.peek();
    }

    // removeBestBuyOrder
    public void removeBestBuyOrder(){
        buyOrders.poll();
    }

    //removeBestSellOrder
    public void removeBestSellOrder(){
        sellOrders.poll();
    }

    public boolean hasBuyOrders() {
        return !buyOrders.isEmpty();
    }

    public boolean hasSellOrders() {
        return !sellOrders.isEmpty();
    }
}
