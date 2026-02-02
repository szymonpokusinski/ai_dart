package com.aidart.backend.visit;

import com.aidart.backend.shot.ShotDto;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record VisitDto(
        Long gamePlayerId,
        List<ShotDto> shots,
        Integer totalScore,
        boolean isBust
){}
