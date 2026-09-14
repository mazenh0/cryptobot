package com.tradingbot.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "price_snapshots")
public class PriceSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol;
    private BigDecimal price;
    private BigDecimal volume;
    private BigDecimal change24h;
    private Instant timestamp;
    private String exchange;

    protected PriceSnapshot() {}

    public PriceSnapshot(CryptoPrice price) {
        this.symbol = price.getSymbol();
        this.price = price.getPrice();
        this.volume = price.getVolume();
        this.change24h = price.getChange24h();
        this.timestamp = price.getTimestamp();
        this.exchange = price.getExchange();
    }
}