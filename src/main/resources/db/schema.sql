CREATE TABLE IF NOT EXISTS person (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name  TEXT NOT NULL,
    last_name   TEXT NOT NULL,
    email       TEXT NOT NULL,
    birth_date  TEXT
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_person_email ON person (email);

CREATE INDEX IF NOT EXISTS ix_person_name ON person (last_name, first_name);
