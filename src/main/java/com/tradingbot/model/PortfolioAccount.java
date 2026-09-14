package com.tradingbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "portfolio_account")
public class PortfolioAccount {
    @Id
    private Long id;
    @Column(precision = 30, scale = 18)
    private BigDecimal cashBalance;
    private Instant updatedAt;

    protected PortfolioAccount() {}

    public PortfolioAccount(BigDecimal cashBalance) {
        this.id = 1L;
        this.cashBalance = cashBalance;
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public BigDecimal getCashBalance() { return cashBalance; }
    public void debit(BigDecimal amount) { cashBalance = cashBalance.subtract(amount); updatedAt = Instant.now(); }
    public void credit(BigDecimal amount) { cashBalance = cashBalance.add(amount); updatedAt = Instant.now(); }
}