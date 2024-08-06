package com.backend.liargame.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
public class WebSocketEventListener {

    private final WebSocketService webSocketService;

    public WebSocketEventListener(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String roomCode = headerAccessor.getFirstNativeHeader("roomCode");
        String nickname = headerAccessor.getFirstNativeHeader("nickname");
        if (roomCode != null && nickname != null) {
            headerAccessor.getSessionAttributes().put("roomCode", roomCode);
            headerAccessor.getSessionAttributes().put("nickname", nickname);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String roomCode = (String) headerAccessor.getSessionAttributes().get("roomCode");
        String nickname = (String) headerAccessor.getSessionAttributes().get("nickname");

        log.info("[WebSocketEventListener] - [Disconnect event Occur]");
        log.info("roomCode : " + roomCode);
        log.info("nickname : " + nickname);
        if (roomCode != null && nickname != null) {
            webSocketService.removePlayer(roomCode, nickname);
        }
    }
}
