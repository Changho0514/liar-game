package com.backend.liargame.game.dto;

import java.util.concurrent.CopyOnWriteArrayList;

public record GameStartResponseDTO(GameCreateDTO game, CopyOnWriteArrayList<String> players) {
}
