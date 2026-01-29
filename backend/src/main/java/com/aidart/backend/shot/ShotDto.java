package com.aidart.backend.shot;

import com.aidart.backend.shot.enums.ScoreMultiplier;

public record ShotDto(
        Long id,
        Long gamePlayerId,
        Integer baseScore,
        ScoreMultiplier multiplier,
        Integer totalScore
){}
