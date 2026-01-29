CREATE TABLE shots(
    id SERIAL PRIMARY KEY,
    game_player_id INTEGER REFERENCES game_players(id) ON DELETE CASCADE,
    base_score INTEGER NOT NULL,
    multiplier VARCHAR(20) NOT NULL,
    total_score INTEGER NOT NULL
)