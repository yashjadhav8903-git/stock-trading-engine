package com.tradingEngine.stockTrade.controller;

import com.tradingEngine.stockTrade.DTOs.Page.PageResponse;
import com.tradingEngine.stockTrade.DTOs.StocksDTOs.CurrentPriceResponseDTO;
import com.tradingEngine.stockTrade.DTOs.StocksDTOs.StockListingDTO;
import com.tradingEngine.stockTrade.DTOs.StocksDTOs.StocksRequestDTO;

import com.tradingEngine.stockTrade.model.Stock;
import com.tradingEngine.stockTrade.service.StockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService){
        this.stockService = stockService;
    }

    // user
    @PreAuthorize("hasAuthority('Stock:View')")
    @GetMapping("/price")
    public ResponseEntity<CurrentPriceResponseDTO> getCurrentPrice(@RequestParam String symbol){
        CurrentPriceResponseDTO price = stockService.getPrice(symbol);
        return ResponseEntity.ok(price);
    }

    // user
    @PreAuthorize("hasAuthority('Stock:View')")
    @GetMapping("/info")
    public ResponseEntity<Stock> getStockInfo(@RequestParam String symbol){
        Stock stockInfo = stockService.getStockInfo(symbol);
        return ResponseEntity.ok(stockInfo);
    }

    // admin
    @PreAuthorize("hasAuthority('Stock:List')")
    @PostMapping("/list")
    public ResponseEntity<String> listStocks(@Valid @RequestBody StockListingDTO listingDTO){
        stockService.listNewStockToMarketAsIPO(listingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Stock has been listed and IPO launched successfully in Sell OrderBook ✅");
    }

    // user and admin
    @PreAuthorize("hasAuthority('Stock:View')")
    @GetMapping
    public ResponseEntity<PageResponse<Stock>> getStocks(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "5") int pageSize){
        PageResponse<Stock> stockEntireData = stockService.getStockEntireData(page, pageSize);
        return ResponseEntity.ok(stockEntireData);
    }
}
