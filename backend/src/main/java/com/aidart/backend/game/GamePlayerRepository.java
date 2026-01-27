package com.aidart.backend.game;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
}
