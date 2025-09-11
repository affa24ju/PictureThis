package com.PictureThis.PictureThis.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String username = null;
        if (request.getURI().getQuery() != null) {
            for (String param : request.getURI().getQuery().split("&")) {
                if (param.startsWith("user=")) {
                    username = param.substring(5);
                    break;
                }
            }
        }
        if (username == null) {
            username = "anonymous-" + System.currentTimeMillis();
        }
        final String finalUsername = username;
        return new Principal() {
            @Override
            public String getName() {
                return finalUsername;
            }
        };
    }
}
