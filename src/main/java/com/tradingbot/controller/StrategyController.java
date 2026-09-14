package com.tradingbot.controller;

import com.tradingbot.service.MovingAverageStrategyService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.Map;

@RestController
@RequestMapping("/api/strategy")
public class StrategyController {
    private final MovingAverageStrategyService strategy;

    public StrategyController(MovingAverageStrategyService strategy) {
        this.strategy = strategy;
    }

    @GetMapping
    public Map<String, Object> status(Authentication authentication) {
        return Map.of("enabled", strategy.isEnabled(authentication.getName()), "name", "Moving average crossover", "fastPeriod", 5, "slowPeriod", 20);
    }

    @PostMapping("/enabled")
    public Map<String, Object> setEnabled(@RequestParam boolean enabled, Authentication authentication) {
        strategy.setEnabled(authentication.getName(), enabled);
        return status(authentication);
    }
}
