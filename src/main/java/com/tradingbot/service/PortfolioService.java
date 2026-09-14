package com.tradingbot.service;

import com.tradingbot.model.*;
import com.tradingbot.repository.PortfolioAccountRepository;
import com.tradingbot.repository.PositionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PortfolioService {
    private final PortfolioAccountRepository accountRepository;
    private final PositionRepository positionRepository;
    private final PriceAggregatorService prices;

    public PortfolioService(PortfolioAccountRepository accountRepository, PositionRepository positionRepository,
                            PriceAggregatorService prices) {
        this.accountRepository = accountRepository;
        this.positionRepository = positionRepository;
        this.prices = prices;
    }

    public PortfolioSummary getSummary() {
        BigDecimal marketValue = BigDecimal.ZERO;
        List<PortfolioSummary.PositionView> views = new java.util.ArrayList<>();
        for (Position position : positionRepository.findAll()) {
            CryptoPrice quote = prices.getLatestPrice(position.getSymbol());
            BigDecimal marketPrice = quote == null ? BigDecimal.ZERO : quote.getPrice();
            BigDecimal value = position.getQuantity().multiply(marketPrice);
            BigDecimal pnl = marketPrice.subtract(position.getAverageEntryPrice()).multiply(position.getQuantity());
            marketValue = marketValue.add(value);
            views.add(new PortfolioSummary.PositionView(position.getSymbol(), position.getQuantity(),
                position.getAverageEntryPrice(), marketPrice, value, pnl));
        }
        BigDecimal cash = accountRepository.findById(1L).orElseThrow().getCashBalance();
        return new PortfolioSummary(cash, marketValue, cash.add(marketValue), views);
    }
}