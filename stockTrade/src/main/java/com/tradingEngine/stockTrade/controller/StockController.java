package com.tradingEngine.stockTrade.controller;

import com.tradingEngine.stockTrade.DTOs.StocksDTOs.CurrentPriceResponseDTO;
import com.tradingEngine.stockTrade.DTOs.StocksDTOs.StocksRequestDTO;

import com.tradingEngine.stockTrade.model.Stock;
import com.tradingEngine.stockTrade.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService){
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<String> saveStock(@RequestBody StocksRequestDTO  stocksRequestDTO){
        stockService.saveStock(stocksRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Stock has been saved successfully✅");
    }

    @GetMapping
    public ResponseEntity<CurrentPriceResponseDTO> getCurrentPrice(@RequestParam String symbol){
        CurrentPriceResponseDTO price = stockService.getPrice(symbol);
        return ResponseEntity.ok(price);
    }

    @GetMapping("/info")
    public ResponseEntity<Stock> getStockInfo(@RequestParam String symbol){
        Stock stockInfo = stockService.getStockInfo(symbol);
        return ResponseEntity.ok(stockInfo);
    }
}
