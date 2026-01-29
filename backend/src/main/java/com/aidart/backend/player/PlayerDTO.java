package com.aidart.backend.player;

public record PlayerDTO(
        Long id,
        String name
) {
    public PlayerDTO(Player player){
        this(player.getId(), player.getName());
    }
}
