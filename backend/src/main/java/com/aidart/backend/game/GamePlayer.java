package com.aidart.backend.game;

import com.aidart.backend.player.Player;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "game_players")
@Data
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne()
    @JoinColumn(name = "player_id")
    private Player player;

    @Column(name = "start_position")
    private Integer startPosition;

    @Column(name = "final_position")
    private Integer finalPosition;

    @Column(name = "score_left")
    private Integer scoreLeft;
}
