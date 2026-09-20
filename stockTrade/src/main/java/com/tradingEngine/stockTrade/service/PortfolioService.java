package com.tradingEngine.stockTrade.service;

import com.tradingEngine.stockTrade.DTOs.PortolioDTOs.PortfolioResponseDTO;
import com.tradingEngine.stockTrade.model.Holding;
import com.tradingEngine.stockTrade.model.Stock;
import com.tradingEngine.stockTrade.repository.HoldingRepository;
import com.tradingEngine.stockTrade.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PortfolioService {

    private final HoldingRepository holdingRepository;
    private final StockRepository stockRepository;

    public PortfolioService(HoldingRepository holdingRepository, StockRepository stockRepository) {
        this.holdingRepository = holdingRepository;
        this.stockRepository = stockRepository;
    }

    public List<PortfolioResponseDTO> getUserPortfolio(Long userId) {
        List<Holding> holdings = holdingRepository.findByUserId(userId);

        List<PortfolioResponseDTO> portfolioList = new ArrayList<>();


        for (Holding holding : holdings) {
            Stock stock = stockRepository.findBySymbol(holding.getSymbol());
            BigDecimal currentPrice = (stock != null && stock.getCurrentPrice() != null)
                    ? stock.getCurrentPrice()
                    : holding.getAvgPrice();


            portfolioList.add(new PortfolioResponseDTO(
                    holding.getUserId(),
                    holding.getSymbol(),
                    holding.getQuantity(),
                    holding.getAvgPrice(),
                    currentPrice
            ));

        }

        return portfolioList;
    }
}
