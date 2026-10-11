package com.tradingEngine.stockTrade.service;

import com.tradingEngine.stockTrade.DTOs.TradeStatsDTOs.TradeStateRepositoryDTO;
import com.tradingEngine.stockTrade.DTOs.TradeStatsDTOs.TradeStatsAvgDTO;
import com.tradingEngine.stockTrade.DTOs.TradeStatsDTOs.TradeStatsCountDTO;
import com.tradingEngine.stockTrade.repository.TradeStatsRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TradeStatsService {

    private final TradeStatsRepository tradeStatsRepository;

   public  TradeStatsService(TradeStatsRepository tradeStatsRepository) {
       this.tradeStatsRepository = tradeStatsRepository;
   }

    @Cacheable(value = "statsRepository",key = "'all'")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('Trade:View')")
    public TradeStateRepositoryDTO  getTradeStateRepository() {
       return tradeStatsRepository.getTradeStateRepositoryDTO();
    }

    @Cacheable(value = "statsCount" , key = "'count'")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('Trade:View')")
    public TradeStatsCountDTO  getTradeStatsCountDTO() {
        return tradeStatsRepository.getTradeStatsCountDTO();
    }

    @Cacheable(value = "statsAvg", key = "'avg'")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('Trade:Read')")
    public TradeStatsAvgDTO getTradeStatsAvgDTO() {
       return tradeStatsRepository.getTradeStatsAvgDTO();
    }

    @Cacheable(value = "topStock",key = "'top'")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('Trade:Read')")
    public String getTopTradedStock(){
        return tradeStatsRepository.getTopTradedStock();
    }
}