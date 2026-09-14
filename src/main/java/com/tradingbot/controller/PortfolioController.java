package com.tradingbot.controller;

import com.tradingbot.model.PortfolioSummary;
import com.tradingbot.service.PortfolioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
    private final PortfolioService portfolio;

    public PortfolioController(PortfolioService portfolio) {
        this.portfolio = portfolio;
    }

    @GetMapping
    public PortfolioSummary getPortfolio() {
        return portfolio.getSummary();
    }
}