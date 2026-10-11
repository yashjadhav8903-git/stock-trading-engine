package com.tradingEngine.stockTrade.service;

import com.tradingEngine.stockTrade.DTOs.Page.PageResponse;
import com.tradingEngine.stockTrade.annotations.AuditTradeLog;
import com.tradingEngine.stockTrade.model.AuditLog;
import com.tradingEngine.stockTrade.repository.AuditRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditRepository  auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @AuditTradeLog(action = "AUDIT_VIEW")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('Audit:View')")
    public PageResponse<AuditLog> getAuditLogs(Integer page, Integer pageSize){
        return  auditRepository.getAuditLogs(page, pageSize);
    }
}
