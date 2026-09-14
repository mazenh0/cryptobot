package com.tradingbot.model;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioSummary(
    BigDecimal cashBalance,
    BigDecimal marketValue,
    BigDecimal totalValue,
    List<PositionView> positions
) {
    public record PositionView(String symbol, BigDecimal quantity, BigDecimal averageEntryPrice,
                               BigDecimal marketPrice, BigDecimal marketValue, BigDecimal unrealizedPnl) {}
}