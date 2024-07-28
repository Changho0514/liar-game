package com.backend.liargame.game.dto;

public record VoteDTO(PlayerDTO voter, PlayerDTO votedFor) {
}
