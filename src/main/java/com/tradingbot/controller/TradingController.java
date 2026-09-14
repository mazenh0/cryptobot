package com.tradingbot.controller;

import com.tradingbot.model.CryptoPrice;
import com.tradingbot.model.OrderRequest;
import com.tradingbot.model.TradeOrder;
import com.tradingbot.service.PaperTradingService;
import com.tradingbot.service.PriceAggregatorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trading")
public class TradingController {
    private final PaperTradingService trading;
    private final PriceAggregatorService prices;

    public TradingController(PaperTradingService trading, PriceAggregatorService prices) {
        this.trading = trading;
        this.prices = prices;
    }

    @PostMapping("/orders")
    public TradeOrder placeOrder(@Valid @RequestBody OrderRequest request) {
        CryptoPrice price = prices.getLatestPrice(request.symbol());
        return trading.execute(request, price);
    }

    @GetMapping("/orders")
    public Iterable<TradeOrder> getOrders() {
        return trading.getOrders();
    }
}