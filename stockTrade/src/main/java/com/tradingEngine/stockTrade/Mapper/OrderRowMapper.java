package com.tradingEngine.stockTrade.Mapper;

import com.tradingEngine.stockTrade.enums.ExecutionType;
import com.tradingEngine.stockTrade.enums.OrderStatus;
import com.tradingEngine.stockTrade.enums.OrderType;
import com.tradingEngine.stockTrade.model.Order;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;


public class OrderRowMapper implements RowMapper<Order> {


    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();

        order.setId(rs.getLong("id"));
        order.setUserId(rs.getLong("user_id"));
        order.setSymbol(rs.getString("symbol"));
        order.setQuantity(rs.getInt("quantity"));
        order.setPrice(rs.getBigDecimal("price"));
        order.setOrderType(OrderType.valueOf(rs.getString("order_type")));
        order.setExecutionType(ExecutionType.valueOf(rs.getString("execution_type")));
        order.setOrderStatus(OrderStatus.valueOf(rs.getString("order_status")));
        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        return order;

    }
}
