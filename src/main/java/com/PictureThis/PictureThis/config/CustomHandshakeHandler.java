package com.PictureThis.PictureThis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.PictureThis.PictureThis.JWTsecurity.JWTUtil;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {
    @Autowired
    private JWTUtil jwtUtil;

    public void setJwtUtil(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        System.out.println("Query string: " + request.getURI().getQuery());
        String user = null;
        String userName = null;
        if (request.getURI().getQuery() != null) {
            for (String param : request.getURI().getQuery().split("&")) {
                if (param.startsWith("user=")) {
                    user = param.substring(5);
                    break;
                }
            }
        }
        if (user != null) {
            System.out.println("Token från query: " + user);
            try {
                userName = jwtUtil.extractUserId(user);
                System.out.println("Extracted userName: " + userName);
            } catch (Exception e) {
                System.out.println("Misslyckades att extrahera userName från token: " + e.getMessage());
                userName = null;
            }
        }
        if (userName == null) {
            System.out.println("Ingen giltig userName, sätter anonymous.");
            userName = "anonymous-" + System.currentTimeMillis();
        }
        final String finalUsername = userName;
        return new Principal() {
            @Override
            public String getName() {
                return finalUsername;
            }
        };
    }

}
