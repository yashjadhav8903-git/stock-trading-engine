package com.tradingEngine.stockTrade.JPARepository;

import com.tradingEngine.stockTrade.model.AuditLog;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

}
