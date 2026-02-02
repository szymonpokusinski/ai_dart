package com.aidart.backend.game;

import com.aidart.backend.game.dto.GamePlayerDto;
import com.aidart.backend.game.enums.GameStatus;
import com.aidart.backend.shot.Shot;
import com.aidart.backend.shot.ShotDto;
import com.aidart.backend.visit.Visit;
import com.aidart.backend.visit.VisitDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GameMapper {

    public GameState toGameState(Game game, List<GamePlayer> gamePlayers) {
        List<GamePlayerDto> players = gamePlayers.stream()
                .map(gamePlayer -> new GamePlayerDto(
                        gamePlayer.getId(),
                        game.getId(),
                        gamePlayer.getPlayer().getId(),
                        gamePlayer.getPlayer().getName(),
                        gamePlayer.getStartPosition(),
                        null,
                        gamePlayer.getScoreLeft(),
                        new java.util.ArrayList<>()
                ))
                .sorted(Comparator.comparing(GamePlayerDto::startPosition))
                .toList();

        return GameState.builder()
                .id(game.getId())
                .uuid(game.getUuid().toString())
                .gameType(game.getType())
                .gameStatus(game.getStatus())
                .finishRule(game.getFinishRule())
                .gamePlayers(players)
                .activePlayerId(players.isEmpty() ? null : players.getFirst().playerId())
                .currentRound(1)
                .build();
    }

    public List<Visit> mapToVisitEntities(List<VisitDto> visitDtos) {
        if (visitDtos == null) return Collections.emptyList();
        return visitDtos.stream().map(this::toEntity).toList();
    }

    private Visit toEntity(VisitDto dto) {
        Visit visit = new Visit();
        visit.setTotalScore(dto.totalScore());
        visit.setIsBust(dto.isBust());

        if (dto.shots() != null) {
            List<Shot> shots = dto.shots().stream()
                    .map(sDto -> toShotEntity(sDto, visit))
                    .toList();
            visit.setShots(shots);
        }
        return visit;
    }
    private Shot toShotEntity(ShotDto dto, Visit visit) {
        Shot shot = new Shot();
        shot.setVisit(visit);
        shot.setBaseScore(dto.baseScore());
        shot.setMultiplier(dto.multiplier());
        shot.setTotalScore(dto.totalScore());
        return shot;
    }

}
