package com.tradingbot.controller;

import com.tradingbot.model.CryptoPrice;
import com.tradingbot.service.BinanceWebSocketService;
import com.tradingbot.service.PriceAggregatorService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController {
    
    private final PriceAggregatorService priceAggregator;
    private final BinanceWebSocketService binanceService;
    
    public CryptoController(PriceAggregatorService priceAggregator, 
                           BinanceWebSocketService binanceService) {
        this.priceAggregator = priceAggregator;
        this.binanceService = binanceService;
    }
    
    @GetMapping("/prices")
    public Mono<Map<String, CryptoPrice>> getAllPrices() {
        return Mono.just(priceAggregator.getLatestPrices());
    }
    
    @GetMapping("/price/{symbol}")
    public Mono<CryptoPrice> getPrice(@PathVariable String symbol) {
        return Mono.justOrEmpty(priceAggregator.getLatestPrice(symbol));
    }
    
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CryptoPrice> streamPrices() {
        return priceAggregator.getPriceStream();
    }
    
    @PostMapping("/track/{symbol}")
    public Mono<String> trackSymbol(@PathVariable String symbol) {
        binanceService.streamPrices(symbol)
            .subscribe(priceAggregator::updatePrice);
        return Mono.just("Started tracking " + symbol);
    }
}
