CREATE TABLE varieties (
                           id              UUID            NOT NULL,
                           name            VARCHAR(100)    NOT NULL,
                           product_category VARCHAR(100)   NOT NULL,
                           CONSTRAINT pk_varieties PRIMARY KEY (id)
);