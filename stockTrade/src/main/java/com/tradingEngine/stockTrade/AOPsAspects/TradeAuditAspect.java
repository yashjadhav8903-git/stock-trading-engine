package com.tradingEngine.stockTrade.AOPsAspects;

import com.tradingEngine.stockTrade.DTOs.RabbitMqEventDTOs.TradeAuditEvent;
import com.tradingEngine.stockTrade.RabbitMQ.AuditEventPublisher;
import com.tradingEngine.stockTrade.annotations.AuditTradeLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class TradeAuditAspect {

    private final HttpServletRequest request;
    private final AuditEventPublisher  auditEventPublisher;

    @Around("@annotation(auditTradeLog)")
    public Object AuditAspect(ProceedingJoinPoint joinPoint, AuditTradeLog auditTradeLog) throws Throwable {

        long startTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String method = signature.getMethod().getName();

        String status = "SUCCESS";
        String message = null;
        String ipAddress = request.getRemoteAddr();
        Object result;

        try{

            result = joinPoint.proceed();
            return result;

        } catch (Throwable throwable) {

            status = "FAILURE";
            message = throwable.getMessage();
            throw throwable;

        } finally {

            long endTime = System.currentTimeMillis() - startTime;

            TradeAuditEvent auditEvent = TradeAuditEvent.builder()
                    .action(auditTradeLog.action())
                    .methodName(method)
                    .status(status)
                    .message(message)
                    .ipAddress(ipAddress)
                    .executionTime(endTime)
                    .timestamp(LocalDateTime.now())
                    .build();

            // RabbitMQ me push
            auditEventPublisher.publishAuditEvent(auditEvent);
            log.info("⚡ [AOP Interceptor] Audit event for '{}' queued in {} ms", message, endTime);
        }
    }
}
