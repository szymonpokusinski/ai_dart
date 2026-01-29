package com.aidart.backend.game.engine;

import com.aidart.backend.game.enums.GameType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameEngineFactory {

    private final List<GameEngine> engines;

    public GameEngineFactory(List<GameEngine> engines) {
        this.engines = engines;
    }

    public GameEngine getEngine(GameType type) {
        return engines.stream()
                .filter(engine -> engine.supportsGameType(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nie znaleziono silnika dla typu gry: " + type
                ));
    }
}