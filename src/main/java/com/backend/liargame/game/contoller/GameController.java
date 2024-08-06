package com.backend.liargame.game.contoller;

import com.backend.liargame.common.service.WebSocketService;
import com.backend.liargame.game.dto.GameCreateDTO;
import com.backend.liargame.game.service.GameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<GameCreateDTO> startGame(@PathVariable String roomCode) {
        GameCreateDTO game = gameService.startGame(roomCode);

        return new ResponseEntity<>(game, HttpStatus.OK);
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