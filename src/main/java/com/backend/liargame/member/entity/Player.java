package com.backend.liargame.member.entity;

import com.backend.liargame.game.entity.Game;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    @Id
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;
}
