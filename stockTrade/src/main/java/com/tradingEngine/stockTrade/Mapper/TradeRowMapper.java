package com.tradingEngine.stockTrade.Mapper;

import com.tradingEngine.stockTrade.model.Trade;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;


public class TradeRowMapper implements RowMapper<Trade> {
    @Override
    public Trade mapRow(ResultSet rs, int rowNum) throws SQLException {

        Trade trade = new Trade();

        trade.setId(rs.getLong("id"));
        trade.setBuyerId(rs.getLong("buyer_Id"));
        trade.setSellerId(rs.getLong("seller_Id"));
        trade.setSymbol(rs.getString("symbol"));
        trade.setQuantity(rs.getInt("quantity"));
        trade.setPrice(rs.getBigDecimal("price"));
        trade.setTradeTime(rs.getTimestamp("trade_time").toLocalDateTime());

        return  trade;
    }
}
