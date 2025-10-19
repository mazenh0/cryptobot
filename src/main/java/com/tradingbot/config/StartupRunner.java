package com.tradingbot.config;

import com.tradingbot.service.BinanceWebSocketService;
import com.tradingbot.service.PriceAggregatorService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StartupRunner {
    
    private static final Logger log = LoggerFactory.getLogger(StartupRunner.class);
    private final BinanceWebSocketService binanceService;
    private final PriceAggregatorService priceAggregator;
    
    private final List<String> DEFAULT_SYMBOLS = List.of("btcusdt", "ethusdt", "bnbusdt");
    
    public StartupRunner(BinanceWebSocketService binanceService, 
                        PriceAggregatorService priceAggregator) {
        this.binanceService = binanceService;
        this.priceAggregator = priceAggregator;
    }
    
    @PostConstruct
    public void startTracking() {
        log.info("Starting to track default crypto symbols: {}", DEFAULT_SYMBOLS);
        
        DEFAULT_SYMBOLS.forEach(symbol -> {
            binanceService.streamPrices(symbol)
                .subscribe(
                    priceAggregator::updatePrice,
                    error -> log.error("Error tracking {}: {}", symbol, error.getMessage())
                );
        });
    }
}
