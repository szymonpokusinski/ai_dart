package com.aidart.backend.game.dto;

import com.aidart.backend.game.enums.GameFinishRule;
import com.aidart.backend.game.enums.GameType;

import java.util.List;

public record CreateGameCommand (
        List<Long> playersIds,
        GameType type,
        GameFinishRule finishRule
){
}
