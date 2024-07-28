package com.backend.liargame.game.dto;



import java.util.List;

public record GameDTO(Long id, String name, List<PlayerDTO> players, List<VoteDTO> votes) {
}

