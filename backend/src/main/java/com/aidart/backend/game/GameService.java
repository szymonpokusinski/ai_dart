package com.aidart.backend.game;

import com.aidart.backend.exception.ResourceNotFoundException;
import com.aidart.backend.game.dto.CreateGameCommand;
import com.aidart.backend.game.dto.PlayerOrder;
import com.aidart.backend.game.enums.GameStatus;
import com.aidart.backend.player.Player;
import com.aidart.backend.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final GamePlayerRepository gamePlayerRepository;

    @Transactional
    public Game create(CreateGameCommand command){

        List<Long> playersIds = new java.util.ArrayList<>(command.players().stream()
                .map(PlayerOrder::playerId)
                .toList());

        List<Long> existingIds = playerRepository.findAllExistingIds(playersIds);

        if (existingIds.size() != playersIds.size()) {
            playersIds.removeAll(existingIds);
            throw new ResourceNotFoundException("Players not found: " + playersIds);
        }

        Game game =  Game.builder()
                .type(command.type())
                .finishRule(command.finishRule())
                .startTime(LocalDate.now())
                .status(GameStatus.IN_PROGRESS)
                .build();

        Game savedGame = gameRepository.save(game);

        List<GamePlayer> gamePlayers = command.players().stream()
                .map(order -> {
                    Player playerProxy = playerRepository.getReferenceById(order.playerId());

                    return GamePlayer.builder()
                            .game(savedGame)
                            .player(playerProxy)
                            .startPosition(order.positon())
                            .build();
                })
                .toList();

        gamePlayerRepository.saveAll(gamePlayers);

        return savedGame;
    }
}
