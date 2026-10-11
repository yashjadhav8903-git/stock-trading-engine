package com.tradingEngine.stockTrade.Mapper;

import com.tradingEngine.stockTrade.model.AuditLog;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuditRowMapper implements RowMapper<AuditLog> {

    @Override
    public AuditLog mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuditLog auditLog = new AuditLog();
        auditLog.setId(rs.getLong("id"));
        auditLog.setAction(rs.getString("action"));
        auditLog.setActor(rs.getString("actor"));
        auditLog.setMethodName(rs.getString("methodName"));
        auditLog.setStatus(rs.getString("status"));
        auditLog.setIpAddress(rs.getString("ipAddress"));
        auditLog.setMessage(rs.getString("message"));
        auditLog.setExecutionTime(rs.getLong("executionTime"));
        auditLog.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());

        return auditLog;
    }
}
