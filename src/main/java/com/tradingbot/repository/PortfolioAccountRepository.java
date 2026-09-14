package com.tradingbot.repository;

import com.tradingbot.model.PortfolioAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioAccountRepository extends JpaRepository<PortfolioAccount, Long> {}