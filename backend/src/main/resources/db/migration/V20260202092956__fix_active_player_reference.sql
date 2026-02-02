ALTER TABLE games DROP CONSTRAINT IF EXISTS games_active_player_id_fkey;

ALTER TABLE games DROP COLUMN IF EXISTS active_player_id;

ALTER TABLE games ADD COLUMN active_player_id INTEGER REFERENCES game_players(id) ON DELETE SET NULL;