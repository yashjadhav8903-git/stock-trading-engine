package com.tradingEngine.stockTrade.Mapper;

import com.tradingEngine.stockTrade.model.Stock;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StockRowMapper implements RowMapper<Stock> {
    @Override
    public Stock mapRow(ResultSet rs, int rowNum) throws SQLException {
        Stock stock = new Stock();
        stock.setId(rs.getLong("id"));
        stock.setCompanyName(rs.getString("companyName"));
        stock.setCurrentPrice(rs.getBigDecimal("current_price"));
        stock.setSymbol(rs.getString("symbol"));

        return  stock;
    }
}
