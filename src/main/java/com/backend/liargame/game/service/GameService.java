package com.backend.liargame.game.service;

import com.backend.liargame.common.service.WebSocketService;
import com.backend.liargame.game.dto.GameCreateDTO;
import com.backend.liargame.game.dto.GameDTO;
import com.backend.liargame.game.dto.PlayerDTO;
import com.backend.liargame.game.dto.VoteDTO;
import com.backend.liargame.game.entity.*;
import com.backend.liargame.game.repository.GameRepository;
import com.backend.liargame.game.repository.KeywordRepository;
import com.backend.liargame.game.repository.TopicRepository;
import com.backend.liargame.member.entity.Player;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final TopicRepository topicRepository;
    private final KeywordRepository keywordRepository;

    private final WebSocketService webSocketService;
    private final Map<String, GameStatus> gameStatusMap = new ConcurrentHashMap<>();

    public GameService(GameRepository gameRepository, TopicRepository topicRepository, KeywordRepository keywordRepository, WebSocketService webSocketService) {
        this.gameRepository = gameRepository;
        this.topicRepository = topicRepository;
        this.keywordRepository = keywordRepository;
        this.webSocketService = webSocketService;
    }

    public GameCreateDTO startGame(String roomCode){

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
        return new GameCreateDTO(liarIndex, topic.getName(), keyword);
    }

    public GameStatus getGameStatus(String roomCode) {
        return gameStatusMap.getOrDefault(roomCode, GameStatus.WAITING);
    }

    public void endGame(String roomCode) {
        gameStatusMap.put(roomCode, GameStatus.WAITING);
    }

    public void recordVote(Long gameId, Long voterId, Long votedForId) {
        Game game = gameRepository.findById(gameId).orElseThrow(IllegalArgumentException::new);
        Player voter = game.getPlayers().stream().filter(p -> p.getId().equals(voterId)).findFirst().orElse(null);
        Player votedFor = game.getPlayers().stream().filter(p -> p.getId().equals(votedForId)).findFirst().orElse(null);

        if (voter != null && votedFor != null) {
            game.getVotes().add(new Vote(voter, votedFor));
        }
    }

    public List<VoteDTO> getVotes(Long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(IllegalArgumentException::new);
        return game.getVotes().stream().map(this::convertToDTO).toList();
    }


    private GameDTO convertToDTO(Game game) {
        List<PlayerDTO> players = game.getPlayers().stream().map(this::convertToDTO).collect(Collectors.toList());
        List<VoteDTO> votes = game.getVotes().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new GameDTO(game.getId(), game.getName(), players, votes);
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
