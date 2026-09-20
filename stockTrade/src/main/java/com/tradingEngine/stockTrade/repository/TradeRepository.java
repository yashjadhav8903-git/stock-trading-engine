package com.tradingEngine.stockTrade.repository;

import com.tradingEngine.stockTrade.DTOs.TradeDTOs.TradeStatsDTO;
import com.tradingEngine.stockTrade.Mapper.TradeRowMapper;
import com.tradingEngine.stockTrade.model.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class TradeRepository {

    private static final Logger log = LoggerFactory.getLogger(TradeRepository.class);
    private final JdbcTemplate jdbcTemplate;
    private final TradeRowMapper  tradeRowMapper =  new TradeRowMapper();

    public TradeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    // add trade
    public void addTrade(Trade trade) {

        String sql = """
                insert into trades
                (
                buyer_id,
                seller_id,
                symbol,
                quantity,
                price,
                trade_time
                )
                values(?,?,?,?,?,?)
                """;

        int rowAffected = jdbcTemplate.update(sql,
                trade.getBuyerId(),
                trade.getSellerId(),
                trade.getSymbol(),
                trade.getQuantity(),
                trade.getPrice(),
                trade.getTradeTime()
        );
        if(rowAffected == 1){
            System.out.println("trade Added successful ✅");
        } else {
            System.err.println("something was wrong with trade Repository ❌");
        }
    }

    // read all
    public List<Trade> getTrade(){

        String sql = """
                select
                id,
                buyer_id,
                seller_id,
                symbol,
                quantity,
                price,
                trade_time
                from trades
                """;

        List<Trade> query =
                jdbcTemplate.query(sql, tradeRowMapper);

        log.info("Data come from tradeRepository getTrade");

        return query;
    }

    public List<Trade> getByUserId(Long userId){

        String sql =  """
                select
                id,
                buyer_id,
                seller_id,
                symbol,
                quantity,
                price,
                trade_time
                from trades
                WHERE
                buyer_id = ? OR seller_id = ?
                ORDER BY trade_time DESC
                """;

        List<Trade> query =
                jdbcTemplate.query(sql, tradeRowMapper, userId,userId);
        log.info("Data come from tradeRepository getByUserId : {} ", userId);

        return query;
    }


    public TradeStatsDTO getTradeStats(){
        String sql = """
                select
                count(*) as trade_count,
                coalesce(max(price),0) as max_price,
                coalesce(sum(quantity),0) as total_volume,
                coalesce(min(price),0) as min_price
                from trades
        """;

        return jdbcTemplate.queryForObject(sql,(rs, rowNum) -> new TradeStatsDTO(
                rs.getLong("trade_count"),
                rs.getBigDecimal("max_price"),
                rs.getInt("total_volume"),
                rs.getBigDecimal("min_price")
        ));
    }



    public void addTradesInBatch(List<Trade> tradeList){
        log.info("Request Come into addTradesInBatch Repository");
        String sql = """
                insert into trades
                (buyer_id,seller_id,symbol,quantity,price,trade_time)
                values(?,?,?,?,?,?)
        """;

        jdbcTemplate.batchUpdate(sql,tradeList,tradeList.size(),
                (ps,trade) -> {

            ps.setLong(1,trade.getBuyerId());
            ps.setLong(2,trade.getSellerId());
            ps.setString(3,trade.getSymbol());
            ps.setInt(4,trade.getQuantity());
            ps.setBigDecimal(5,trade.getPrice());
            ps.setTimestamp(6, Timestamp.valueOf(trade.getTradeTime()));

        });
    }
}
