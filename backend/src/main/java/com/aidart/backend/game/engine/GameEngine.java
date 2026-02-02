package com.aidart.backend.game.engine;

import com.aidart.backend.game.GameState;
import com.aidart.backend.game.enums.GameType;
import com.aidart.backend.shot.ShotDto;

public interface GameEngine{
    GameState handleShot(GameState gameState,ShotDto shotDto);
    GameState deleteShot(GameState gameState);
    GameState finishGame(GameState gameState);
    boolean supportsGameType(GameType gameType);
}
