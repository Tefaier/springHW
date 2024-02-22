CREATE IF NOT EXISTS TABLE authors (
		id     BIGSERIAL PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name  TEXT NOT NULL,
    books_id BIGINT REFERENCES books (id)
);

CREATE IF NOT EXISTS TABLE books (
		id     BIGSERIAL PRIMARY KEY,
		author_id BIGINT REFERENCES authors (id),
		title TEXT NOT NULL,
		tags_id REFERENCES tags (id)
);

CREATE IF NOT EXISTS TABLE tags (
		id     BIGSERIAL PRIMARY KEY,
		name TEXT NOT NULL
);