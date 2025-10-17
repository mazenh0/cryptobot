package com.tradingbot.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
private final String apiKey;
public SecurityConfig(org.springframework.core.env.Environment env) {
this.apiKey = env.getProperty("security.api-key", "change-me");
}


@Bean
public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
return http
.csrf(ServerHttpSecurity.CsrfSpec::disable)
.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
.formLogin(ServerHttpSecurity.FormLoginSpec::disable)
.authorizeExchange(reg -> reg
.pathMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
.pathMatchers(HttpMethod.GET, "/api/v1/signals/stream").permitAll() // demo SSE open
.anyExchange().authenticated())
.addFilterAt((exchange, chain) -> authenticate(exchange).flatMap(authed -> authed ? chain.filter(exchange) :
Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED))),
org.springframework.security.web.server.SecurityWebFiltersOrder.AUTHENTICATION)
.build();
}


private Mono<Boolean> authenticate(ServerWebExchange exchange) {
var key = exchange.getRequest().getHeaders().getFirst("X-API-Key");
return Mono.just(apiKey.equals(key));
}
}