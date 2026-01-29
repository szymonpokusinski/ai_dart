package com.aidart.backend.game;

import org.springframework.data.jpa.repository.JpaRepository;

import java.nio.channels.FileChannel;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByUuid(UUID uuid);
}
