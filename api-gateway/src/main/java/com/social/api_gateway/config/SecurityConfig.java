package com.social.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity
                // Tắt CSRF vì dùng API Stateless (Token)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/eureka/**").permitAll()
                        // Yêu cầu xác thực với tất cả các API còn lại
                        .pathMatchers("/api/**").authenticated()
                        .anyExchange().authenticated()
                )
                // Kích hoạt OAuth2 Resource Server để kiểm tra JWT Token
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));

        return serverHttpSecurity.build();
    }
}