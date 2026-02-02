package com.aidart.backend.game.engine;

import com.aidart.backend.game.GameState;
import com.aidart.backend.game.enums.GameType;
import com.aidart.backend.shot.ShotDto;

public class CricketGameEngine implements GameEngine{

    @Override
    public GameState handleShot(GameState gameState, ShotDto shotDto) {
        return null;
    }

    @Override
    public GameState deleteShot(GameState gameState) {
        return null;
    }

    @Override
    public GameState finishGame(GameState gameState) {
        return null;
    }

    @Override
    public boolean supportsGameType(GameType gameType) {
        return gameType == GameType.CRICKET;
    }
}
