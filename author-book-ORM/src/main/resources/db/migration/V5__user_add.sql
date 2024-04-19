CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username TEXT  NOT NULL UNIQUE,
    password TEXT  NOT NULL,
    roles    JSONB NOT NULL
);

ALTER TABLE authors ADD username TEXT NOT NULL UNIQUE;