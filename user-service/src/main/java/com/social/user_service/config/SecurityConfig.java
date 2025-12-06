package com.social.user_service.config;

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
                        // Cho phép tải báo cáo user tự do
                        .requestMatchers("/api/users/report/**").permitAll()
                        .requestMatchers("/api/users/search").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}