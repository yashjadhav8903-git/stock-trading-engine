package com.tradingEngine.stockTrade.controller;

import com.tradingEngine.stockTrade.DTOs.OrderDTOs.OpenOrderResponseDTO;
import com.tradingEngine.stockTrade.DTOs.OrderDTOs.OrderModifyRequestDTO;
import com.tradingEngine.stockTrade.DTOs.OrderDTOs.OrderRequestDTO;
import com.tradingEngine.stockTrade.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/buy")
    public ResponseEntity<String> placeOrder(@Valid @RequestBody OrderRequestDTO
                                                         orderRequestDTO){

        log.info("Buy-Order Request Enter ORDER Controller | UserId : {}",orderRequestDTO.getUserId());
        orderService.placeBuyOrder(orderRequestDTO);

        return ResponseEntity.ok("Buy Order Placed");
    }

    @PostMapping("/sell")
    public ResponseEntity<String> sellOrder(@RequestBody OrderRequestDTO
                                                        orderRequestDTO){

        log.info("Sell-Order Request Enter ORDER Controller | UserId : {}",orderRequestDTO.getUserId());
        orderService.placeSellOrder(orderRequestDTO);

        return ResponseEntity.ok("Sell Order Placed");

    }


    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId,
                                              @RequestParam Long userId){
        log.info("Cancel-Order Request Enter ORDER Controller | UserId : {}",orderId);
        orderService.cancelOrder(orderId,userId);

        return ResponseEntity.ok("Order cancelled successfully");
    }


    @PatchMapping("/{orderId}")
    public ResponseEntity<String> modifyOrder(@PathVariable Long orderId,
                                              @RequestParam Long userId,
                                              @RequestBody OrderModifyRequestDTO requestDTO) {
        log.info("Modify-Order Request Enter ORDER Controller | UserId : {}",orderId);
        orderService.modifyOrder(orderId,userId,requestDTO);
        return ResponseEntity.ok("Order modified and re-queued successfully");
    }


    @GetMapping("/open")
    public ResponseEntity<List<OpenOrderResponseDTO>> getOpenOrder(@RequestParam long userId){

        log.info("Open-Order Request Enter ORDER Controller | UserId : {}",userId);

        List<OpenOrderResponseDTO> orders = orderService.getOrders(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orders);
    }
}
