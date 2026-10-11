package com.tradingEngine.stockTrade.repository;

import com.tradingEngine.stockTrade.DTOs.Page.PageResponse;
import com.tradingEngine.stockTrade.Mapper.AuditRowMapper;
import com.tradingEngine.stockTrade.model.AuditLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class AuditRepository {

    private final JdbcTemplate jdbcTemplate;
    private final AuditRowMapper  auditRowMapper = new AuditRowMapper();

    public AuditRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PageResponse<AuditLog> getAuditLogs(int pageNumber, int pageSize) {

        // calculate offSet
        int offSet = pageNumber * pageSize;

        String countSql = """
                select count(*) from Trade_Audit_Logs
        """;

        Long elementCount = jdbcTemplate.queryForObject(countSql, Long.class);
        if(elementCount == null) elementCount = 0L;

        String sql = """
                SELECT
                id,
                action,
                actor,
                ip_address,
                message,
                method_name,
                status,
                execution_time,
                timestamp
                FROM Trade_Audit_Logs
                order by id desc
                limit ? offset ?
                """;

        List<AuditLog> content = jdbcTemplate.query(sql, auditRowMapper, pageSize, offSet);

        int totalPages = (int) Math.ceil((double) elementCount / pageSize);
        PageResponse<AuditLog> response = new PageResponse<>();
        response.setTotalPages(totalPages);
        response.setContent(content);
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotalElements(elementCount);

        return response;


    }
}
