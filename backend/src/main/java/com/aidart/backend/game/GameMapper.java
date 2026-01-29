package com.aidart.backend.game;

import com.aidart.backend.game.dto.GamePlayerDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class GameMapper {

    public GameState toGameState(Game game, List<GamePlayer> gamePlayers) {
        List<GamePlayerDto> players = gamePlayers.stream()
                .map(gamePlayer -> new GamePlayerDto(
                        gamePlayer.getId(),
                        game.getId(),
                        gamePlayer.getPlayer().getId(),
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
}
