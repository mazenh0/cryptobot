package com.tradingbot.repository;

import com.tradingbot.model.PriceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceSnapshotRepository extends JpaRepository<PriceSnapshot, Long> {}