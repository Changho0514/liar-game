package com.backend.liargame.game.dto;

import java.util.List;

public record LiarOptionResponseDto(String liar, List<String> options, String correctAnswer) {
}
