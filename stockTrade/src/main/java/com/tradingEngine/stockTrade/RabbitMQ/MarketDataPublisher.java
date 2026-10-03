package com.tradingEngine.stockTrade.RabbitMQ;

import com.tradingEngine.stockTrade.DTOs.TradingUI.TradeTickDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketDataPublisher {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void publishTick(TradeTickDto tradeTickDto) {
        String destination = "/topic/ticks/" + tradeTickDto.getSymbol();
        simpMessagingTemplate.convertAndSend(destination, tradeTickDto);
        log.info("📊 [WebSocket Tick Pushed] {} @ ₹{}", tradeTickDto.getSymbol(), tradeTickDto.getPrice());
    }
}
