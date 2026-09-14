package com.tradingbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingbot.model.CryptoPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;

@Service
public class BinanceWebSocketService {
    
    private static final Logger log = LoggerFactory.getLogger(BinanceWebSocketService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebSocketClient webSocketClient = new ReactorNettyWebSocketClient();
    
    public Flux<CryptoPrice> streamPrices(String symbol) {
        String streamUrl = "wss://stream.binance.com:9443/ws/" + symbol.toLowerCase() + "@ticker";
        
        return Flux.create(sink -> webSocketClient.execute(
            URI.create(streamUrl),
            session -> session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(this::parseBinanceMessage)
                .filter(price -> price != null)
                .doOnNext(price -> {
                    log.info("Received price update: {}", price);
                    sink.next(price);
                })
                .doOnError(sink::error)
                .then()
        ).subscribe(ignored -> sink.complete(), sink::error));
    }
    
    private CryptoPrice parseBinanceMessage(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            CryptoPrice price = new CryptoPrice();
            price.setSymbol(node.get("s").asText());
            price.setPrice(new BigDecimal(node.get("c").asText()));
            price.setVolume(new BigDecimal(node.get("v").asText()));
            price.setChange24h(new BigDecimal(node.get("p").asText()));
            price.setTimestamp(Instant.now());
            price.setExchange("Binance");
            return price;
        } catch (Exception e) {
            log.error("Error parsing Binance message: {}", e.getMessage());
            return null;
        }
    }
}
