package com.tradingEngine.stockTrade.repository;

import com.tradingEngine.stockTrade.Mapper.OrderRowMapper;
import com.tradingEngine.stockTrade.enums.OrderStatus;
import com.tradingEngine.stockTrade.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.net.Proxy;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class OrderRepository {

    private static final Logger log = LoggerFactory.getLogger(OrderRepository.class);
    private final JdbcTemplate jdbcTemplate;

    private final OrderRowMapper orderRowMapper = new OrderRowMapper();


    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // Save Order
    public void saveOrder(Order order) {



        String sql = """
                INSERT INTO orders(
                    user_id,
                    symbol,
                    quantity,
                    price,
                    order_type,
                    execution_type,
                    order_status,
                    created_at
                )
                VALUES(?,?,?,?,?,?,?,?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();


        jdbcTemplate.update(con -> {
            // 🔴 FIX 1: Explicitly specify column name "id" for generated keys in PostgreSQL
            PreparedStatement ps =
                    con.prepareStatement(
                            sql, new String[]{"id"}
                    );

            ps.setLong(1, order.getUserId());
            ps.setString(2, order.getSymbol());
            ps.setInt(3, order.getQuantity());

            if(order.getPrice() != null){
                ps.setBigDecimal(4, order.getPrice());
            } else {
                ps.setNull(4, Types.NUMERIC);
            }

            ps.setString(5,order.getOrderType().name());
            ps.setString(6,order.getExecutionType().name());
            ps.setString(7,order.getOrderStatus().name());

            // 🔴 FIX 2: Null Safety for CreatedAt
            if(order.getCreatedAt() != null){
                ps.setTimestamp(8, Timestamp.valueOf(order.getCreatedAt()));
            } else {
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            }

                    return ps;

                },keyHolder
        );

        log.trace("Save Request in OrderRepository");

        // 🔴 FIX 3: Safe Extraction without NullPointerException or Multiple Keys Exception
        Map<String,Object> keys = keyHolder.getKeys();
        if(keys != null && keys.containsKey("id")) {
            Long generatedID = ((Number) keys.get("id")).longValue();
            order.setId(generatedID);
            log.info("ORDER GENERATED ID = {} " , generatedID);
        } else if (keyHolder.getKey() != null) {
            Long generatedID = keyHolder.getKey().longValue();
            order.setId(generatedID);
            log.info("ORDER GENERATED ID = {} " , generatedID);
        }
    }

    //Status Update
    public void updateOrder(Long id, OrderStatus orderStatus,Integer quantity){

        log.trace("Update Request in OrderRepository");

        String sql = """
                update orders
                set order_status = ?,
                quantity = ?
                where id = ?
        """;

        jdbcTemplate.update(
                sql,
                orderStatus.name(),
                quantity,
                id
        );
    }

    public List<Order> findOpenOrders(Long userId) {

        String sql = """
                select
                id,
                user_id,
                symbol,
                quantity,
                price,
                order_type,
                execution_type,
                order_status,
                created_at
                from orders
                where user_id = ?
                and order_status in('PENDING','PARTIALLY_FILLED')
                order by created_at desc
        """;


        List<Order> query = jdbcTemplate.query(sql,
                orderRowMapper,
                userId);

        log.info("Data Come from Database UserId is : {}", userId);

        return query;

    }
    
    public Order findOrderById(Long orderId){
        String sql = """
                select
                id,
                user_id,
                symbol,
                quantity,
                price,
                order_type,
                execution_type,
                order_status,
                created_at
                from orders
                where id = ?
        """;

        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{orderId},
                orderRowMapper
        );

    }


    public int updateOrderStatus(Long id, OrderStatus orderStatus){
        String sql = """
                update orders
                set order_status = ?
                where id = ?
                and order_status = 'PENDING'
                """;

        return jdbcTemplate.update(sql, orderStatus.name(), id);
    }


    public int updateOrderPriceAndQuantity(Long orderId, BigDecimal price, Integer quantity){
        String sql = """
                update orders
                set price = ?, quantity = ?
                where id = ?
                and order_status = 'PENDING'
        """;

        return jdbcTemplate.update(sql,price,quantity,orderId);
    }
}
