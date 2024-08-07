package com.backend.liargame.game.service;

import com.backend.liargame.common.service.WebSocketService;
import com.backend.liargame.game.dto.*;
import com.backend.liargame.game.entity.*;
import com.backend.liargame.game.repository.GameRepository;
import com.backend.liargame.game.repository.KeywordRepository;
import com.backend.liargame.game.repository.TopicRepository;
import com.backend.liargame.member.entity.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final TopicRepository topicRepository;
    private final KeywordRepository keywordRepository;

    private final WebSocketService webSocketService;
    private final Map<String, GameStatus> gameStatusMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> votes = new ConcurrentHashMap<>();

    public GameService(GameRepository gameRepository, TopicRepository topicRepository, KeywordRepository keywordRepository, WebSocketService webSocketService) {
        this.gameRepository = gameRepository;
        this.topicRepository = topicRepository;
        this.keywordRepository = keywordRepository;
        this.webSocketService = webSocketService;
    }

    public GameStartResponseDTO startGame(String roomCode) {

        if (gameStatusMap.getOrDefault(roomCode, GameStatus.WAITING) == GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("이미 게임이 진행중입니다.");
        }

        CopyOnWriteArrayList<String> players = webSocketService.getPlayers(roomCode);

        int liarIndex = new Random().nextInt(players.size());

        // 랜덤한 Topic 가져오기
        Topic topic = topicRepository.findRandomTopic()
                .orElseThrow(() -> new RuntimeException("No Topic found"));

        // 해당 토픽에 속하는 랜덤한 keyword 가져오기
        String keyword = keywordRepository.findRandomKeywordByTopicId(topic.getId())
                .map(Keyword::getName)
                .orElse("실패한 지시어 입니다.");

        webSocketService.startGame(roomCode, liarIndex, topic.getName(), keyword);
        gameStatusMap.put(roomCode, GameStatus.IN_PROGRESS); // 시작 상태로 변경

        GameCreateDTO game = new GameCreateDTO(liarIndex, topic.getName(), keyword);
        return new GameStartResponseDTO(game, players);
    }

    public GameStatus getGameStatus(String roomCode) {
        return gameStatusMap.getOrDefault(roomCode, GameStatus.WAITING);
    }


    public void recordVote(Long gameId, Long voterId, Long votedForId) {
        Game game = gameRepository.findById(gameId).orElseThrow(IllegalArgumentException::new);
        Player voter = game.getPlayers().stream().filter(p -> p.getId().equals(voterId)).findFirst().orElse(null);
        Player votedFor = game.getPlayers().stream().filter(p -> p.getId().equals(votedForId)).findFirst().orElse(null);

        if (voter != null && votedFor != null) {
            game.getVotes().add(new Vote(voter, votedFor));
        }
    }

    public void submitVote(String roomCode, String voter, String votee) {
        votes.computeIfAbsent(roomCode, k -> new ConcurrentHashMap<>()).put(voter, votee);
        log.info("[GameService - submitVote] - 총 투표 사이즈 : " + votes.get(roomCode).size());
    }

    public void endGame(String roomCode) {
        Map<String, String> roomVotes = votes.get(roomCode);
        if (roomVotes != null) {
            // Count votes
            Map<String, Long> voteCounts = roomVotes.values().stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            // Determine the player with the most votes
            String mostVotedPlayer = Collections.max(voteCounts.entrySet(), Map.Entry.comparingByValue()).getKey();

            // Notify players of the game result
            webSocketService.endGame(roomCode, mostVotedPlayer);

            gameStatusMap.put(roomCode, GameStatus.WAITING);
        }
    }
    // 새로운 투표 메시지 매핑 추가
    public void handleVote(String roomCode, VoteRequest voteRequest) {

        String voter = voteRequest.name();
        String votee = voteRequest.vote();

        // 투표 결과 업데이트
        votes.computeIfAbsent(roomCode, k -> new ConcurrentHashMap<>()).put(voter, votee);

        // 실시간 투표 결과 전송
        Map<String, String> voteResults = votes.get(roomCode);
        log.info("Sending vote results: " + voteResults);
        webSocketService.convertAndSend("/topic/room/" + roomCode + "/voteUpdate", voteResults);
    }



    private PlayerDTO convertToDTO(Player player) {
        return new PlayerDTO(player.getId(), player.getName());
    }

    private VoteDTO convertToDTO(Vote vote) {
        PlayerDTO voter = convertToDTO(vote.getVoter());
        PlayerDTO votedFor = convertToDTO(vote.getVotedFor());
        return new VoteDTO(voter, votedFor);
    }


}
