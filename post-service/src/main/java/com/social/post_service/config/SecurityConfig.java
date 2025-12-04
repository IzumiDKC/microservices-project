package com.social.post_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order; // Quan trọng
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    // --- CHAIN 1: CẤU HÌNH RIÊNG  CAMUNDA (Ưu tiên chạy trước)
    @Bean
    @Order(1)
    public SecurityFilterChain camundaSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // Chỉ áp dụng cấu hình này cho các đường dẫn của Camunda
                .securityMatcher(
                        "/camunda/**",
                        "/camunda-welcome",
                        "/assets/**",
                        "/webjars/**",
                        "/lib/**",
                        "/api/engine/**", // API nội bộ
                        "/api/admin/**"   // API quản trị
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Cho phép truy cập hết (Camunda sẽ tự hiện form login)
                )
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Để hiển thị tốt các khung UI

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        return http.build();
    }

    //  CHAIN 2: CẤU HÌNH CHO API NGHIỆP VỤ
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/reports/**").permitAll()
                        .anyRequest().authenticated()
                )
                //  JWT Token
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                // Tắt Session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}