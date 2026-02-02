CREATE TABLE visits(
    id SERIAL PRIMARY KEY,
    game_player_id INTEGER REFERENCES game_players(id) ON DELETE SET NULL,
    total_score INTEGER,
    is_bust BOOLEAN
)