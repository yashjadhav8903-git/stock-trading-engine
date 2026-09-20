package com.tradingEngine.stockTrade.controller;

import com.tradingEngine.stockTrade.DTOs.PortolioDTOs.PortfolioResponseDTO;
import com.tradingEngine.stockTrade.service.PortfolioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<List<PortfolioResponseDTO>> getPortfolio(@RequestParam Long userId) {

        List<PortfolioResponseDTO> userPortfolio =
                portfolioService.getUserPortfolio(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userPortfolio);
    }   
}
