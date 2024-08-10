package com.backend.liargame.game.service;

import com.backend.liargame.common.service.WebSocketService;
import com.backend.liargame.game.contoller.LiarGuessResponseDto;
import com.backend.liargame.game.dto.*;
import com.backend.liargame.game.entity.*;
import com.backend.liargame.game.repository.GameRepository;
import com.backend.liargame.game.repository.KeywordRepository;
import com.backend.liargame.game.repository.TopicRepository;
import com.backend.liargame.member.entity.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final TopicRepository topicRepository;
    private final KeywordRepository keywordRepository;

    private final WebSocketService webSocketService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, GameStatus> gameStatusMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> votes = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Boolean>> liarVoteConsent = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> declarations = new ConcurrentHashMap<>();
    private final Map<String, Integer> currentTurnMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> timeLeftMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> turnCounterMap = new ConcurrentHashMap<>();
    private final ThreadPoolTaskScheduler scheduler;
    private final Map<String, ScheduledFuture<?>> voteStartTasks = new ConcurrentHashMap<>();

    public GameService(GameRepository gameRepository, TopicRepository topicRepository, KeywordRepository keywordRepository, WebSocketService webSocketService, SimpMessagingTemplate messagingTemplate, ThreadPoolTaskScheduler scheduler) {
        this.gameRepository = gameRepository;
        this.topicRepository = topicRepository;
        this.keywordRepository = keywordRepository;
        this.webSocketService = webSocketService;
        this.messagingTemplate = messagingTemplate;
        this.scheduler = scheduler;
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

        // 랜덤하게 첫 번째 턴 플레이어 선택
        int initialTurn = new Random().nextInt(players.size());
        currentTurnMap.put(roomCode, initialTurn);

        timeLeftMap.put(roomCode, 15); // 초기 남은 시간 설정

        // 첫 번째 턴 플레이어 알림
        webSocketService.convertAndSend("/topic/room/" + roomCode + "/turnUpdate", players.get(initialTurn));

        GameCreateDTO game = new GameCreateDTO(liarIndex, topic.getName(), keyword);
        return new GameStartResponseDTO(game, players);
    }

    public GameStatus getGameStatus(String roomCode) {
        return gameStatusMap.getOrDefault(roomCode, GameStatus.WAITING);
    }

    public void submitVote(String roomCode, String voter, String votee) {
        votes.computeIfAbsent(roomCode, k -> new ConcurrentHashMap<>()).put(voter, votee);
        log.info("[GameService - submitVote] - 총 투표 사이즈 : " + votes.get(roomCode).size());

        if(votes.get(roomCode).size() == webSocketService.getPlayerCount(roomCode)){

            CopyOnWriteArrayList<String> players = webSocketService.getPlayers(roomCode);

            // 각 플레이어가 받은 표를 집계하는 로직
            Map<String, AtomicInteger> voteCounts = new ConcurrentHashMap<>();
            votes.get(roomCode).values().forEach(voted ->
                    voteCounts.computeIfAbsent(voted, k -> new AtomicInteger(0)).incrementAndGet()
            );
            Map<String, Integer> voteResult = voteCounts.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

            List<String> mostVotedPlayers = findMostVotedPlayers(voteResult);
            Integer liarIndex = webSocketService.getLiarIndex(roomCode);

            String liar = players.get(liarIndex);
            boolean liarWon = mostVotedPlayers.size() != 1 || !mostVotedPlayers.getFirst().equals(liar);

            VoteResultResponseDto voteResultResponseDto = new VoteResultResponseDto(voteResult, mostVotedPlayers, liar, liarWon);
            messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/voteResult", voteResultResponseDto);
        }


    }

    // 가장 많이 투표받은 플레이어들을 찾는 로직
    public List<String> findMostVotedPlayers(Map<String, Integer> voteCounts) {
        List<String> mostVoted = voteCounts.entrySet().stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue)) // 표 수(value)로 그룹핑
                .entrySet().stream()
                .max(Map.Entry.comparingByKey()) // 최대 표 수를 가진 엔트리를 찾음
                .map(Map.Entry::getValue) // 해당 표 수를 가진 플레이어들 리스트
                .orElse(new ArrayList<>()) // 동률이 없을 경우 빈 리스트 반환
                .stream()
                .map(Map.Entry::getKey) // 플레이어 이름만 추출
                .collect(Collectors.toList());

        return mostVoted;
    }



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

    public void voteForLiarVote(String roomCode, VoteRequest voteRequest) {

        String nickname = voteRequest.name();
        Boolean yesOrNo = voteRequest.vote().equals("yes");

        if(yesOrNo){
            liarVoteConsent.computeIfAbsent(roomCode, k -> new ConcurrentHashMap<>()).put(nickname, yesOrNo);
        }

        Map<String, Boolean> voteResults = liarVoteConsent.get(roomCode);
        log.info("라이어 투표 할래요? 몇 명 동의? ->  " + voteResults.size());

        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/voteUpdate", voteResults.size());

        if(webSocketService.getPlayerCount(roomCode) / 2 < voteResults.size()){
            messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/startVote", "");
        }
    }

    public void handleDeclaration(String roomCode, DeclarationRequest declarationRequest) {

        String nickname = declarationRequest.name();
        String declaration = declarationRequest.declaration();

        CopyOnWriteArrayList<String> players = webSocketService.getPlayers(roomCode);

        int currentTurn = currentTurnMap.getOrDefault(roomCode, 0);

        if (!players.get(currentTurn).equals(nickname)) {
            throw new IllegalStateException("현재 턴이 아닙니다.");
        }
        declarations.computeIfAbsent(roomCode, k -> new ConcurrentHashMap<>()).put(nickname, declaration);

        // 실시간 선언 전송
        log.info("[GameService - submitDeclaration] - " + nickname + " : " + declaration);
        webSocketService.convertAndSend("/topic/room/" + roomCode + "/declarationUpdate", declarations.get(roomCode));

        // 턴 카운터 업데이트
        int turnCounter = turnCounterMap.getOrDefault(roomCode, 0) + 1;
        if (turnCounter >= players.size()) {
            // 모든 플레이어가 한 번씩 턴을 마쳤다면 게임 종료 또는 다음 단계로
            log.info("[GameService - handleDeclaration] - turnCounter: {}, players.size(): {}", turnCounter, players.size());

            endGame(roomCode); // 게임 종료 메서드 호출 (또는 다음 단계로 이동)
            return;
        } else {
            turnCounterMap.put(roomCode, turnCounter);
        }

        // 턴을 다음 플레이어로 넘김
        currentTurn = (currentTurn + 1) % players.size();
        currentTurnMap.put(roomCode, currentTurn);
        timeLeftMap.put(roomCode, 15); // 남은 시간 초기화

        String nextPlayer = players.get(currentTurn);
        webSocketService.convertAndSend("/topic/room/" + roomCode + "/turnUpdate", nextPlayer);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/timer", new TimerMessage(15, nextPlayer)); // 초기화된 타이머 브로드캐스트
    }


    /*
       타이머 관련 로직
     */
    @Scheduled(fixedRate = 1000) // 1초마다 실행
    public void broadcastTimeLeft() {
        for (Map.Entry<String, Integer> entry : timeLeftMap.entrySet()) {
            String roomCode = entry.getKey();
            int timeLeft = entry.getValue();

//            log.info("[Scheduling] - roomCode : " + roomCode);
            if (timeLeft > 0) {
                CopyOnWriteArrayList<String> players = webSocketService.getPlayers(roomCode);
                int currentTurn = currentTurnMap.getOrDefault(roomCode, 0);
                String currentPlayer = players.get(currentTurn);
                TimerMessage timerMessage = new TimerMessage(timeLeft, currentPlayer);
//                log.info("[Scheduling] - Broadcasting TimerMessage: " + timerMessage); // 추가된 로그
                messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/timer", new TimerMessage(timeLeft, currentPlayer));
                timeLeftMap.put(roomCode, timeLeft - 1);
            } else {
                nextPlayerTurn(roomCode);
            }
        }
    }

    private void nextPlayerTurn(String roomCode) {
        CopyOnWriteArrayList<String> players = webSocketService.getPlayers(roomCode);
        int currentTurn = currentTurnMap.getOrDefault(roomCode, 0);
        currentTurn = (currentTurn + 1) % players.size();
        currentTurnMap.put(roomCode, currentTurn);
        timeLeftMap.put(roomCode, 15);

        String nextPlayer = players.get(currentTurn);
        webSocketService.convertAndSend("/topic/room/" + roomCode + "/turnUpdate", nextPlayer);

        TimerMessage timerMessage = new TimerMessage(15, nextPlayer);
        log.info("[nextPlayerTurn] - Broadcasting TimerMessage: " + timerMessage); // 추가된 로그
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/timer", timerMessage); // 타이머 리셋 브로드캐스트
    }



    public void endGame(String roomCode) {
        // 1. "투표를 시행하겠습니까? 60초 뒤에는 자동으로 투표에 진입합니다" 메시지와 타이머 전송
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/votePrompt", "투표를 시행하겠습니까? 60초 뒤에는 자동으로 투표에 진입합니다.");
        timeLeftMap.put(roomCode, 60);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/timer", new TimerMessage(60, "vote"));

        // 60초 후 자동 투표 진입
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                initiateVoting(roomCode);
//            }
//        }, 60000);
    }

