package com.aidart.backend.game.dto;

import com.aidart.backend.shot.ShotDto;

import java.util.List;

public record VisitDto(
        Long gamePlayerId,
        List<ShotDto> shots,
        Integer totalScore,
        boolean isBust
){}
