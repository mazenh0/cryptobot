package com.tradingbot.model;

import java.math.BigDecimal;
import java.time.Instant;

public class CryptoPrice {
    private String symbol;
    private BigDecimal price;
    private BigDecimal volume;
    private BigDecimal change24h;
    private Instant timestamp;
    private String exchange;

    public CryptoPrice() {}

    public CryptoPrice(String symbol, BigDecimal price, BigDecimal volume, 
                      BigDecimal change24h, Instant timestamp, String exchange) {
        this.symbol = symbol;
        this.price = price;
        this.volume = volume;
        this.change24h = change24h;
        this.timestamp = timestamp;
        this.exchange = exchange;
    }

    // Getters and setters
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public BigDecimal getVolume() { return volume; }
    public void setVolume(BigDecimal volume) { this.volume = volume; }
    
    public BigDecimal getChange24h() { return change24h; }
    public void setChange24h(BigDecimal change24h) { this.change24h = change24h; }
    
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    
    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }
}
