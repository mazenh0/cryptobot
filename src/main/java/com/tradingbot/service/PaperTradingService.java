package com.tradingbot.service;

import com.tradingbot.model.*;
import com.tradingbot.repository.PortfolioAccountRepository;
import com.tradingbot.repository.PositionRepository;
import com.tradingbot.repository.TradeOrderRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaperTradingService {
    private final TradeOrderRepository orderRepository;
    private final PortfolioAccountRepository accountRepository;
    private final PositionRepository positionRepository;
    private final BigDecimal startingCash;

    public PaperTradingService(TradeOrderRepository orderRepository,
                               PortfolioAccountRepository accountRepository,
                               PositionRepository positionRepository,
                               @Value("${trading.paper.starting-cash:10000}") BigDecimal startingCash) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
        this.positionRepository = positionRepository;
        this.startingCash = startingCash;
    }

    @PostConstruct
    void initializeAccount() {
        accountRepository.findById(1L).orElseGet(() -> accountRepository.save(new PortfolioAccount(startingCash)));
    }

    @Transactional
    public TradeOrder execute(OrderRequest request, CryptoPrice marketPrice) {
        String symbol = request.symbol().toUpperCase();
        BigDecimal price = marketPrice == null ? null : marketPrice.getPrice();
        if (price == null || !symbol.equalsIgnoreCase(marketPrice.getSymbol())) {
            return orderRepository.save(rejected(symbol, request, "No current market price is available"));
        }

        BigDecimal notional = request.quantity().multiply(price);
        PortfolioAccount account = accountRepository.findById(1L).orElseThrow();
        Position position = positionRepository.findById(symbol).orElse(null);
        if (request.side() == OrderSide.BUY && account.getCashBalance().compareTo(notional) < 0) {
            return orderRepository.save(rejected(symbol, request, "Insufficient cash balance"));
        }
        if (request.side() == OrderSide.SELL && (position == null || position.getQuantity().compareTo(request.quantity()) < 0)) {
            return orderRepository.save(rejected(symbol, request, "Insufficient position quantity"));
        }

        if (request.side() == OrderSide.BUY) {
            account.debit(notional);
            if (position == null) position = new Position(symbol, BigDecimal.ZERO, BigDecimal.ZERO);
            position.buy(request.quantity(), price);
            positionRepository.save(position);
        } else {
            account.credit(notional);
            position.sell(request.quantity());
            if (position.getQuantity().signum() == 0) positionRepository.delete(position);
            else positionRepository.save(position);
        }
        accountRepository.save(account);
        return orderRepository.save(new TradeOrder(symbol, request.side(), request.quantity(), price,
            notional, OrderStatus.FILLED, null));
    }

    private TradeOrder rejected(String symbol, OrderRequest request, String reason) {
        return new TradeOrder(symbol, request.side(), request.quantity(), null, BigDecimal.ZERO,
            OrderStatus.REJECTED, reason);
    }

    public Iterable<TradeOrder> getOrders() { return orderRepository.findAll(); }
}