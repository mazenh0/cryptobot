package com.tradingbot.repository;

import com.tradingbot.model.Position;
import com.tradingbot.model.PositionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, PositionId> {
	java.util.List<Position> findByOwnerId(String ownerId);
	java.util.Optional<Position> findByOwnerIdAndSymbol(String ownerId, String symbol);
}