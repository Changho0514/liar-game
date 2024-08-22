package com.backend.liargame.game.service;

import com.backend.liargame.game.contoller.LiarGuessResponseDto;
import com.backend.liargame.game.dto.*;
import com.backend.liargame.game.entity.GameStatus;
import com.backend.liargame.game.entity.Keyword;
import com.backend.liargame.game.entity.Topic;
import com.backend.liargame.game.repository.GameRepository;
import com.backend.liargame.game.repository.KeywordRepository;
import com.backend.liargame.game.repository.TopicRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GameService {

    private final TopicRepository topicRepository;
    private final KeywordRepository keywordRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, CopyOnWriteArrayList<String>> roomPlayers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<String>> lateJoiners = new ConcurrentHashMap<>();
    private final Map<String, GameStatus> gameStatusMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> votes = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Boolean>> liarVoteConsent = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> declarations = new ConcurrentHashMap<>();
    private final Map<String, Integer> currentTurnMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> timeLeftMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> turnCounterMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> liarPlayer = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<String>> topicAndKeyword = new ConcurrentHashMap<>();
    private final ThreadPoolTaskScheduler scheduler;
    private final Map<String, ScheduledFuture<?>> voteStartTasks = new ConcurrentHashMap<>();

    private final int TURN_TIME = 20;  // 각 플레이어 별 시간

    public GameService(GameRepository gameRepository, TopicRepository topicRepository, KeywordRepository keywordRepository, SimpMessagingTemplate messagingTemplate, ThreadPoolTaskScheduler scheduler) {
        this.topicRepository = topicRepository;
        this.keywordRepository = keywordRepository;
        this.messagingTemplate = messagingTemplate;
        this.scheduler = scheduler;
    }

    // 플레이어 추가
    public void addPlayer(String roomCode, String nickname) {
        roomPlayers.putIfAbsent(roomCode, new CopyOnWriteArrayList<>());
        lateJoiners.putIfAbsent(roomCode, new CopyOnWriteArrayList<>());

        // 게임 도중 입장했다면 lateJoiner 에 우선 추가
        if(getGameStatus(roomCode) == GameStatus.IN_PROGRESS){
            lateJoiners.get(roomCode).add(nickname);
        } else {
            roomPlayers.get(roomCode).add(nickname);
        }
        List<String> playerList = new ArrayList<>();
        playerList.addAll(roomPlayers.get(roomCode));
        playerList.addAll(lateJoiners.get(roomCode));

        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/players", playerList);
    }

    // 플레이어 제거
    public void removePlayer(String roomCode, String nickname) {
        CopyOnWriteArrayList<String> players = roomPlayers.getOrDefault(roomCode, new CopyOnWriteArrayList<>());
        CopyOnWriteArrayList<String> waiters = lateJoiners.getOrDefault(roomCode, new CopyOnWriteArrayList<>());
        if (players.contains(nickname)) {
            players.remove(nickname);
        } else if(waiters.contains(nickname)){
            waiters.remove(nickname);
        }
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/players", players);
    }

    public int getPlayerCount(String roomCode) {
        CopyOnWriteArrayList<String> players = roomPlayers.get(roomCode);
        return (players != null) ? players.size() : 0;
    }

    public CopyOnWriteArrayList<String> getPlayers(String roomCode) {
        return roomPlayers.get(roomCode);
    }

    public CopyOnWriteArrayList<String> getWaiters(String roomCode) {
        return lateJoiners.get(roomCode);
    }

    public boolean isNicknameTaken(String roomCode, String nickname) {
        CopyOnWriteArrayList<String> players = roomPlayers.getOrDefault(roomCode, new CopyOnWriteArrayList<>());
        CopyOnWriteArrayList<String> waiters = lateJoiners.getOrDefault(roomCode, new CopyOnWriteArrayList<>());
        return players.contains(nickname) || waiters.contains(nickname);
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

    // 게임 시작
    public GameStartResponseDTO startGame(String roomCode) {

        if (gameStatusMap.getOrDefault(roomCode, GameStatus.WAITING) == GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("이미 게임이 진행중입니다.");
        }

        CopyOnWriteArrayList<String> players = this.getPlayers(roomCode);
        CopyOnWriteArrayList<String> waiters = lateJoiners.getOrDefault(roomCode, new CopyOnWriteArrayList<>());

        // 기다리던 사람 모두 추가
        players.addAll(waiters);
        lateJoiners.put(roomCode, new CopyOnWriteArrayList<>());

        int liarIndex = new Random().nextInt(players.size());

        // 랜덤한 Topic 가져오기
        Topic topic = topicRepository.findRandomTopic()
                .orElseThrow(() -> new RuntimeException("No Topic found"));
        log.info("[GameService] - Topic ID: {}", topic.getId());

        // 해당 토픽에 속하는 랜덤한 keyword 가져오기
        String keyword = keywordRepository.findRandomKeywordByTopicId(topic.getId())
                .map(Keyword::getName)
                .orElse("실패한 지시어 입니다.");

        this.startGame(roomCode, liarIndex, topic.getName(), keyword);
        gameStatusMap.put(roomCode, GameStatus.IN_PROGRESS); // 시작 상태로 변경

        // 랜덤하게 첫 번째 턴 플레이어 선택
        int initialTurn = new Random().nextInt(players.size());
        currentTurnMap.put(roomCode, initialTurn);
        timeLeftMap.put(roomCode, TURN_TIME); // 초기 남은 시간 설정

        // 첫 번째 턴 플레이어 알림
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/turnUpdate", players.get(initialTurn));

        GameCreateDTO game = new GameCreateDTO(liarIndex, topic.getName(), keyword);
        return new GameStartResponseDTO(game, players);
    }

    // 라이어 및 일반 플레이어에게 메시지 전송
    public void startGame(String roomCode, int liarIndex, String topic, String keyword) {
        CopyOnWriteArrayList<String> players = this.getPlayers(roomCode);
        String liar = players.get(liarIndex);
        liarPlayer.put(roomCode, liarIndex);
        topicAndKeyword.put(roomCode, new CopyOnWriteArrayList<>());
        topicAndKeyword.get(roomCode).add(topic);
        topicAndKeyword.get(roomCode).add(keyword);

        GameCreateWebSocketResponseDTO liarDto = new GameCreateWebSocketResponseDTO("당신은 라이어입니다. <br>주제는 \"" + topic + "\"입니다.", players);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/gameStart/" + liar, liarDto);

        GameCreateWebSocketResponseDTO normalDto = new GameCreateWebSocketResponseDTO("주제는 \"" + topic + "\"이며 <br>제시어는 \"" + keyword + "\"입니다. <br>라이어를 찾아주세요.", players);
        for (int i = 0; i < players.size(); i++) {
            if (i != liarIndex) {
                String player = players.get(i);
                messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/gameStart/" + player, normalDto);
            }
        }
    }

    public GameStatus getGameStatus(String roomCode) {
        return gameStatusMap.getOrDefault(roomCode, GameStatus.WAITING);
    }

    public void submitVote(String roomCode, String voter, String votee) {
        votes.computeIfAbsent(roomCode, k -> new ConcurrentHashMap<>()).put(voter, votee);
        log.info("[GameService - submitVote] - 총 투표 사이즈 : " + votes.get(roomCode).size());

        if (votes.get(roomCode).size() == this.getPlayerCount(roomCode)) {

            CopyOnWriteArrayList<String> players = this.getPlayers(roomCode);

            // 각 플레이어가 받은 표를 집계하는 로직
            Map<String, AtomicInteger> voteCounts = new ConcurrentHashMap<>();
            votes.get(roomCode).values().forEach(voted ->
                    voteCounts.computeIfAbsent(voted, k -> new AtomicInteger(0)).incrementAndGet()
            );
            Map<String, Integer> voteResult = voteCounts.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

            List<String> mostVotedPlayers = findMostVotedPlayers(voteResult);
            Integer liarIndex = this.getLiarIndex(roomCode);
            String liar = players.get(liarIndex);
            boolean liarWon = mostVotedPlayers.size() != 1 || !mostVotedPlayers.get(0).equals(liar);

            VoteResultResponseDto voteResultResponseDto = new VoteResultResponseDto(voteResult, mostVotedPlayers, liar, liarWon);
            messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/voteResult", voteResultResponseDto);
        }
    }

    public List<String> findMostVotedPlayers(Map<String, Integer> voteCounts) {
        return voteCounts.entrySet().stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue)) // 표 수(value)로 그룹핑
                .entrySet().stream()
                .max(Map.Entry.comparingByKey()) // 최대 표 수를 가진 엔트리를 찾음
                .map(Map.Entry::getValue) // 해당 표 수를 가진 플레이어들 리스트
                .orElse(new ArrayList<>()) // 동률이 없을 경우 빈 리스트 반환
                .stream()
                .map(Map.Entry::getKey) // 플레이어 이름만 추출
                .collect(Collectors.toList());
    }

    public void handleVote(String roomCode, VoteRequest voteRequest) {
        String voter = voteRequest.name();
        String votee = voteRequest.vote();

        // 투표 결과 업데이트
        votes.computeIfAbsent(roomCode, k -> new ConcurrentHashMap<>()).put(voter, votee);

        // 실시간 투표 결과 전송
        Map<String, String> voteResults = votes.get(roomCode);
        log.info("Sending vote results: " + voteResults);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/voteUpdate", voteResults);
    }

    public void voteForLiarVote(String roomCode, VoteRequest voteRequest) {
        String nickname = voteRequest.name();
        Boolean yesOrNo = voteRequest.vote().equals("yes");

        if (yesOrNo) {
            liarVoteConsent.computeIfAbsent(roomCode, k -> new ConcurrentHashMap<>()).put(nickname, yesOrNo);
        }

        Map<String, Boolean> voteResults = liarVoteConsent.get(roomCode);
        log.info("라이어 투표 할래요? 몇 명 동의? ->  " + voteResults.size());

        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/voteUpdate", voteResults.size());

        if (this.getPlayerCount(roomCode) / 2 < voteResults.size()) {
            timeLeftMap.put(roomCode, 60);
            currentTurnMap.put(roomCode, -1);
            messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/startVote", "");
        }
    }

    public void handleDeclaration(String roomCode, DeclarationRequest declarationRequest) {
        String nickname = declarationRequest.name();
        String declaration = declarationRequest.declaration();
        CopyOnWriteArrayList<String> players = this.getPlayers(roomCode);
        int currentTurn = currentTurnMap.getOrDefault(roomCode, 0);

        if (!players.get(currentTurn).equals(nickname)) {
            throw new IllegalStateException("현재 턴이 아닙니다.");
        }
        declarations.computeIfAbsent(roomCode, k -> new ConcurrentHashMap<>()).put(nickname, declaration);

        // 실시간 선언 전송
        log.info("[GameService - submitDeclaration] - " + nickname + " : " + declaration);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/declarationUpdate", declarations.get(roomCode));

        // 턴 카운터 업데이트
        int turnCounter = turnCounterMap.getOrDefault(roomCode, 0) + 1;
        if (turnCounter >= players.size()) {
            log.info("[GameService - handleDeclaration] - turnCounter: {}, players.size(): {}", turnCounter, players.size());
            startVote(roomCode); // 게임 종료 메서드 호출 (또는 다음 단계로 이동)
            return;
        } else {
            turnCounterMap.put(roomCode, turnCounter);
        }


        // 턴을 다음 플레이어로 넘김
        nextPlayerTurn(roomCode);
    }

    /*
       타이머 관련 로직
     */
    @Scheduled(fixedRate = 1000) // 1초마다 실행
    public void broadcastTimeLeft() {
        for (Map.Entry<String, Integer> entry : timeLeftMap.entrySet()) {
            String roomCode = entry.getKey();
            int timeLeft = entry.getValue();
            CopyOnWriteArrayList<String> players = this.getPlayers(roomCode);

            if (players.isEmpty()) {
                log.warn("No players left in room: " + roomCode);
                timeLeftMap.remove(roomCode);  // 더 이상 시간을 카운트하지 않도록 함
                currentTurnMap.remove(roomCode);  // currentTurn 정보도 삭제
                continue;
            }
            int currentTurn = currentTurnMap.getOrDefault(roomCode, 0);
            if (timeLeft > 0) {

                // 라이어 투표를 위한 투표중 이라면
                if(currentTurn == -1){
                    String currentPlayer = "투표중입니다.";
                    TimerMessage timerMessage = new TimerMessage(timeLeft-1, currentPlayer);
                    messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/timer", timerMessage);
                    timeLeftMap.put(roomCode, timeLeft - 1);
                } else if (!players.isEmpty() && currentTurn < players.size()) {
                    String currentPlayer = players.get(currentTurn);
                    TimerMessage timerMessage = new TimerMessage(timeLeft-1, currentPlayer);
                    messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/timer", timerMessage);
                    timeLeftMap.put(roomCode, timeLeft - 1);
                } else {
                    nextPlayerTurn(roomCode);
                }
            } else {
                if (currentTurn != -1) {
                    nextPlayerTurn(roomCode);
                } else {
                    TimerMessage timerMessage = new TimerMessage(0, "게임 종료!");
                    messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/timer", timerMessage);
                }
            }
        }
    }

    private void nextPlayerTurn(String roomCode) {
        CopyOnWriteArrayList<String> players = this.getPlayers(roomCode);
        if (players.isEmpty()) {
            log.warn("No players left in room: " + roomCode);
            return;
        }
        int currentTurn = currentTurnMap.getOrDefault(roomCode, 0);

        boolean alreadyHaveDeclared = false;
        while(!alreadyHaveDeclared){
            currentTurn = (currentTurn + 1) % players.size();
            String currentPlayer = players.get(currentTurn);

            Map<String, String> roomDeclarations = declarations.getOrDefault(roomCode, new ConcurrentHashMap<>());
            log.info("currentPlayer : {}", currentPlayer);
            log.info("declarations.get(roomCode).containsKey(currentPlayer) : {}", roomDeclarations.containsKey(currentPlayer));
            // 현재 플레이어가 발언하지 않았다면 루프 종료
            if(!roomDeclarations.containsKey(currentPlayer)) {
                alreadyHaveDeclared = true;
            }

            // 모든 플레이어가 이미 발언한 경우, 루프 종료
            if (roomDeclarations.size() >= players.size()) {
                break;
            }
        }
        currentTurnMap.put(roomCode, currentTurn);
        timeLeftMap.put(roomCode, TURN_TIME);

        String nextPlayer = players.get(currentTurn);
        log.info("while루프 이후 : {}", nextPlayer);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/turnUpdate", nextPlayer);

        TimerMessage timerMessage = new TimerMessage(TURN_TIME, nextPlayer);
        log.info("[nextPlayerTurn] - Broadcasting TimerMessage: " + timerMessage);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/timer", timerMessage);
    }

    public void startVote(String roomCode) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/votePrompt", "투표를 시행하겠습니까? 60초 뒤에는 자동으로 투표에 진입합니다.");
        timeLeftMap.put(roomCode, 60);
        currentTurnMap.put(roomCode, -1);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/timer", new TimerMessage(60, "vote"));
    }


    public LiarOptionResponseDto liarOptions(String roomCode) {
        CopyOnWriteArrayList<String> players = this.getPlayers(roomCode);
        log.info("[GameService - liarOptions] - players.size() : {}", players.size());
        Integer liarIndex = this.getLiarIndex(roomCode);
        log.info("[GameService - liarOptions] - liarIndex : {}", liarIndex);
        String liar = players.get(liarIndex);
        log.info("[GameService - liarOptions] - liar : {}", liar);
        List<String> topicAndKeyword = this.getKeyword(roomCode);
        String topic = topicAndKeyword.get(0);
        String keyword = topicAndKeyword.get(1);
        log.info("[GameService - liarOptions] - topic : {}", topic);
        log.info("[GameService - liarOptions] - keyword : {}", keyword);
        Long topicId = topicRepository.findIdByName(topic);
        log.info("[GameService - liarOptions] - topicId : {}", topicId);
        List<String> optionalKeywords = keywordRepository.findOptionKeywordByTopicIdExcludingKeyword(topicId, keyword);
        log.info("[GameService - liarOptions] - optionalKeywords.getFirst() : {}", optionalKeywords.get(0));
        optionalKeywords.add(keyword);

        Collections.shuffle(optionalKeywords);
        return new LiarOptionResponseDto(liar, optionalKeywords, keyword);
    }

    public void liarGuess(String roomCode, LiarGuessRequestDto liarGuessRequestdto) {
        boolean correct = liarGuessRequestdto.selectedOption().equals(liarGuessRequestdto.correctAnswer());
        LiarGuessResponseDto result = new LiarGuessResponseDto(correct, liarGuessRequestdto.selectedOption(), liarGuessRequestdto.correctAnswer());
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/liar-result", result);
    }

    public void endGame(String roomCode) {
        gameStatusMap.remove(roomCode);
        votes.remove(roomCode);
        liarVoteConsent.remove(roomCode);
        declarations.remove(roomCode);
        currentTurnMap.remove(roomCode);
        timeLeftMap.remove(roomCode);
        turnCounterMap.remove(roomCode);

        ScheduledFuture<?> scheduledTask = voteStartTasks.remove(roomCode);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }

        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/gameEnd", "게임이 종료되었습니다.");
    }

    public String getLiarNickName(String roomCode){
        Integer liarIndex = getLiarIndex(roomCode);
        CopyOnWriteArrayList<String> players = roomPlayers.getOrDefault(roomCode, new CopyOnWriteArrayList<>());
        return players.get(liarIndex);
    }


}
