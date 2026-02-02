package com.aidart.backend.shot;

import com.aidart.backend.game.GamePlayer;
import com.aidart.backend.shot.enums.ScoreMultiplier;
import com.aidart.backend.visit.Visit;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "shots")
@Data
public class Shot{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_player_id")
    private GamePlayer gamePlayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private Visit visit;

    @Column(name = "base_score")
    private Integer baseScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "multiplier")
    private ScoreMultiplier multiplier;

    @Column(name = "total_score")
    private Integer totalScore;
}
