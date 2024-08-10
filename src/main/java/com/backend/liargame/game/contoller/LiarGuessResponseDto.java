package com.backend.liargame.game.contoller;

public record LiarGuessResponseDto(boolean correct, String selectedOption, String correctAnswer) {
}
