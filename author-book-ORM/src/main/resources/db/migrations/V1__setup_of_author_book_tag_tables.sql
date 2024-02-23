CREATE IF NOT EXISTS TABLE authors (
		id     BIGSERIAL PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name  TEXT NOT NULL,
    books_id BIGINT REFERENCES books (id)
);

CREATE IF NOT EXISTS TABLE books (
		id     BIGSERIAL PRIMARY KEY,
		author_id BIGINT REFERENCES authors (id),
		title TEXT NOT NULL
);

CREATE IF NOT EXISTS TABLE book_tag (
		book_id BIGINT REFERENCES books (id) NOT NULL,
    tag_id   BIGINT REFERENCES tags (id)   NOT NULL,
    PRIMARY KEY (book_id, tag_id)
);

CREATE IF NOT EXISTS TABLE tags (
		id     BIGSERIAL PRIMARY KEY,
		name TEXT NOT NULL UNIQUE
);