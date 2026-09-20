package com.tradingEngine.stockTrade.repository;

import com.tradingEngine.stockTrade.Mapper.StockRowMapper;
import com.tradingEngine.stockTrade.model.Stock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class StockRepository {

    private final JdbcTemplate jdbcTemplate;

    private final StockRowMapper stockRowMapper = new StockRowMapper();

    public StockRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Stock findBySymbol(String symbol){

        String sql = """
                SELECT
                id,
                companyName,
                symbol,
                current_price
                FROM stocks
                WHERE symbol = ?
                """;

        return jdbcTemplate.queryForObject(sql, stockRowMapper, symbol.toUpperCase());
    }

    public int updateCurrentPrice(String symbol, BigDecimal newPrice){
        String sql = """
                UPDATE stocks
                set current_price = ?
                where symbol = ?
        """;
        return jdbcTemplate.update(sql,newPrice,symbol.toUpperCase());
    }
}
