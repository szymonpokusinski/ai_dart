package com.aidart.backend.game.service;

import com.aidart.backend.exception.ResourceNotFoundException;
import com.aidart.backend.game.*;
import com.aidart.backend.game.dto.CreateGameCommand;
import com.aidart.backend.game.dto.GamePlayerDto;
import com.aidart.backend.game.dto.PlayerOrder;
import com.aidart.backend.game.enums.GameStatus;
import com.aidart.backend.game.enums.GameType;
import com.aidart.backend.player.Player;
import com.aidart.backend.player.PlayerRepository;
import com.aidart.backend.shot.ShotDto;
import com.aidart.backend.shot.ThrowRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GameService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final GameMapper gameMapper;
    private final RedisTemplate<String, GameState> redisTemplate;

    @Transactional
    public Game create(CreateGameCommand command) {

        List<Long> playersIds = new java.util.ArrayList<>(command.players().stream()
                .map(PlayerOrder::playerId)
                .toList());

        List<Long> existingIds = playerRepository.findAllExistingIds(playersIds);

        if (existingIds.size() != playersIds.size()) {
            playersIds.removeAll(existingIds);
            throw new ResourceNotFoundException("Players not found: " + playersIds);
        }

        Game game = Game.builder()
                .uuid(UUID.randomUUID())
                .type(command.type())
                .finishRule(command.finishRule())
                .startTime(LocalDate.now())
                .status(GameStatus.IN_PROGRESS)
                .build();

        Game savedGame = gameRepository.save(game);

        int initialScore = getInitialScore(command.type());

        List<GamePlayer> gamePlayers = command.players().stream()
                .map(order -> {
                    Player playerProxy = playerRepository.getReferenceById(order.playerId());

                    return GamePlayer.builder()
                            .game(savedGame)
                            .player(playerProxy)
                            .startPosition(order.positon())
                            .scoreLeft(initialScore)
                            .build();
                })
                .toList();

        GameState gameState = gameMapper.toGameState(savedGame, gamePlayers);

        String redisKey = "game:" + gameState.getUuid();
        redisTemplate.opsForValue().set(redisKey, gameState, Duration.ofHours(12));

        gamePlayerRepository.saveAll(gamePlayers);

        return savedGame;
    }

    public GameState getGameState(String gameUuid) {
        String redisKey = "game:" + gameUuid;
        GameState state = redisTemplate.opsForValue().get(redisKey);

        if(state != null){
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

    public void processThrow(String uuid, ThrowRequest request){
        GameState gameState = redisTemplate.opsForValue().get(uuid);

        if(gameState == null){
            throw new IllegalArgumentException("Game not found with UUID: " + uuid);
        }

        GamePlayerDto currentPlayer =  gameState.getGamePlayers().stream()
                .filter(player -> player.playerId().equals(gameState.getActivePlayerId()))
                .findFirst()
                .orElse(null);
    }

    private int getInitialScore(GameType gameType){
        return switch (gameType){
            case TYPE_301 -> 301;
            case TYPE_501 ->  501;
            case TYPE_701 -> 701;
            case TYPE_901 -> 901;
            default -> 0;
        };
    }
}