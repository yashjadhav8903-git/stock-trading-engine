package com.tradingEngine.stockTrade.RabbitMQ;

import com.tradingEngine.stockTrade.DTOs.RabbitMqEventDTOs.TradeAuditEvent;
import com.tradingEngine.stockTrade.JPARepository.AuditLogRepository;
import com.tradingEngine.stockTrade.configuration.RabbitMqConfig;
import com.tradingEngine.stockTrade.model.AuditLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventListener {

    private final AuditLogRepository auditLogRepository;

    @RabbitListener(queues = RabbitMqConfig.AUDIT_QUEUE)
    public void handleAuditEvent(TradeAuditEvent event) {
        log.info("📩 [RabbitMQ Async Audit Log] Processing: {} | Status: {}", event.getAction(), event.getStatus());

        // Event DTO se Entity mapper aur DB save
        AuditLog auditLog = AuditLog.builder()
                .action(event.getAction())
                .methodName(event.getMethodName())
                .status(event.getStatus())
                .ipAddress(event.getIpAddress())
                .message(event.getMessage())
                .executionTime(event.getExecutionTime())
                .timestamp(event.getTimestamp())
                .build();

        auditLogRepository.save(auditLog);

    }
}
