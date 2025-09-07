package com.PictureThis.PictureThis.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import com.PictureThis.PictureThis.user.dto.UserDto;
import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketEventListener {

    @Autowired
    private ChatService chatService;

    private final Map<String, String> sessionIdToUsername = new HashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String username = sha.getFirstNativeHeader("user");
        String sessionId = sha.getSessionId();
        if (username != null && sessionId != null) {
            sessionIdToUsername.put(sessionId, username);
            System.out.println("User connected: " + username + " (sessionId: " + sessionId + ")");
            chatService.playerJoined(new UserDto(null, username));
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();
        if (sessionId != null) {
            String username = sessionIdToUsername.remove(sessionId);
            if (username != null) {
                System.out.println("User disconnected: " + username + " (sessionId: " + sessionId + ")");
                chatService.playerLeft(new UserDto(null, username));
            } else {
                System.out.println("Unknown user disconnected (sessionId: " + sessionId + ")");
            }
        }
    }
}
