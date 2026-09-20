package com.tradingEngine.stockTrade.controller;

import com.tradingEngine.stockTrade.DTOs.TradeDTOs.TradeResponseDTO;
import com.tradingEngine.stockTrade.DTOs.TradeDTOs.TradeStatsDTO;
import com.tradingEngine.stockTrade.service.TradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trade")
public class TradeController {


    private static final Logger log = LoggerFactory.getLogger(TradeController.class);
    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/record")
    public ResponseEntity<List<TradeResponseDTO>> getTradeRecord(){

        log.info("Record-Request come to Controller");
        List<TradeResponseDTO> trades = tradeService.getTrades();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(trades);
    }

    @GetMapping("/stats")
    public ResponseEntity<TradeStatsDTO> getTradeStats(){
        log.info("Stats-Request come to Controller");
        return new ResponseEntity<>(
                tradeService.getTradeFromDB(),
                HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<List<TradeResponseDTO>> getHistoryByUserId(@RequestParam Long userId){
        log.info("History-Request come to Controller {} ", userId);

        List<TradeResponseDTO> byUserId = tradeService.getByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(byUserId);
    }
}
