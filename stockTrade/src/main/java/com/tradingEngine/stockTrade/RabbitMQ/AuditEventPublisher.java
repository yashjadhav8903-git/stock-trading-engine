package com.tradingEngine.stockTrade.RabbitMQ;

import com.tradingEngine.stockTrade.DTOs.RabbitMqEventDTOs.TradeAuditEvent;
import com.tradingEngine.stockTrade.configuration.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishAuditEvent(TradeAuditEvent auditEvent) {
        try{

            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.EXCHANGE,
                    RabbitMqConfig.ROUTING_KEY_AUDIT,
                    auditEvent
            );

            log.info("Published Audit MethodName: {}", auditEvent.getMethodName());

        }catch (Exception e){

            log.error("Failed to publish audit event to RabbitMQ: {}", e.getMessage());

        }
    }
}
