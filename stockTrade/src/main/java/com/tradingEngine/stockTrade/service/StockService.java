package com.tradingEngine.stockTrade.service;

import com.tradingEngine.stockTrade.DTOs.OrderDTOs.OrderRequestDTO;
import com.tradingEngine.stockTrade.DTOs.Page.PageResponse;
import com.tradingEngine.stockTrade.DTOs.StocksDTOs.CurrentPriceResponseDTO;
import com.tradingEngine.stockTrade.DTOs.StocksDTOs.StockListingDTO;
import com.tradingEngine.stockTrade.DTOs.StocksDTOs.StocksRequestDTO;
import com.tradingEngine.stockTrade.enums.ExecutionType;
import com.tradingEngine.stockTrade.enums.OrderType;
import com.tradingEngine.stockTrade.matchingEngine.MatchingLogic;
import com.tradingEngine.stockTrade.model.Stock;
import com.tradingEngine.stockTrade.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class StockService {

    private static final Long EXCHANGE_SYSTEM_USER_ID = 0L;

    private final StockRepository stockRepository;
    private final OrderService orderService;

    public StockService(StockRepository stockRepository, OrderService orderService) {
        this.stockRepository = stockRepository;
        this.orderService = orderService;
    }

    @Transactional(readOnly = true)
    public CurrentPriceResponseDTO getPrice(String symbol) {
        return stockRepository.getStockCurrentPrice(symbol);
    }

    @Transactional(readOnly = true)
    public Stock getStockInfo(String symbol) {
        return stockRepository.findBySymbol(symbol);
    }

    @Transactional
    public void  listNewStockToMarketAsIPO(StockListingDTO  stockListingDTO) {

        if (stockListingDTO.getCompanyName() == null || stockListingDTO.getCompanyName().isBlank()) {
            throw new IllegalArgumentException("Company name cannot be null or empty!");
        }

        String symbol = stockListingDTO.getSymbol().toUpperCase().trim();

        if(stockRepository.existsStockBySymbol(symbol)){
            throw new RuntimeException("Stock with symbol " + symbol + " is already listed!");
        }

        String cName = stockListingDTO.getCompanyName().trim();
        // create New Stock
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        stock.setCompanyName(cName);
        stock.setCurrentPrice(stockListingDTO.getInitialPrice());
        // save that
       stockRepository.save(stock);

       // build new stock
        OrderRequestDTO requestDTO =  new OrderRequestDTO();
        requestDTO.setUserId(EXCHANGE_SYSTEM_USER_ID);
        requestDTO.setSymbol(symbol);
        requestDTO.setPrice(stockListingDTO.getInitialPrice());
        requestDTO.setQuantity(stockListingDTO.getTotalQuantity());
        requestDTO.setOrderType(OrderType.SELL);
        requestDTO.setExecutionType(ExecutionType.LIMIT);

        // pass to sell order
        orderService.placeSellOrder(requestDTO);
        log.info("New stock has been added to the Database and listed in Sell OrderBook as well 👻");
    }


    @Transactional(readOnly = true)
    public PageResponse<Stock> getStockEntireData(Integer page, Integer pageSize){
        return stockRepository.getStockPage(page, pageSize);
    }
}