package com.aidart.backend.visit;

import com.aidart.backend.game.GamePlayer;
import com.aidart.backend.shot.Shot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "visits")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_player_id")
    private GamePlayer gamePlayer;

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "is_bust")
    private Boolean isBust;

    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Shot> shots = new ArrayList<>();

}
