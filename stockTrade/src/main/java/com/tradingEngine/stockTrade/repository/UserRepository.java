package com.tradingEngine.stockTrade.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Atomically reserve cash for BUY order
    public int reserveCash (Long userId, BigDecimal amount){

        String sql = """
                update users
                set cash_balance = cash_balance - ?,
                reserved_balance = reserved_balance + ?
                where id = ? and cash_balance >= ?
                """;

        return jdbcTemplate.update(sql,amount,amount,userId,amount);
    }


    // Unblock reserved cash on Cancel / Modify
    public void releaseReservedCash(Long userId, BigDecimal amount){
        String sql = """
                update users
                set cash_balance = cash_balance + ?,
                reserved_balance = reserved_balance - ?
                where id = ?
        """;

        jdbcTemplate.update(sql,amount,amount,userId);
    }

    // Deduct reserved cash after successful trade match
    public int deductReservedCash(Long userId, BigDecimal amount){
        String sql = """
                update users
                set reserved_balance = reserved_balance - ?
                where id = ? and reserved_balance >= ?
        """;

        return jdbcTemplate.update(sql,amount,userId,amount);
    }

    // Add cash to seller account
    public int addCash(Long userId, BigDecimal amount){
        String sql = """
                update users
                set cash_balance = cash_balance + ?
                where id = ?
        """;
        return jdbcTemplate.update(sql,amount,userId);
    }
}
