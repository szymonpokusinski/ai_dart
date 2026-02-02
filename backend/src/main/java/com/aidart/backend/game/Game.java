package com.aidart.backend.game;

import com.aidart.backend.game.enums.GameFinishRule;
import com.aidart.backend.game.enums.GameStatus;
import com.aidart.backend.game.enums.GameType;
import com.aidart.backend.player.Player;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", unique = true, updatable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID uuid;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GamePlayer> gamePlayers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private GameType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private GameStatus status;

    @Column(name = "start_time")
    private LocalDate startTime;

    @Column(name = "end_time")
    private LocalDate endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "finish_rule")
    private GameFinishRule finishRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_player_id")
    private GamePlayer activePlayer;

    @Column(name = "current_round")
    private int currentRound;
}
