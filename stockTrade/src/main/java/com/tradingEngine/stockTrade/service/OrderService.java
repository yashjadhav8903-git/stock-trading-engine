package com.tradingEngine.stockTrade.service;

import com.tradingEngine.stockTrade.DTOs.OrderDTOs.OpenOrderResponseDTO;
import com.tradingEngine.stockTrade.DTOs.OrderDTOs.OrderModifyRequestDTO;
import com.tradingEngine.stockTrade.DTOs.OrderDTOs.OrderRequestDTO;
import com.tradingEngine.stockTrade.Locks.SymbolLockRegistry;
import com.tradingEngine.stockTrade.bookOrderEngine.OrderBook;
import com.tradingEngine.stockTrade.enums.ExecutionType;
import com.tradingEngine.stockTrade.enums.OrderStatus;
import com.tradingEngine.stockTrade.enums.OrderType;
import com.tradingEngine.stockTrade.exception.EngineBusyException;
import com.tradingEngine.stockTrade.exception.InsufficientBalanceException;
import com.tradingEngine.stockTrade.exception.OrderNotFoundException;
import com.tradingEngine.stockTrade.matchingEngine.MatchingLogic;
import com.tradingEngine.stockTrade.model.Order;
import com.tradingEngine.stockTrade.model.Stock;
import com.tradingEngine.stockTrade.repository.HoldingRepository;
import com.tradingEngine.stockTrade.repository.OrderRepository;
import com.tradingEngine.stockTrade.repository.StockRepository;
import com.tradingEngine.stockTrade.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final ConcurrentHashMap<String,OrderBook> orderBook = new ConcurrentHashMap<>();

    private final MatchingLogic matchingLogic;

    private final UserRepository userRepository;

    private final StockRepository stockRepository;

    private final OrderRepository orderRepository;

    private final HoldingRepository holdingRepository;

    private final SymbolLockRegistry symbolLockRegistry;


    public OrderService(MatchingLogic matchingLogic, OrderRepository orderRepository,UserRepository userRepository,
                        StockRepository stockRepository,HoldingRepository holdingRepository,SymbolLockRegistry symbolLockRegistry) {
        this.matchingLogic = matchingLogic;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.holdingRepository = holdingRepository;
        this.symbolLockRegistry = symbolLockRegistry;
    }




    public void placeBuyOrder(OrderRequestDTO orderRequestDTO){
            OrderPlacement(orderRequestDTO, OrderType.BUY);
    }

    public void placeSellOrder(OrderRequestDTO orderRequestDTO){
        OrderPlacement(orderRequestDTO, OrderType.SELL);
    }




    public void OrderPlacement(OrderRequestDTO orderRequestDTO, OrderType orderType) {

        log.info("Request in Order-Service | OrderPlacement | Thread : UserId : | {}, orderType : {} ",
                orderRequestDTO.getUserId(), orderRequestDTO.getOrderType());

        // 1. LOCK KE BAHAR: Validation, Cash/Stock Locking & DB Persistence

        // Step A: Validation
        validateOrder(orderRequestDTO);

        // Step B: Cash / Stock Reservation (Lock ke bahar, User-level Atomic operation)
        if (orderType == OrderType.BUY) {
            BigDecimal priceToLock;

            if (orderRequestDTO.getExecutionType() == ExecutionType.LIMIT) {
                priceToLock = orderRequestDTO.getPrice();
            } else {
                // MARKET Order - Current Market Price Fetch Karte hai
                Stock stock = stockRepository.findBySymbol(orderRequestDTO.getSymbol());
                if (stock == null || stock.getCurrentPrice() == null) {
                    throw new IllegalArgumentException("Invalid stock symbol or price unavailable");
                }
                priceToLock = stock.getCurrentPrice();
            }

            BigDecimal requiredCash = priceToLock.multiply(BigDecimal.valueOf(orderRequestDTO.getQuantity()));
            int rows = userRepository.reserveCash(orderRequestDTO.getUserId(), requiredCash);

            if (rows == 0) {
                throw new InsufficientBalanceException("Insufficient cash balance to place buy order");
            }
        }
        else if (orderType == OrderType.SELL) {
            int rows = holdingRepository.reserveStock(
                    orderRequestDTO.getUserId(),
                    orderRequestDTO.getSymbol(),
                    orderRequestDTO.getQuantity()
            );

            if (rows == 0) {
                throw new InsufficientBalanceException("Insufficient stock holdings to place sell order");
            }
        }

        // Step C: Order Creation & DB Save
        Order order = orderCreation(orderRequestDTO, orderType);
        orderRepository.saveOrder(order);


        // 2. LOCK KE ANDAR: Pure Microsecond In-Memory Matching Operations

        ReentrantLock lock = symbolLockRegistry.getLock(orderRequestDTO.getSymbol());
        boolean acquired = false;

        try {
            acquired = lock.tryLock(1000, TimeUnit.MILLISECONDS);

            if (!acquired) {
                // Exception Guard: No Silent Rejection!
                throw new EngineBusyException("Timeout acquiring lock for symbol: " + orderRequestDTO.getSymbol());
            }

            // RAM-level shared state mutation & Matching
            OrderBook orderBook = getOrderBook(orderRequestDTO.getSymbol());
            orderBook.addOrder(order);
            matchingLogic.processOrders(orderBook);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while placing order", e);

        } finally {
            if (acquired) {
                lock.unlock(); // Lock releasing fast (In microseconds!)
            }
        }
    }

    private Order orderCreation(OrderRequestDTO orderRequestDTO, OrderType orderType) {

        Order order = new Order();

        order.setId(order.getId());
        order.setUserId(orderRequestDTO.getUserId());
        order.setPrice(orderRequestDTO.getPrice());
        order.setSymbol(orderRequestDTO.getSymbol());
        order.setQuantity(orderRequestDTO.getQuantity());

        order.setOrderType(orderType);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setExecutionType(orderRequestDTO.getExecutionType());
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }

    // check validations
    private void validateOrder(OrderRequestDTO orderRequestDTO){

        if(orderRequestDTO.getExecutionType() == ExecutionType.LIMIT){
            if(orderRequestDTO.getPrice() == null) {
                throw new IllegalArgumentException("Price required for LIMIT order");
            }

            if(orderRequestDTO.getPrice().compareTo(BigDecimal.ZERO) < 0){
                throw new IllegalArgumentException("Price must be greater than zero");
            }
        }

        if(orderRequestDTO.getExecutionType() == ExecutionType.MARKET){
            if(orderRequestDTO.getPrice() != null){
                throw new IllegalArgumentException("Market Order should not contain price");
            }
        }

    }



    // remove order
    @Transactional
    public void cancelOrder(Long orderId,Long userId){

        // 1. Database se Order fetch karein
        Order order = orderRepository.findOrderById(orderId);

        if(order == null){
            throw new OrderNotFoundException("Order not found: " + orderId);
        }

        if(!order.getUserId().equals(userId)){
            throw new IllegalArgumentException("Unauthorized user for order cancellation");
        }

        if(order.getOrderStatus() != OrderStatus.PENDING){
            throw new IllegalStateException("Only PENDING orders can be cancelled. Current state: " + order.getOrderStatus());
        }

        // 2. Per-Symbol Lock Acquire karo (Concurrency Protection)
        ReentrantLock symbolLock = symbolLockRegistry.getLock(order.getSymbol());
        boolean acquired = false;

        try{
            acquired = symbolLock.tryLock(1000,TimeUnit.MILLISECONDS);
            if(!acquired) {
                throw new EngineBusyException("Timeout acquiring symbol: " + order.getSymbol());
            }

            // 3. Database Status Atomic Update via Spring JDBC
            int rowUpdated = orderRepository.updateOrderStatus(orderId, OrderStatus.CANCELLED);

            if(rowUpdated == 0){
                throw new IllegalStateException("Order already processed or cancelled concurrently");
            }

            if(order.getOrderType() == OrderType.BUY && order.getExecutionType() == ExecutionType.LIMIT){
                BigDecimal refundAmount = order.getPrice().multiply(BigDecimal.valueOf(order.getQuantity()));
                userRepository.releaseReservedCash(order.getUserId(), refundAmount);
                log.info("Refunded {} back to cash balance for Order ID {}", refundAmount, orderId);
            }

            // 4. In-Memory OrderBook se Remove (PriorityBlockingQueue)
            OrderBook book = getOrderBook(order.getSymbol());
            boolean removeOrder = book.cancelOrder(order);

            log.info("Order ID {} CANCELLED successfully via JDBC. Removed from RAM: {}", orderId, removeOrder);


        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while trying to cancel order",e);
        } finally {
            if(acquired) {
                symbolLock.unlock();
            }
        }
    }

    @Transactional
    public void modifyOrder(Long orderId, Long userId, OrderModifyRequestDTO orderModifyRequestDTO){

        // 1. Fetch Order
        Order order = orderRepository.findOrderById(orderId);

        if(order == null){
            throw new OrderNotFoundException("Order not found: " + orderId);
        }

        if(!order.getUserId().equals(userId)){
            throw new IllegalArgumentException("Unauthorized user for order modifying");
        }

        if(order.getOrderStatus() != OrderStatus.PENDING){
            throw new IllegalStateException("Only PENDING orders can be modifying");
        }

        // 2. Lock acquire karo (Per-Symbol Lock)
        ReentrantLock symbolLock = symbolLockRegistry.getLock(order.getSymbol());
        boolean acquired = false;

        try {
            acquired = symbolLock.tryLock(1000,TimeUnit.MILLISECONDS);
            if(!acquired) {
                throw new EngineBusyException("Timeout acquiring symbol: " + order.getSymbol());
            }

            OrderBook book = getOrderBook(order.getSymbol());

            // 3. Remove existing order from Memory Queue to break old priority
            boolean removed = book.cancelOrder(order);

            if(!removed){
                throw new IllegalStateException("Order already processed or cancelled concurrently");
            }

            if(order.getOrderType() == OrderType.BUY && order.getExecutionType() == ExecutionType.LIMIT){

                BigDecimal difference = getDifference(orderModifyRequestDTO, order);

                if(difference.compareTo(BigDecimal.ZERO) > 0){
                    // Extra balance reserve karo
                    int updated = userRepository.reserveCash(userId, difference);
                    if(updated == 0){
                        throw new InsufficientBalanceException("Insufficient cash to upgrade order price/quantity");
                    }
                } else if (difference.compareTo(BigDecimal.ZERO) < 0){
                    // Extra balance refund karo // Difference negative hai (-200), isliye .abs() hoke +200 refund hoga
                    userRepository.releaseReservedCash(userId, difference.abs());
                }
            }

            // 4. Update Values
            if(orderModifyRequestDTO.getNewPrice() != null){
                order.setPrice(orderModifyRequestDTO.getNewPrice());
            }
            if(orderModifyRequestDTO.getNewQuantity() != null){
                order.setQuantity(orderModifyRequestDTO.getNewQuantity());
            }

            // Priority reset timestamp **IMP
            order.setCreatedAt(LocalDateTime.now());

            // 5. Update DB via JDBC Template
            int row = orderRepository.updateOrderPriceAndQuantity(orderId, order.getPrice(), order.getQuantity());

            if(row == 0){
                throw new IllegalStateException("Order already processed or cancelled concurrently");
            }

            // 6. Re-insert modified order into Queue & Trigger Engine
            book.addOrder(order);
            matchingLogic.processOrders(book);
            log.info("Order ID {} modified successfully to Price: {} Qty: {}", orderId, order.getPrice(), order.getQuantity());

        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while trying to modify order",e);
        } finally {
            if(acquired) {
                symbolLock.unlock();
            }
        }
    }

    // helper method
    private static  BigDecimal getDifference(OrderModifyRequestDTO orderModifyRequestDTO, Order order) {
        BigDecimal oldCost = order.getPrice().multiply(BigDecimal.valueOf(order.getQuantity()));

        BigDecimal newPrice = (orderModifyRequestDTO.getNewPrice() != null) ? orderModifyRequestDTO.getNewPrice() : order.getPrice();
        int newQty = (orderModifyRequestDTO.getNewQuantity() != null) ? orderModifyRequestDTO.getNewQuantity() : order.getQuantity();

        BigDecimal newCost = newPrice.multiply(BigDecimal.valueOf(newQty));
        BigDecimal difference = newCost.subtract(oldCost);
        return difference;
    }


    // helper method  -> create new queue based on Symbol
    private OrderBook getOrderBook(String symbol){
        log.info("Getting OrderBook for Symbol : {}", symbol);
        return orderBook
                .computeIfAbsent(
                        symbol,
                        k -> new OrderBook()
                );
    }


    @Transactional(readOnly = true)
    public List<OpenOrderResponseDTO> getOrders(Long userId){

        log.info("OrderService -- Getting OpenOrders for UserId : {}", userId);

        return orderRepository.findOpenOrders(userId)
                .stream()
                .map(this::toDTO)
                .toList();

    }

    private OpenOrderResponseDTO toDTO(Order order){

        OpenOrderResponseDTO dto = new OpenOrderResponseDTO();

        dto.setOrderId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setPrice(order.getPrice());
        dto.setSymbol(order.getSymbol().toUpperCase());
        dto.setQuantity(order.getQuantity());
        dto.setPrice(order.getPrice());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setOrderType(order.getOrderType());
        dto.setExecutionType(order.getExecutionType());
        dto.setCreatedAt(order.getCreatedAt());

        log.info("Order data returned service layer to DTO. userId {} ", order.getUserId());

        return dto;
    }
}
//Trade History


