package com.tradingEngine.stockTrade.Mapper;

import com.tradingEngine.stockTrade.model.Holding;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HoldingRowMapper implements RowMapper<Holding> {
    @Override
    public Holding mapRow(ResultSet rs, int rowNum) throws SQLException {
        Holding holding = new Holding();

        holding.setId(rs.getLong("id"));
        holding.setUserId(rs.getLong("user_id"));
        holding.setSymbol(rs.getString("symbol"));
        holding.setQuantity(rs.getInt("quantity"));
        holding.setReservedQuantity(rs.getInt("reserved_quantity"));
        holding.setAvgPrice(rs.getBigDecimal("avg_price"));

        return holding;
    }
}
