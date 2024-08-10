package com.backend.liargame.game.dto;

import java.util.List;
import java.util.Map;

public record VoteResultResponseDto(Map<String, Integer> voteResult, List<String> mostVotedPlayers, String liar, boolean liarWon) {
}
