package com.aidart.backend.game;

import com.aidart.backend.player.Player;
import com.aidart.backend.visit.Visit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_players")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(mappedBy = "gamePlayer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Visit> visits = new ArrayList<>();

    @Column(name = "start_position")
    private Integer startPosition;

    @Column(name = "final_position")
    private Integer finalPosition;

    @Column(name = "score_left")
    private Integer scoreLeft;

    public void setFinalVisits(List<Visit> newVisits) {
        this.visits.clear();
        if (newVisits != null) {
            newVisits.forEach(v -> v.setGamePlayer(this));
            this.visits.addAll(newVisits);
        }
    }
}
