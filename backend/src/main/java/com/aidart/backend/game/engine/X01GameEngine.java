package com.aidart.backend.game.engine;

import com.aidart.backend.game.GameState;
import com.aidart.backend.game.enums.GameType;
import com.aidart.backend.shot.ShotDto;
import org.springframework.stereotype.Service;

@Service
public class X01GameEngine implements GameEngine{
    @Override
    public GameState handleShot(ShotDto shotDto) {
        return null;
    }

    @Override
    public boolean supportsGameType(GameType gameType) {
        return gameType != GameType.CRICKET;
    }
}
