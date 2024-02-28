ALTER TABLE books
DROP CONSTRAINT books_author_id_fkey,
ADD CONSTRAINT books_author_id_fkey
   FOREIGN KEY (author_id)
   REFERENCES authors (id)
   ON DELETE CASCADE;