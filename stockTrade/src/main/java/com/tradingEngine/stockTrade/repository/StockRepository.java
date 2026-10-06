package com.tradingEngine.stockTrade.repository;

import com.tradingEngine.stockTrade.DTOs.Page.PageResponse;
import com.tradingEngine.stockTrade.DTOs.StocksDTOs.CurrentPriceResponseDTO;
import com.tradingEngine.stockTrade.DTOs.StocksDTOs.StocksRequestDTO;
import com.tradingEngine.stockTrade.Mapper.StockRowMapper;
import com.tradingEngine.stockTrade.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class StockRepository {

    private static final Logger log = LoggerFactory.getLogger(StockRepository.class);
    private final JdbcTemplate jdbcTemplate;

    private final StockRowMapper stockRowMapper = new StockRowMapper();

    public StockRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void save(Stock stock) {
        String sql = "INSERT INTO stocks (symbol, companyname, current_price) VALUES (?, ?, ?)";

        jdbcTemplate.update(
                sql,
                stock.getSymbol(),
                stock.getCompanyName(),
                stock.getCurrentPrice()
        );
    }

    public boolean existsStockBySymbol (String symbol){
        String sql = "select count(*) from stocks where symbol = ?";
        Integer count = jdbcTemplate.queryForObject(sql,Integer.class,symbol);
        return count > 0;
    }

    public Stock findBySymbol(String symbol){

        String sql = """
                SELECT
                id,
                companyname,
                symbol,
                current_price
                FROM stocks
                WHERE symbol = ?
                """;

        return jdbcTemplate.queryForObject(sql, stockRowMapper, symbol.toUpperCase());
    }

    public void updateCurrentPrice(String symbol, BigDecimal newPrice){
        String sql = """
                UPDATE stocks
                set current_price = ?
                where symbol = ?
        """;
        jdbcTemplate.update(sql, newPrice, symbol.toUpperCase());
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


    public PageResponse<Stock> getStockPage(int page, int pageSize){
        // calculate offSet
        int offSet = page * pageSize;

        String countSql = """
                select count(*) from stocks
        """;

        Long elementCount = jdbcTemplate.queryForObject(countSql, Long.class);
        if(elementCount == null) elementCount = 0L;

        String sql = """
                select
                 id,
                companyname,
                symbol,
                current_price
                FROM stocks
                order by id desc
                limit ? offset ?
        """;

        List<Stock> content = jdbcTemplate.query(sql, stockRowMapper, pageSize, offSet);

        int totalPages = (int) Math.ceil((double) elementCount / pageSize);
        PageResponse<Stock> response = new PageResponse<>();
        response.setTotalPages(totalPages);
        response.setContent(content);
        response.setPageNumber(page);
        response.setPageSize(pageSize);
        response.setTotalElements(elementCount);

        return response;

    }

    // get Current price
    public BigDecimal getCurrentPrice(String symbol){
        String sql = """
                select current_price from stocks where symbol = ?
                """;

        return jdbcTemplate.queryForObject(sql,BigDecimal.class,symbol.toUpperCase());
    }
}
