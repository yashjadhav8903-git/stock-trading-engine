package com.tradingEngine.stockTrade.controller;

import com.tradingEngine.stockTrade.DTOs.OrderDTOs.OpenOrderResponseDTO;
import com.tradingEngine.stockTrade.enums.UserStatus;
import com.tradingEngine.stockTrade.service.OrderService;
import com.tradingEngine.stockTrade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    @PostMapping("addCash/{userId}")
    public ResponseEntity<String> addUserCash(@PathVariable Long userId, @RequestParam BigDecimal cashAmount) {
        userService.addCash(userId,cashAmount);
        return ResponseEntity.ok().body("Cash balance added successfully.");
    }

    @GetMapping
    public ResponseEntity<List<OpenOrderResponseDTO>> getOpenOrderResponseByUserId(@RequestParam Long userId){
        return ResponseEntity.ok(orderService.getOpenOrderResponseByUserId(userId));
    }

    @PostMapping("/status/{userId}")
    public ResponseEntity<String> updateUserStatus(@PathVariable Long userId, @RequestParam String userStatus){
        userService.UpdateUserStatus(userId,userStatus);
        return ResponseEntity.ok().body("User status updated successfully.");
    }
}