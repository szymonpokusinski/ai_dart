package com.aidart.backend.game.service;

import com.aidart.backend.exception.ResourceNotFoundException;
import com.aidart.backend.game.*;
import com.aidart.backend.game.dto.CreateGameCommand;
import com.aidart.backend.game.dto.GameDto;
import com.aidart.backend.game.engine.GameEngineFactory;
import com.aidart.backend.game.enums.GameStatus;
import com.aidart.backend.game.enums.GameType;
import com.aidart.backend.player.Player;
import com.aidart.backend.player.PlayerRepository;
import com.aidart.backend.shot.ShotDto;
import com.aidart.backend.shot.ThrowRequest;
import com.aidart.backend.visit.Visit;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final GameMapper gameMapper;
    private final RedisTemplate<String, GameState> redisTemplate;
    private final GameEngineFactory gameEngineFactory;

    @Transactional
    public Game create(CreateGameCommand command) {
        List<Long> playersIds = command.playersIds();

        List<Long> existingIds = playerRepository.findAllExistingIds(playersIds);
        if (existingIds.size() != playersIds.size()) {
            List<Long> missingIds = new ArrayList<>(playersIds);
            missingIds.removeAll(existingIds);
            throw new ResourceNotFoundException("Players not found: " + missingIds);
        }

        Game game = Game.builder()
                .uuid(UUID.randomUUID())
                .type(command.type())
                .finishRule(command.finishRule())
                .startTime(LocalDate.now())
                .status(GameStatus.IN_PROGRESS)
                .currentRound(1)
                .build();

        Game savedGame = gameRepository.save(game);
        int initialScore = getInitialScore(command.type());

        List<GamePlayer> gamePlayers = IntStream.range(0, playersIds.size())
                .mapToObj(index -> {
                    Long playerId = playersIds.get(index);
                    Player playerProxy = playerRepository.getReferenceById(playerId);

                    return GamePlayer.builder()
                            .game(savedGame)
                            .player(playerProxy)
                            .startPosition(index + 1)
                            .scoreLeft(initialScore)
                            .build();
                })
                .toList();

        List<GamePlayer> savedGamePlayers = gamePlayerRepository.saveAll(gamePlayers);

        GamePlayer firstPlayer = savedGamePlayers.stream()
                .filter(gp -> gp.getStartPosition() == 1)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No first player found"));

        savedGame.setActivePlayer(firstPlayer);

        GameState gameState = gameMapper.toGameState(savedGame, gamePlayers);

        gameState.setActivePlayerId(firstPlayer.getId());

        String redisKey = "game:" + gameState.getUuid();
        redisTemplate.opsForValue().set(redisKey, gameState, Duration.ofHours(12));

        return savedGame;
    }

    @Transactional
    public GameDto finalizeGame(String uuid) {
        GameState state = getGameState(uuid);

        Game game = gameRepository.findByUuid(UUID.fromString(uuid))
                .orElseThrow(() -> new EntityNotFoundException("Game not found: " + uuid));

        game.setStatus(GameStatus.FINISHED);
        game.setEndTime(LocalDate.now());

        game.setActivePlayer(gamePlayerRepository.getReferenceById(state.getActivePlayerId()));
        game.setCurrentRound(state.getCurrentRound());

        Map<Long, GamePlayer> dbPlayersMap = game.getGamePlayers().stream()
                .collect(Collectors.toMap(GamePlayer::getId, Function.identity()));

        for (var playerDto : state.getGamePlayers()) {
            GamePlayer dbPlayer = dbPlayersMap.get(playerDto.id());

            if (dbPlayer != null) {
                List<Visit> finalVisits = gameMapper.mapToVisitEntities(playerDto.visits());
                dbPlayer.setFinalVisits(finalVisits);
            }
        }
        gameRepository.save(game);

        redisTemplate.delete(uuid);

        return GameDto.fromEntity(game);
    }

    public GameState getGameState(String gameUuid) {
        String redisKey = "game:" + gameUuid;
        GameState state = redisTemplate.opsForValue().get(redisKey);

        if (state != null) {
            return state;
        }
        UUID uuid = UUID.fromString(gameUuid);

        return gameRepository.findByUuid(uuid)
                .map(game -> {
                    List<GamePlayer> players = gamePlayerRepository.findAllByGame(game);
                    GameState gameState = gameMapper.toGameState(game, players);
                    redisTemplate.opsForValue().set(redisKey, gameState, Duration.ofHours(12));
                    return gameState;
                })
                .orElse(null);
    }

    public GameState processThrow(String uuid, ThrowRequest request) {

        GameState gameState = this.getGameState(uuid);

        ShotDto shotDto = ShotDto.builder()
                .baseScore(request.score())
                .multiplier(request.multiplier())
                .build();

        GameState updatedState = gameEngineFactory
                .getEngine(gameState.getGameType())
                .handleShot(gameState, shotDto);

        String redisKey = "game:" + uuid;
        redisTemplate.opsForValue().set(redisKey, updatedState, Duration.ofHours(12));

        return updatedState;
    }

    public GameState deleteShot(String uuid) {
        GameState gameState = this.getGameState(uuid);
        GameState updatedState = gameEngineFactory
                .getEngine(gameState.getGameType())
                .deleteShot(gameState);

        String redisKey = "game:" + uuid;
        redisTemplate.opsForValue().set(redisKey, updatedState, Duration.ofHours(12));

        return updatedState;
    }

    private int getInitialScore(GameType gameType) {
        return switch (gameType) {
            case TYPE_301 -> 301;
            case TYPE_501 -> 501;
            case TYPE_701 -> 701;
            case TYPE_901 -> 901;
            default -> 0;
        };
    }

    public GameState finishGame(String gameUuid) {
        GameState gameState = this.getGameState(gameUuid);
        return gameEngineFactory.getEngine(gameState.getGameType())
                .finishGame(gameState);
    }
}