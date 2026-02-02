package com.aidart.backend.game;

import com.aidart.backend.game.dto.CreateGameCommand;
import com.aidart.backend.game.dto.GamePlayerDto;
import com.aidart.backend.game.enums.GameFinishRule;
import com.aidart.backend.game.enums.GameStatus;
import com.aidart.backend.game.enums.GameType;
import com.aidart.backend.shot.ShotDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GameState implements Serializable {

    private Long id;
    private String uuid;
    private GameType gameType;
    private GameStatus gameStatus;
    private GameFinishRule finishRule;
    private List<GamePlayerDto> gamePlayers;

    private Long activePlayerId;
    private Integer currentRound;
}
