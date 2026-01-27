package com.aidart.backend.game.dto;

public record GamePlayerDto(
        Long id,
        Long gameId,
        Long playerId,
        Integer startPosition,
        Integer finalPosition,
        Integer scoreLeft
){
}
