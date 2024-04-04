ALTER TABLE books ADD buy_status TEXT NOT NULL DEFAULT 'NotBought';

CREATE TABLE outbox (
   id BIGSERIAL NOT NULL,
   data TEXT NOT NULL,
   CONSTRAINT pk_outbox PRIMARY KEY (id)
);