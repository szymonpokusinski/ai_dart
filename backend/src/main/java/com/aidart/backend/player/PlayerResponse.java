package com.aidart.backend.player;

public record PlayerResponse(Long id, String name){
    public PlayerResponse(PlayerDTO dto){
        this(dto.id(), dto.name());
    }
}
