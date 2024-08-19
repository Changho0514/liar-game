package com.backend.liargame.game.contoller;

import com.backend.liargame.game.dto.*;
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

    public GameController(GameService gameService) {
        this.gameService = gameService;
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
    public ResponseEntity<String> submitVote(@PathVariable String roomCode, @RequestBody VoteRequest vote) {
        String voter = vote.name();
        String votee = vote.vote();
        gameService.submitVote(roomCode, voter, votee);
        return ResponseEntity.ok("Vote submitted");
    }

    @PostMapping("/end/{roomCode}")
    public ResponseEntity<String> endGame(@PathVariable String roomCode) {
        gameService.endGame(roomCode);
        return ResponseEntity.ok("Game ended");
    }

    @PostMapping("/liarOptions/{roomCode}")
    public ResponseEntity<LiarOptionResponseDto> liarOptions(@PathVariable String roomCode) {
        LiarOptionResponseDto liarOptionResponseDto = gameService.liarOptions(roomCode);
        return ResponseEntity.ok(liarOptionResponseDto);
    }


    @GetMapping("/players/{roomCode}")
    public ResponseEntity<List<String>> getPlayers(@PathVariable String roomCode) {
        List<String> players = gameService.getPlayers(roomCode);

        if (players != null) {
            return ResponseEntity.ok(players);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @MessageMapping("/room/{roomCode}/vote-for-liar-vote")
    public void handleVote(@DestinationVariable String roomCode, @Payload VoteRequest voteRequest) {
        gameService.voteForLiarVote(roomCode, voteRequest);
    }

    @MessageMapping("/room/{roomCode}/declaration")
    public void handleDeclaration(@DestinationVariable String roomCode, @Payload DeclarationRequest declarationRequest) {
        gameService.handleDeclaration(roomCode, declarationRequest);
    }

    @PostMapping("/liarGuess/{roomCode}")
    public ResponseEntity<Void> liarGuess(@PathVariable String roomCode, @RequestBody LiarGuessRequestDto liarGuessRequestdto) {
        gameService.liarGuess(roomCode, liarGuessRequestdto);
        return ResponseEntity.ok().build();
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