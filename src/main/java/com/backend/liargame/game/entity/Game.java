package com.backend.liargame.game.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



}
