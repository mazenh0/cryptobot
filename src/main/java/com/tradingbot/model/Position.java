package com.tradingbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "positions")
@IdClass(PositionId.class)
public class Position {
    @Id
    private String ownerId;
    @Id
    private String symbol;
    @Column(precision = 30, scale = 18)
    private BigDecimal quantity;
    @Column(precision = 30, scale = 18)
    private BigDecimal averageEntryPrice;
    private Instant updatedAt;

    protected Position() {}

    public Position(String ownerId, String symbol, BigDecimal quantity, BigDecimal averageEntryPrice) {
        this.ownerId = ownerId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.averageEntryPrice = averageEntryPrice;
        this.updatedAt = Instant.now();
    }

    public String getOwnerId() { return ownerId; }
    public String getSymbol() { return symbol; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getAverageEntryPrice() { return averageEntryPrice; }
    public void buy(BigDecimal quantity, BigDecimal price) {
        BigDecimal currentValue = this.quantity.multiply(this.averageEntryPrice);
        BigDecimal purchaseValue = quantity.multiply(price);
        this.quantity = this.quantity.add(quantity);
        this.averageEntryPrice = currentValue.add(purchaseValue).divide(this.quantity, 18, java.math.RoundingMode.HALF_UP);
        this.updatedAt = Instant.now();
    }
    public void sell(BigDecimal quantity) {
        this.quantity = this.quantity.subtract(quantity);
        this.updatedAt = Instant.now();
    }
}