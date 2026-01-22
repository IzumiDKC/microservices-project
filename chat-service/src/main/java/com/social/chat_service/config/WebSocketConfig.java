package com.social.chat_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Frontend sẽ kết nối vào đường dẫn này: ws://localhost:8084/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Cho phép Angular kết nối
                .withSockJS(); // Hỗ trợ fallback nếu trình duyệt không có WS
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix cho các tin nhắn từ Server gửi xuống Client
        registry.enableSimpleBroker("/user");

        // Prefix cho các tin nhắn từ Client gửi lên Server
        registry.setApplicationDestinationPrefixes("/app");

        // Prefix dành riêng cho tin nhắn cá nhân (1-1)
        registry.setUserDestinationPrefix("/user");
    }
}