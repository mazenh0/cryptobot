package com.tradingbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Value("${app.security.supabase.enabled:false}")
    private boolean supabaseEnabled;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        if (supabaseEnabled) {
            return http
                .authorizeExchange(exchanges -> exchanges
                    .pathMatchers("/actuator/health", "/actuator/health/**").permitAll()
                    .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
                .build();
        }
        return http.authorizeExchange(exchanges -> exchanges.anyExchange().permitAll()).build();
    }
}
