package com.tradingEngine.stockTrade.service;

import com.tradingEngine.stockTrade.DTOs.StocksDTOs.CurrentPriceResponseDTO;
import com.tradingEngine.stockTrade.DTOs.StocksDTOs.StocksRequestDTO;
import com.tradingEngine.stockTrade.model.Stock;
import com.tradingEngine.stockTrade.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void saveStock(StocksRequestDTO stocksRequestDTO) {
        stockRepository.SaveToStock(stocksRequestDTO);
    }

    @Transactional(readOnly = true)
    public CurrentPriceResponseDTO getPrice(String symbol) {
        return stockRepository.getStockCurrentPrice(symbol);
    }

    @Transactional(readOnly = true)
    public Stock getStockInfo(String symbol) {
        return stockRepository.findBySymbol(symbol);
    }
}
