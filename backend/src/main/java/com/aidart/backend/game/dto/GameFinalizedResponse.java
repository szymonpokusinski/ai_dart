package com.aidart.backend.game.dto;

public record GameFinalizedResponse(
        String uuid,
        Long dbId,
        String status
) {
}
