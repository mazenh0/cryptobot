package com.tradingbot.model;

import java.io.Serializable;
import java.util.Objects;

public class PositionId implements Serializable {
    private String ownerId;
    private String symbol;

    public PositionId() {}

    public PositionId(String ownerId, String symbol) {
        this.ownerId = ownerId;
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof PositionId that)) return false;
        return Objects.equals(ownerId, that.ownerId) && Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId, symbol);
    }
}