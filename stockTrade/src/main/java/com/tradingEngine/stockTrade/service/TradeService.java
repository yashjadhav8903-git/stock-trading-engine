package com.tradingEngine.stockTrade.service;

import com.tradingEngine.stockTrade.DTOs.TradeDTOs.TradeResponseDTO;
import com.tradingEngine.stockTrade.DTOs.TradeDTOs.TradeStatsDTO;
import com.tradingEngine.stockTrade.model.Trade;
import com.tradingEngine.stockTrade.repository.TradeRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class TradeService {

//    private final List<Trade> trades = new CopyOnWriteArrayList<>();
    // we use CopyOnWriteArrayList multiple thread can write (add) at time . normal arrayList can throw race condition.

    // now we use Database
    private final TradeRepository tradeRepository;

    public TradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    // from DB

    @Cacheable(value = "tradeRecord", key = "'record'")
    @Transactional(readOnly = true)
    public TradeStatsDTO getTradeFromDB() {
        return tradeRepository.getTradeStats();
    }



    // return
    @Cacheable(value = "globalTrades", key = "'recent'")
    @Transactional(readOnly = true)
    public List<TradeResponseDTO> getTrades() {
        return tradeRepository.getTrade()
                .stream()
                .map(this::toDTO)
                .toList();

    }

    private TradeResponseDTO toDTO(Trade trade) {
        TradeResponseDTO tradeResponseDTO = new TradeResponseDTO();
        tradeResponseDTO.setId(trade.getId());
        tradeResponseDTO.setBuyerId(trade.getBuyerId());
        tradeResponseDTO.setSellerId(trade.getSellerId());
        tradeResponseDTO.setQuantity(trade.getQuantity());
        tradeResponseDTO.setPrice(trade.getPrice());
        tradeResponseDTO.setSymbol(trade.getSymbol());
        tradeResponseDTO.setTradeTime(trade.getTradeTime());

        return tradeResponseDTO;
    }

    // get user trade History by user id
    @Cacheable(value = "userTrades", key = "#userId")
    @Transactional(readOnly = true)
    public List<TradeResponseDTO> getByUserId(Long userId) {
       return tradeRepository.getByUserId(userId)
               .stream()
               .map(this::toDTOId)
               .toList();
    }

    private TradeResponseDTO toDTOId(Trade trade) {
        TradeResponseDTO tradeResponseDTO = new TradeResponseDTO();

        tradeResponseDTO.setId(trade.getId());
        tradeResponseDTO.setBuyerId(trade.getBuyerId());
        tradeResponseDTO.setSellerId(trade.getSellerId());
        tradeResponseDTO.setQuantity(trade.getQuantity());
        tradeResponseDTO.setPrice(trade.getPrice());
        tradeResponseDTO.setSymbol(trade.getSymbol());
        tradeResponseDTO.setTradeTime(trade.getTradeTime());

        return tradeResponseDTO;
    }

}