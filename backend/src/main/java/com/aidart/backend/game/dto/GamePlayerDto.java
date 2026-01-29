package com.aidart.backend.game.dto;

import java.util.List;


public record GamePlayerDto(
        Long id,
        Long gameId,
        Long playerId,
        Integer startPosition,
        Integer finalPosition,
        Integer scoreLeft,
        List<VisitDto> visits
){}
