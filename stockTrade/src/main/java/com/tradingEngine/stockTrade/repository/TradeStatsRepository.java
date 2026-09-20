package com.tradingEngine.stockTrade.repository;

import com.tradingEngine.stockTrade.DTOs.TradeStatsDTOs.TradeStateRepositoryDTO;
import com.tradingEngine.stockTrade.DTOs.TradeStatsDTOs.TradeStatsAvgDTO;
import com.tradingEngine.stockTrade.DTOs.TradeStatsDTOs.TradeStatsCountDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TradeStatsRepository {

    private final JdbcTemplate jdbcTemplate;

    public TradeStatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public TradeStateRepositoryDTO getTradeStateRepositoryDTO() {

        String sql = """
                select count(*) as totalTradePrice,
                coalesce(sum(quantity),0) as tradeVolume,
                coalesce(avg(price),0.0) as avgTradePrice,
                coalesce(max(price),0) as maxTradePrice,
                coalesce(min(price),0) as minTradePrice
                from trades
                """;

        return jdbcTemplate.queryForObject(sql, (rs,rowNum) -> new TradeStateRepositoryDTO(
                rs.getLong("totalTradePrice"),
                rs.getLong("tradeVolume"),
                rs.getBigDecimal("avgTradePrice"),
                rs.getBigDecimal("maxTradePrice"),
                rs.getBigDecimal("minTradePrice")
        ));
    }


    public TradeStatsCountDTO getTradeStatsCountDTO() {

        String sql = """
                select count(*) as totalTradeCount
                from trades
        """;

        return jdbcTemplate.queryForObject(sql, (rs,rowNum) -> new TradeStatsCountDTO(
                rs.getLong("totalTradeCount")
        ));
    }


    public TradeStatsAvgDTO getTradeStatsAvgDTO() {

        String sql = """
                select
                coalesce(avg(price),0.0) as avgTradePrice
                from trades
        """;

        return jdbcTemplate.queryForObject(sql, (rs,rowNum) -> new TradeStatsAvgDTO(
                rs.getBigDecimal("avgTradePrice")
        ));
    }


    public String getTopTradedStock(){

        String sql = """
                select symbol
                from trades
                group by symbol
                order by sum(quantity) desc
                limit 1
                """;

        List<String> symbol =
                jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("symbol"));

        return symbol.isEmpty() ? "N/A" : symbol.getFirst();
    }

}

