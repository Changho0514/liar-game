package com.backend.liargame.chat;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, List<String>> roomPlayers = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/{roomCode}")
    public void sendMessage(@DestinationVariable String roomCode, @Payload Map<String, String> payload) {
        String message = payload.get("message");
        messagingTemplate.convertAndSend("/topic/room/" + roomCode, message);
    }

    @MessageMapping("/chat/{roomCode}/join")
    public void joinRoom(@DestinationVariable String roomCode, @Payload Map<String, String> payload) {
        String nickname = payload.get("nickname");
        System.out.println("/chat/{roomCode}/join -> nickname = " + nickname);
        roomPlayers.putIfAbsent(roomCode, new CopyOnWriteArrayList<>());
        roomPlayers.get(roomCode).add(nickname);

        try {
            String playerListJson = objectMapper.writeValueAsString(roomPlayers.get(roomCode));
            messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/players", playerListJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Player joined: " + nickname + " in room: " + roomCode);
    }

    @MessageMapping("/chat/{roomCode}/leave")
    public void leaveRoom(@DestinationVariable String roomCode, @Payload Map<String, String> payload) {
        String nickname = payload.get("nickname");
        if (roomPlayers.containsKey(roomCode)) {
            roomPlayers.get(roomCode).remove(nickname);

            try {
                String playerListJson = objectMapper.writeValueAsString(roomPlayers.get(roomCode));
                messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/players", playerListJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Player left: " + nickname + " from room: " + roomCode);
        }
    }
}
