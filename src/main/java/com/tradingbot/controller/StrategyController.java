package com.tradingbot.controller;

import com.tradingbot.service.MovingAverageStrategyService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/strategy")
public class StrategyController {
    private final MovingAverageStrategyService strategy;

    public StrategyController(MovingAverageStrategyService strategy) {
        this.strategy = strategy;
    }

    @GetMapping
    public Map<String, Object> status() {
        return Map.of("enabled", strategy.isEnabled(), "name", "Moving average crossover", "fastPeriod", 5, "slowPeriod", 20);
    }

    @PostMapping("/enabled")
    public Map<String, Object> setEnabled(@RequestParam boolean enabled) {
        strategy.setEnabled(enabled);
        return status();
    }
}
