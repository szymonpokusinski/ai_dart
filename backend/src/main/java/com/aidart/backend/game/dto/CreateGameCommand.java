package com.aidart.backend.game.dto;

import com.aidart.backend.game.enums.GameFinishRule;
import com.aidart.backend.game.enums.GameType;

import java.util.List;

public record CreateGameCommand (
        List<PlayerOrder> players,
        GameType type,
        GameFinishRule finishRule
){
}
