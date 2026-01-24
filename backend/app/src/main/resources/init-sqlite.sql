CREATE TABLE IF NOT EXISTS data_syncs (
    uid TEXT PRIMARY KEY,
    sync_type VARCHAR(16) NOT NULL,
    timestamp TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS questions (
    uid TEXT PRIMARY KEY,
    problem_id INTEGER NOT NULL,
    question TEXT NOT NULL,
    complexity TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS questionnaires (
    uid TEXT PRIMARY KEY,
    is_complete INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS submissions (
    uid TEXT PRIMARY KEY,
    questionnaire_uid TEXT NOT NULL,
    question_uid TEXT NOT NULL,
    answer INTEGER NOT NULL,
    FOREIGN KEY (questionnaire_uid) REFERENCES questionnaires(uid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS next_questions (
    uid TEXT PRIMARY KEY,
    questionnaire_uid TEXT NOT NULL,
    question_uid TEXT NOT NULL,
    FOREIGN KEY (questionnaire_uid) REFERENCES questionnaires(uid) ON DELETE CASCADE,
    FOREIGN KEY (question_uid) REFERENCES questions(uid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS problems (
    id INTEGER PRIMARY KEY,
    title VARCHAR(512) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(512) NOT NULL,
    url TEXT NOT NULL,
    difficulty VARCHAR(50) NOT NULL,
    likes INTEGER NOT NULL,
    dislikes INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS problem_hints (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    problem_id INTEGER NOT NULL,
    hint TEXT NOT NULL,
    FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
    uid TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL
);
