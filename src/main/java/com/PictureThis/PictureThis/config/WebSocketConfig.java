package com.PictureThis.PictureThis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeHandler;

import com.PictureThis.PictureThis.JWTsecurity.JWTUtil;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JWTUtil jwtUtil;

    @Bean
    public HandshakeHandler handshakeHandler() {
        CustomHandshakeHandler handshakeHandler = new CustomHandshakeHandler();
        handshakeHandler.setJwtUtil(jwtUtil);
        return handshakeHandler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:5173", 
                        "http://localhost:8080",
                        "https://picturethisfrontend-production.up.railway.app",
                        "https://picturethis-production.up.railway.app"
                )
                .setHandshakeHandler(handshakeHandler())
                .withSockJS();
    }

}
