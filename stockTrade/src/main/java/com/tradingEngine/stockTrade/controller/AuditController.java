package com.tradingEngine.stockTrade.controller;

import com.tradingEngine.stockTrade.DTOs.Page.PageResponse;
import com.tradingEngine.stockTrade.model.AuditLog;
import com.tradingEngine.stockTrade.service.AuditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('Audit:View')")
    public ResponseEntity<PageResponse<AuditLog>> getAuditInfo(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "5") int size){
        PageResponse<AuditLog> auditLogs = auditService.getAuditLogs(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(auditLogs);
    }
}
