package com.backend.liargame.game.dto;

import java.util.concurrent.CopyOnWriteArrayList;

public record GameCreateWebSocketResponseDTO(String ownSentence, CopyOnWriteArrayList<String> players) {
}
