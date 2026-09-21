package com.tradingEngine.stockTrade.repository;

import com.tradingEngine.stockTrade.DTOs.StocksDTOs.CurrentPriceResponseDTO;
import com.tradingEngine.stockTrade.DTOs.StocksDTOs.StocksRequestDTO;
import com.tradingEngine.stockTrade.Mapper.StockRowMapper;
import com.tradingEngine.stockTrade.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class StockRepository {

    private static final Logger log = LoggerFactory.getLogger(StockRepository.class);
    private final JdbcTemplate jdbcTemplate;

    private final StockRowMapper stockRowMapper = new StockRowMapper();

    public StockRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void SaveToStock(StocksRequestDTO stock){

        String sql = """
                insert into stocks
                (
                symbol,
                companyname,
                current_price
                )
                values(?,?,?)
                """;

       int rowAffected = jdbcTemplate.update(sql,
                stock.getSymbol(),
                stock.getCompanyName(),
                stock.getCurrentPrice());

       if(rowAffected == 1){
        log.info("Stock has been saved successfully");
       } else {
           log.warn("Stock has been saved failure ❌");
       }
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


    public CurrentPriceResponseDTO getStockCurrentPrice(String symbol){
        String sql = """
                SELECT
                symbol,
                current_price
                from stocks
                where symbol = ?
        """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs,rowNum) -> new CurrentPriceResponseDTO(
                    rs.getString("symbol"),
                    rs.getBigDecimal("current_price")
        ),
                symbol.trim().toUpperCase());

    }
}
