package com.social.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoders;

@Configuration
public class SecurityConfig {
    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation("http://localhost:9090/realms/social-network");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/report/**").permitAll()
                        .requestMatchers("/api/users/search").permitAll()
                        .requestMatchers("/api/users/*/following-ids").permitAll()
                        .anyRequest().authenticated()
                )
                        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }
}