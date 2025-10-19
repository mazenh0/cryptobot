package com.tradingbot.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler {
    
    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<Map<String, Object>>> onValidation(ConstraintViolationException e) {
        return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "validation_failed", "details", e.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> onAny(Exception e) {
        return Mono.just(ResponseEntity.internalServerError().body(Map.of("error", "internal_error")));
    }
}
