package com.tradingEngine.stockTrade.repository;

import com.tradingEngine.stockTrade.Mapper.HoldingRowMapper;
import com.tradingEngine.stockTrade.model.Holding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository

public class HoldingRepository {

    private static final Logger log = LoggerFactory.getLogger(HoldingRepository.class);
    private final JdbcTemplate jdbcTemplate;

    private final HoldingRowMapper holdingRowMapper = new HoldingRowMapper();

    public HoldingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. BUYER SIDE: Stock Holdings Add / Update (UPSERT logic)
    public void addOrUpdateHolding(Long userId, String symbol, int quantity, BigDecimal buyPrice){

        String sql = """
            INSERT INTO holdings (user_id, symbol, quantity, reserved_quantity, avg_price)
            VALUES (?, ?, ?, 0, ?)
            ON CONFLICT (user_id, symbol)
            DO UPDATE SET
                avg_price = ((holdings.quantity * holdings.avg_price) + (EXCLUDED.quantity * EXCLUDED.avg_price))
                            / (holdings.quantity + EXCLUDED.quantity),
                quantity = holdings.quantity + EXCLUDED.quantity
            """;

        jdbcTemplate.update(sql, userId, symbol.toUpperCase(), quantity, buyPrice);
    }

    // 2. SELLER SIDE: Trade match hone par reserved_quantity deduct karo
    public int deductReservedStock(Long userId,String symbol,int quantity){
        String sql = """
                update holdings
                set reserved_quantity = reserved_quantity - ?
                where user_id=? and symbol=? and reserved_quantity >= ?
        """;

        return jdbcTemplate.update(sql,quantity,userId,symbol.toUpperCase(),quantity);
    }

    // 3. SELL ORDER PLACEMENT: Order lagte waqt stock lock karne ke liye (OrderService mein kaam aayega)
    public int reserveStock(Long userId,String symbol,int quantity){
        String sql = """
                update holdings
                set quantity = quantity - ?,
                reserved_quantity = reserved_quantity + ?
                where user_id=? and symbol=? and quantity >= ?
        """;

        return jdbcTemplate.update(sql,quantity,quantity,userId,symbol.toUpperCase(),quantity);
    }


    public List<Holding> findByUserId(Long userId){

        String sql = """
                select
                id,
                user_id,
                symbol,
                avg_price,
                quantity,
                reserved_quantity
                from holdings
                where user_id=?
                """;

        List<Holding> query =
                jdbcTemplate.query(sql,holdingRowMapper,userId);

        log.info("Data Come from Database UserId is : {}", userId);

        return query;
    }




}
