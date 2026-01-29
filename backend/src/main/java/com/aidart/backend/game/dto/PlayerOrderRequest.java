package com.aidart.backend.game.dto;

import jakarta.validation.constraints.NotNull;

public record PlayerOrderRequest(
        @NotNull Long playerId,
        @NotNull Integer position
){}
