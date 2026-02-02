package com.aidart.backend.game.dto;

import com.aidart.backend.visit.VisitDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder(toBuilder = true)

public record GamePlayerDto(
        Long id,
        Long gameId,
        Long playerId,
        String  playerName,
        Integer startPosition,
        Integer finalPosition,
        Integer scoreLeft,
        List<VisitDto> visits
){
}
