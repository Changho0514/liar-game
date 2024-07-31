package com.backend.liargame.game.contoller;

import com.backend.liargame.game.service.GameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createGame(@RequestBody Map<String, String> payload, HttpSession session) {
        String nickname = payload.get("nickname");
        if (nickname == null || nickname.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid nickname");
        }
        String roomCode = gameService.createGame(nickname);
        if (roomCode != null && !roomCode.isEmpty()) {
            session.setAttribute("nickname", nickname);
            return ResponseEntity.ok(roomCode);
        } else {
            return ResponseEntity.status(500).body("Failed to create game");
        }
    }

    @PostMapping("/join/{roomCode}")
    public ResponseEntity<String> joinGame(@PathVariable String roomCode, @RequestBody Map<String, String> payload, HttpSession session) {
        String nickname = payload.get("nickname");
        if (nickname == null || nickname.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid nickname");
        }
        session.setAttribute("nickname", nickname);
        return ResponseEntity.ok("Joined room " + roomCode);
    }

    @PostMapping("/start/{roomCode}")
    public ResponseEntity<Map<String, Boolean>> startGame(@PathVariable String roomCode, @RequestBody Map<String, String> payload) {
        String nickname = payload.get("nickname");
//        boolean success = gameService.startGame(roomCode, nickname);
//        return ResponseEntity.ok(Map.of("success", success));
        return (ResponseEntity<Map<String, Boolean>>) ResponseEntity.ok();
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