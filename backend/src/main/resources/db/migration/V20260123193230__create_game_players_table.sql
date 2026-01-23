CREATE TABLE game_players
(
    id             SERIAL PRIMARY KEY,
    game_id        INTEGER REFERENCES games (id) ON DELETE CASCADE,
    player_id      INTEGER REFERENCES players (id) ON DELETE SET NULL, -- TWOJE SET NULL
    start_position INTEGER,
    final_position INTEGER,
    score_left     INTEGER
);