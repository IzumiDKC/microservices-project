package com.social.post_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/reports/**").permitAll()
                        .anyRequest().authenticated() // Tất cả API đều cần Token
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {})); // đọc JWT
        return http.build();
    }
}