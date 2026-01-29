package com.aidart.backend.game.dto;

public record GameInitializedEvent(
        String gameUuid,
        String gameTopic,
        String status
) {}