CREATE TABLE balance (
   id BIGINT NOT NULL,
   value BIGINT NOT NULL,
   CONSTRAINT pk_balance PRIMARY KEY (id)
);

CREATE TABLE outbox (
   id BIGSERIAL NOT NULL,
   data TEXT NOT NULL,
   CONSTRAINT pk_outbox PRIMARY KEY (id)
);