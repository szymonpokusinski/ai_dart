CREATE TABLE games(
    id SERIAL PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    start_time TIMESTAMP DEFAULT current_timestamp,
    end_time TIMESTAMP
)