package com.backend.liargame.common.service;

import com.backend.liargame.game.dto.GameCreateWebSocketResponseDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, CopyOnWriteArrayList<String>> roomPlayers = new ConcurrentHashMap<>();
    private final Map<String, Integer> liarPlayer = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<String>> topicAndKeyword = new ConcurrentHashMap<>();

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
        liarPlayer.put(roomCode, liarIndex);

        topicAndKeyword.put(roomCode, new CopyOnWriteArrayList<>());
        topicAndKeyword.get(roomCode).add(topic);
        topicAndKeyword.get(roomCode).add(keyword);
        GameCreateWebSocketResponseDTO liarDto = new GameCreateWebSocketResponseDTO("당신은 라이어입니다. <br>주제는 \"" + topic + "\"입니다.", players);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/gameStart/" + liar, liarDto);

        GameCreateWebSocketResponseDTO normalDto = new GameCreateWebSocketResponseDTO("주제는 \"" + topic + "\"이며 <br>제시어는 \"" + keyword + "\"입니다. <br>라이어를 찾아주세요.", players);
        // 나머지 플레이어에게 메시지 전송
        for (int i = 0; i < players.size(); i++) {
            if (i != liarIndex) {
                String player = players.get(i);

                messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/gameStart/" + player, normalDto);
            }
        }
    }

    public CopyOnWriteArrayList<String> getPlayers(String roomCode) {
        return roomPlayers.get(roomCode);
    }

    public void endGame(String roomCode, String mostVotedPlayer) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/gameEnd", mostVotedPlayer);
    }

    public void updateVotes(String roomCode, Map<String, Integer> votes) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/voteUpdate", votes);
        System.out.println("Vote update sent: " + votes); // 로그 추가
    }

    public void convertAndSend(String topic, Map<String, String> voteResults) {
        messagingTemplate.convertAndSend(topic, voteResults);
    }

    public void convertAndSend(String topic, String s) {
        messagingTemplate.convertAndSend(topic, s);
    }

    public Integer getLiarIndex(String roomCode) {
        return liarPlayer.get(roomCode);
    }

    public List<String> getKeyword(String roomCode) {
        CopyOnWriteArrayList<String> topicAndKeywords = topicAndKeyword.get(roomCode);
        List<String> answer = new ArrayList<>();
        answer.add(topicAndKeywords.get(0));
        answer.add(topicAndKeywords.get(1));

        return answer;
    }
}
