package com.tradingbot.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "trade_orders")
public class TradeOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ownerId;
    private String symbol;
    @Enumerated(EnumType.STRING)
    private OrderSide side;
    @Column(precision = 30, scale = 18)
    private BigDecimal quantity;
    @Column(precision = 30, scale = 18)
    private BigDecimal executedPrice;
    @Column(precision = 30, scale = 18)
    private BigDecimal notional;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private String rejectionReason;
    private Instant createdAt;

    protected TradeOrder() {}

    public TradeOrder(String ownerId, String symbol, OrderSide side, BigDecimal quantity, BigDecimal executedPrice,
                      BigDecimal notional, OrderStatus status, String rejectionReason) {
        this.ownerId = ownerId;
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.executedPrice = executedPrice;
        this.notional = notional;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getOwnerId() { return ownerId; }
    public String getSymbol() { return symbol; }
    public OrderSide getSide() { return side; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getExecutedPrice() { return executedPrice; }
    public BigDecimal getNotional() { return notional; }
    public OrderStatus getStatus() { return status; }
    public String getRejectionReason() { return rejectionReason; }
    public Instant getCreatedAt() { return createdAt; }
}