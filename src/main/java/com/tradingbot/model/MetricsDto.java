package com.tradingbot.model;

import java.time.Instant;

public class MetricsDto {
    private String exchange;
    private String symbol;
    private Double price;
    private Long volume;
    private Instant timestamp;

    public MetricsDto() {}

    public MetricsDto(String exchange, String symbol, Double price, Long volume, Instant timestamp) {
        this.exchange = exchange;
        this.symbol = symbol;
        this.price = price;
        this.volume = volume;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
