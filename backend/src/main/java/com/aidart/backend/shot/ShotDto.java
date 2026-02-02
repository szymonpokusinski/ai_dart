package com.aidart.backend.shot;

import com.aidart.backend.shot.enums.ScoreMultiplier;
import lombok.Builder;

@Builder(toBuilder = true)
public record ShotDto(
        Long id,
        Long visitId,
        Integer baseScore,
        ScoreMultiplier multiplier,
        Integer totalScore
) {
    public ShotDto {
        if (totalScore == null && baseScore != null && multiplier != null) {
            int multiVal = switch (multiplier) {
                case DOUBLE -> 2;
                case TRIPLE -> 3;
                default -> 1;
            };
            totalScore = baseScore * multiVal;
        }
    }
}
