package com.tradingbot.repository;

import com.tradingbot.model.TradeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeOrderRepository extends JpaRepository<TradeOrder, Long> {}