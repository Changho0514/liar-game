package com.backend.liargame.game.contoller;

import com.backend.liargame.common.service.WebSocketService;
import com.backend.liargame.game.dto.DeclarationRequest;
import com.backend.liargame.game.dto.GameStartResponseDTO;
import com.backend.liargame.game.dto.VoteRequest;
import com.backend.liargame.game.entity.GameStatus;
import com.backend.liargame.game.service.GameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;
    private final WebSocketService webSocketService;

    public GameController(GameService gameService, WebSocketService webSocketService) {
        this.gameService = gameService;
        this.webSocketService = webSocketService;
    }

    @PostMapping("/start/{roomCode}")
    public ResponseEntity<GameStartResponseDTO> startGame(@PathVariable String roomCode) {
        GameStatus status = gameService.getGameStatus(roomCode);
        if (status == GameStatus.IN_PROGRESS) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // 게임 진행 중이면 에러 반환
        }
        GameStartResponseDTO game = gameService.startGame(roomCode);

        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @GetMapping("/status/{roomCode}")
    public ResponseEntity<Map<String, String>> getGameStatus(@PathVariable String roomCode) {
        GameStatus status = gameService.getGameStatus(roomCode);
        Map<String, String> response = new HashMap<>();
        response.put("status", status.name());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/vote/{roomCode}")
    public ResponseEntity<String> submitVote(@PathVariable String roomCode, @RequestBody Map<String, String> voteData) {
        String voter = voteData.get("voter");
        String votee = voteData.get("votee");
        gameService.submitVote(roomCode, voter, votee);
        return ResponseEntity.ok("Vote submitted");
    }

    @PostMapping("/end/{roomCode}")
    public ResponseEntity<String> endGame(@PathVariable String roomCode) {
        gameService.endGame(roomCode);
        return ResponseEntity.ok("Game ended");
    }

    @GetMapping("/players/{roomCode}")
    public ResponseEntity<List<String>> getPlayers(@PathVariable String roomCode) {
        List<String> players = webSocketService.getPlayers(roomCode);
        if (players != null) {
            return ResponseEntity.ok(players);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @MessageMapping("/room/{roomCode}/vote")
    public void handleVote(@DestinationVariable String roomCode, @Payload VoteRequest voteRequest) {

        gameService.handleVote(roomCode, voteRequest);
    }

    @MessageMapping("/room/{roomCode}/declaration")
    public void handleDeclaration(@DestinationVariable String roomCode, @Payload DeclarationRequest declarationRequest) {
        gameService.handleDeclaration(roomCode, declarationRequest);
    }


    @GetMapping("/session/nickname")
    public ResponseEntity<String> getNickname(HttpSession session) {
        String nickname = (String) session.getAttribute("nickname");
        if (nickname != null) {
            return ResponseEntity.ok(nickname);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}