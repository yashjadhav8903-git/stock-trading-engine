package com.tradingEngine.stockTrade.DTOs.TradingUI;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TradeTickDto {
    private String symbol;
    private BigDecimal price;
    private Integer quantity;
    private LocalDateTime timestamp;
}
