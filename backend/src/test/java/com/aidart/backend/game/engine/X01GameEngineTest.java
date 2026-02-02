package com.aidart.backend.game.engine;

import com.aidart.backend.game.GameState;
import com.aidart.backend.game.dto.GamePlayerDto;
import com.aidart.backend.game.enums.GameFinishRule;
import com.aidart.backend.game.enums.GameType;
import com.aidart.backend.shot.ShotDto;
import com.aidart.backend.shot.enums.ScoreMultiplier;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.List;

class X01GameEngineTest {

    @Test
    void shouldCreateFirstVisitWithOneShot() throws JsonProcessingException {
        var engine = new X01GameEngine();

        var p1 = player(1L, "Player 1", 501);
        var p2 = player(2L, "Player 2", 501);

        var gameState = gameState(List.of(p1, p2));

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        System.out.println("Initial game state:\n" +
                mapper.writeValueAsString(gameState));

        ShotDto shotDto = ShotDto.builder()
                .baseScore(20)
                .multiplier(ScoreMultiplier.DOUBLE)
                .build();

        ShotDto shotDto2 = ShotDto.builder()
                .baseScore(10)
                .multiplier(ScoreMultiplier.SINGLE)
                .build();

        gameState = engine.handleShot(gameState, shotDto);
        gameState = engine.handleShot(gameState, shotDto);
        gameState = engine.handleShot(gameState, shotDto);

        gameState = engine.handleShot(gameState, shotDto);
        gameState = engine.handleShot(gameState, shotDto);
        gameState = engine.handleShot(gameState, shotDto);

        gameState = engine.handleShot(gameState, shotDto2);
        gameState = engine.handleShot(gameState, shotDto2);
        gameState = engine.handleShot(gameState, shotDto2);

        System.out.println("Updated game state:\n" +
                mapper.writeValueAsString(gameState));
    }

    private GameState gameState(
            List<GamePlayerDto> players
    )
    {
        return GameState.builder()
                .gameType(GameType.TYPE_501)
                .finishRule(GameFinishRule.ANY_OUT)
                .gamePlayers(players)
                .activePlayerId(players.getFirst().playerId())
                .currentRound(1)
                .build();
    }
    private GamePlayerDto player(Long id, String name, Integer startPosition)
    {
        return GamePlayerDto.builder()
                .id(id)
                .playerId(id)
                .playerName(name)
                .startPosition(startPosition)
                .scoreLeft(501)
                .build();
    }
}