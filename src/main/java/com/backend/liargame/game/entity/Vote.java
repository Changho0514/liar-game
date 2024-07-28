package com.backend.liargame.game.entity;

import com.backend.liargame.member.entity.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    private Player voter;
    private Player votedFor;
}
