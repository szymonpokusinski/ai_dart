package com.aidart.backend.game.dto;

import com.aidart.backend.game.enums.GameFinishRule;
import com.aidart.backend.game.enums.GameType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GameRequest(
        @NotEmpty(message = "Player list cannot be empty")
        @Size(min = 1, max = 6, message = "Game must have between 1 and 6 players")
        List<Long> playersIds,

        @NotNull(message = "Game type is required (e.g., TYPE_301, TYPE_501)")
        GameType type,

        @NotNull(message = "Finish rule is required (e.g., DOUBLE_OUT)")
        GameFinishRule finishRule
){}

