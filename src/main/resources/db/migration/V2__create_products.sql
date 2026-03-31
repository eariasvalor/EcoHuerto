CREATE TABLE products (
                          id          UUID            NOT NULL,
                          name        VARCHAR(150)    NOT NULL,
                          variety_id  UUID            NOT NULL,
                          price       NUMERIC(10, 2)  NOT NULL,
                          currency    VARCHAR(3)      NOT NULL DEFAULT 'EUR',
                          unit        VARCHAR(20)     NOT NULL,
                          stock       INTEGER         NOT NULL DEFAULT 0,
                          available   BOOLEAN         NOT NULL DEFAULT TRUE,
                          version     INTEGER         NOT NULL DEFAULT 0,
                          CONSTRAINT pk_products      PRIMARY KEY (id),
                          CONSTRAINT fk_product_variety FOREIGN KEY (variety_id)
                              REFERENCES varieties (id)
);