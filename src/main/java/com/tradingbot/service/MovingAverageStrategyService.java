package com.tradingbot.service;

import com.tradingbot.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MovingAverageStrategyService {
    private final PaperTradingService trading;
    private final Map<String, Deque<BigDecimal>> prices = new ConcurrentHashMap<>();
    private final Set<String> enabledUsers = ConcurrentHashMap.newKeySet();
    private final Map<String, Map<String, OrderSide>> signals = new ConcurrentHashMap<>();

    public MovingAverageStrategyService(PaperTradingService trading,
                                        @Value("${trading.strategy.enabled:false}") boolean enabled) {
        this.trading = trading;
        if (enabled) enabledUsers.add("default");
    }

    public void onPrice(CryptoPrice price) {
        if (price == null || enabledUsers.isEmpty()) return;
        Deque<BigDecimal> history = prices.computeIfAbsent(price.getSymbol(), ignored -> new ArrayDeque<>());
        history.addLast(price.getPrice());
        while (history.size() > 20) history.removeFirst();
        if (history.size() < 20) return;

        BigDecimal fast = average(history, 5);
        BigDecimal slow = average(history, 20);
        OrderSide signal = fast.compareTo(slow) > 0 ? OrderSide.BUY : OrderSide.SELL;
        for (String ownerId : enabledUsers) {
            Map<String, OrderSide> userSignals = signals.computeIfAbsent(ownerId, ignored -> new ConcurrentHashMap<>());
            if (signal != userSignals.put(price.getSymbol(), signal)) {
                BigDecimal quantity = new BigDecimal("0.001");
                trading.execute(ownerId, new OrderRequest(price.getSymbol(), signal, quantity), price);
            }
        }
    }

    public boolean isEnabled(String ownerId) {
        return enabledUsers.contains(ownerId);
    }

    public void setEnabled(String ownerId, boolean enabled) {
        if (enabled) enabledUsers.add(ownerId);
        else enabledUsers.remove(ownerId);
    }

    private BigDecimal average(Deque<BigDecimal> history, int period) {
        return history.stream().skip(history.size() - period).reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(period), 18, java.math.RoundingMode.HALF_UP);
    }
}