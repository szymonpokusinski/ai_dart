package com.aidart.backend.shot;

import com.aidart.backend.shot.enums.ScoreMultiplier;

public record ThrowRequest(
        Integer score,
        ScoreMultiplier multiplier
){}
