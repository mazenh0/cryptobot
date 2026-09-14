package com.tradingbot.service;

import com.tradingbot.model.CryptoPrice;
import com.tradingbot.model.PriceSnapshot;
import com.tradingbot.repository.PriceSnapshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PriceAggregatorService {
    
    private static final Logger log = LoggerFactory.getLogger(PriceAggregatorService.class);
    private final Map<String, CryptoPrice> latestPrices = new ConcurrentHashMap<>();
    private final Sinks.Many<CryptoPrice> priceSink = Sinks.many().multicast().onBackpressureBuffer();
    private final PriceSnapshotRepository snapshotRepository;

    public PriceAggregatorService(PriceSnapshotRepository snapshotRepository) {
        this.snapshotRepository = snapshotRepository;
    }
    
    public void updatePrice(CryptoPrice price) {
        if (price != null) {
            latestPrices.put(price.getSymbol(), price);
            snapshotRepository.save(new PriceSnapshot(price));
            priceSink.tryEmitNext(price);
        }
    }
    
    public Flux<CryptoPrice> getPriceStream() {
        return priceSink.asFlux();
    }
    
    public Map<String, CryptoPrice> getLatestPrices() {
        return new ConcurrentHashMap<>(latestPrices);
    }
    
    public CryptoPrice getLatestPrice(String symbol) {
        return latestPrices.get(symbol.toUpperCase());
    }
}
