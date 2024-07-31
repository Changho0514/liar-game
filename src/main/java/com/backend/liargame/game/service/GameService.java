package com.backend.liargame.game.service;

import com.backend.liargame.game.dto.GameDTO;
import com.backend.liargame.game.dto.PlayerDTO;
import com.backend.liargame.game.dto.VoteDTO;
import com.backend.liargame.game.entity.Game;
import com.backend.liargame.game.entity.Vote;
import com.backend.liargame.game.repository.GameRepository;
import com.backend.liargame.member.entity.Player;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public String createGame(String name) {
        String uuid = generateUUID();
        Game game = new Game(uuid);
        gameRepository.save(game);
        return uuid;
    }

    public GameDTO getGameById(Long id) {
        Game game = gameRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Game not found"));
        return convertToDTO(game);
    }

    public List<PlayerDTO> getPlayersInGame(Long gameId) {

        Game game = gameRepository.findById(gameId).orElseThrow(IllegalArgumentException::new);
        return game.getPlayers().stream().map(this::convertToDTO).toList();
    }

    public void addPlayerToGame(Long gameId, Player player) {
        Game game = gameRepository.findById(gameId).orElseThrow(IllegalArgumentException::new);
        game.getPlayers().add(player);
        gameRepository.save(game);
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

    private String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
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
