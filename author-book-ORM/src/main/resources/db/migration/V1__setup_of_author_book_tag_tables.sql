CREATE TABLE IF NOT EXISTS authors
(
		id     BIGSERIAL PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name  TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS books
(
		id     BIGSERIAL PRIMARY KEY,
		author_id BIGINT REFERENCES authors (id),
		title TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS tags
(
		id     BIGSERIAL PRIMARY KEY,
		name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS book_tag
(
		book_id BIGINT REFERENCES books (id) NOT NULL,
    tag_id   BIGINT REFERENCES tags (id)   NOT NULL,
    PRIMARY KEY (book_id, tag_id)
);
