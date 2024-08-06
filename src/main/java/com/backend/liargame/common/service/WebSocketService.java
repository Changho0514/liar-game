package com.backend.liargame.common.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, CopyOnWriteArrayList<String>> roomPlayers = new ConcurrentHashMap<>();

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void addPlayer(String roomCode, String nickname) {
        roomPlayers.putIfAbsent(roomCode, new CopyOnWriteArrayList<>());
        roomPlayers.get(roomCode).add(nickname);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/players", roomPlayers.get(roomCode));
    }

    public void removePlayer(String roomCode, String nickname) {
        CopyOnWriteArrayList<String> players = roomPlayers.get(roomCode);
        if (players != null) {
            players.remove(nickname);
            messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/players", players);
        }
    }

    public int getPlayerCount(String roomCode) {
        CopyOnWriteArrayList<String> players = roomPlayers.get(roomCode);
        return (players != null) ? players.size() : 0;
    }

    public void startGame(String roomCode, int liarIndex, String topic, String keyword){
        CopyOnWriteArrayList<String> players = this.getPlayers(roomCode);

        // 라이어에게 메시지 전송
        String liar = players.get(liarIndex);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/gameStart/" + liar, "당신은 라이어입니다. 주제는 \"" + topic + "\"입니다.");

        // 나머지 플레이어에게 메시지 전송
        for (int i = 0; i < players.size(); i++) {
            if (i != liarIndex) {
                String player = players.get(i);
                messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/gameStart/" + player, "주제는 \"" + topic + "\"이며 제시어는 \"" + keyword + "\"입니다. 라이어를 찾아주세요.");
            }
        }
    }

    public CopyOnWriteArrayList<String> getPlayers(String roomCode) {
        return roomPlayers.get(roomCode);
    }
}
