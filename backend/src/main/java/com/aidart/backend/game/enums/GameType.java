package com.aidart.backend.game.enums;

import lombok.Getter;

@Getter
public enum GameType {
    TYPE_301("301"),
    TYPE_501("501"),
    TYPE_701("701"),
    TYPE_901("901"),
    CRICKET("CRICKET");

    private final String code;

    GameType(String code) {
        this.code = code;
    }

}
