CREATE TABLE IF NOT EXISTS questions (
    id INT PRIMARY KEY,
    title VARCHAR(512) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(512) NOT NULL,
    url TEXT NOT NULL,
    difficulty INT NOT NULL,
    hints TEXT,
    likes INT NOT NULL,
    dislikes INT NOT NULL
);

CREATE TABLE IF NOT EXISTS question_syncs (
    uid UUID PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS statistics (
    question_id INT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    total_accepted VARCHAR(256) NOT NULL,
    total_submission VARCHAR(256) NOT NULL,
    total_accepted_raw BIGINT NOT NULL,
    total_submission_raw BIGINT NOT NULL,
    acceptance_rate VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS users (
    uid UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);