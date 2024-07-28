package com.backend.liargame.game.contoller;

import com.backend.liargame.game.dto.GameDTO;
import com.backend.liargame.game.dto.PlayerDTO;
import com.backend.liargame.game.dto.VoteDTO;
import com.backend.liargame.game.entity.Vote;
import com.backend.liargame.game.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{gameId}")
    public GameDTO getGameById(@PathVariable Long gameId) {
        return gameService.getGameById(gameId);
    }

    @GetMapping("/{gameId}/players")
    public List<PlayerDTO> getPlayersInGame(@PathVariable Long gameId) {
        return gameService.getPlayersInGame(gameId);
    }

    @PostMapping("/{gameId}/votes")
    public void recordVote(@PathVariable Long gameId, @RequestBody Vote vote) {
        gameService.recordVote(gameId, vote.getVoter().getId(), vote.getVoter().getId());
    }

    @GetMapping("/{gameId}/votes")
    public List<VoteDTO> getVotes(@PathVariable Long gameId) {
        return gameService.getVotes(gameId);
    }
}
