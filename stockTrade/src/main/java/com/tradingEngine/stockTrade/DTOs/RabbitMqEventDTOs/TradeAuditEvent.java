package com.tradingEngine.stockTrade.DTOs.RabbitMqEventDTOs;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TradeAuditEvent {

    private String action;
    private String methodName;
    private String status;
    private String ipAddress;
    private String message;
    private Long executionTime;
    private LocalDateTime timestamp;

}
