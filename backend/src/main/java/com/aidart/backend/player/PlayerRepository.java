package com.aidart.backend.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    boolean existsByName(String name);

    @Query("SELECT p.id FROM Player p WHERE p.id IN :ids")
    List<Long> findAllExistingIds(@Param("ids") List<Long> ids);

}
