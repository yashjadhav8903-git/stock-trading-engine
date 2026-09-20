package com.tradingEngine.stockTrade.controller;

import com.tradingEngine.stockTrade.DTOs.TradeStatsDTOs.TradeStateRepositoryDTO;
import com.tradingEngine.stockTrade.DTOs.TradeStatsDTOs.TradeStatsAvgDTO;
import com.tradingEngine.stockTrade.DTOs.TradeStatsDTOs.TradeStatsCountDTO;
import com.tradingEngine.stockTrade.service.TradeStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class TradeStatsController {

    private final TradeStatsService  tradeStatsService;

    public TradeStatsController(TradeStatsService tradeStatsService) {
        this.tradeStatsService = tradeStatsService;
    }

    @GetMapping("/trade-data")
    public ResponseEntity<TradeStateRepositoryDTO> getData(){
        TradeStateRepositoryDTO tradeStateRepository =
                tradeStatsService.getTradeStateRepository();
        return ResponseEntity.ok(tradeStateRepository);
    }

    @GetMapping("/trade-count")
    public ResponseEntity<TradeStatsCountDTO> getTradeStatsCountDTO() {
        TradeStatsCountDTO tradeStatsCountDTO =
                tradeStatsService.getTradeStatsCountDTO();
        return ResponseEntity.ok(tradeStatsCountDTO);
    }

    @GetMapping("/avg-price")
    public ResponseEntity<TradeStatsAvgDTO> getTradeStatsAvgDTO() {
        TradeStatsAvgDTO tradeStatsAvgDTO =
                tradeStatsService.getTradeStatsAvgDTO();
        return ResponseEntity.ok(tradeStatsAvgDTO);
    }

    @GetMapping("/top-stock")
    public ResponseEntity<String> getTopTrade() {
        String topTradedStock =
                tradeStatsService.getTopTradedStock();
        return ResponseEntity.ok(topTradedStock);
    }
}
