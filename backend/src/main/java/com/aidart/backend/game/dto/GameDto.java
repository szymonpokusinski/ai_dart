package com.aidart.backend.game.dto;

import com.aidart.backend.game.Game;
import com.aidart.backend.game.enums.GameFinishRule;
import com.aidart.backend.game.enums.GameStatus;
import com.aidart.backend.game.enums.GameType;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record GameDto(
        Long id,
        String uuid,
        GameType type,
        GameStatus status,
        LocalDate startTime,
        LocalDate endTime,
        GameFinishRule finishRule,
        List<GamePlayerDto> gamePlayerDto
) {
        public static GameDto fromEntity(Game game) {
                return GameDto.builder()
                        .id(game.getId())
                        .uuid(game.getUuid().toString())
                        .type(game.getType())
                        .status(game.getStatus())
                        .startTime(game.getStartTime())
                        .endTime(game.getEndTime())
                        .finishRule(game.getFinishRule())
                        .build();
        }
}