//    private void initiateVoting(String roomCode) {
//        // 투표 로직 시작
//        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/startVoting", "투표를 시작합니다.");
//
//        // 투표 결과를 처리하는 로직
//        // 과반수 찬성이면 5초 후 투표 시작
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                startVote(roomCode);
//            }
//        }, 5000);
//    }

    private void finalizeGame(String roomCode, boolean liarWon) {
        // 게임 종료 처리 로직
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/gameEnd", liarWon ? "라이어가 승리했습니다!" : "라이어가 패배했습니다!");
        // 필요한 리소스 정리 및 게임 데이터 초기화
    }

    public LiarOptionResponseDto liarOptions(String roomCode) {
        CopyOnWriteArrayList<String> players = webSocketService.getPlayers(roomCode);
        Integer liarIndex = webSocketService.getLiarIndex(roomCode);

        String liar = players.get(liarIndex);

        List<String> topicAndKeyword = webSocketService.getKeyword(roomCode);
        String topic = topicAndKeyword.get(0);
        String keyword = topicAndKeyword.get(1);

        Long topicId = topicRepository.findIdByName(topic);
        List<String> optionalKeywords = keywordRepository.findOptionKeywordByTopicIdExcludingKeyword(topicId, keyword);
        optionalKeywords.add(keyword);

        // 리스트를 무작위로 섞음
        Collections.shuffle(optionalKeywords);

        return new LiarOptionResponseDto(liar, optionalKeywords, keyword);
    }

    public void liarGuess(String roomCode, LiarGuessRequestDto liarGuessRequestdto) {
        boolean correct = liarGuessRequestdto.selectedOption().equals(liarGuessRequestdto.correctAnswer());

        LiarGuessResponseDto result = new LiarGuessResponseDto(
                correct,
                liarGuessRequestdto.selectedOption(),
                liarGuessRequestdto.correctAnswer()
        );

        // 결과를 모든 클라이언트에게 브로드캐스트
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/liar-result", result);
    }
}
