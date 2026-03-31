CREATE TABLE customers (
                           id            UUID            NOT NULL,
                           name          VARCHAR(150)    NOT NULL,
                           email         VARCHAR(255)    NOT NULL,
                           password_hash VARCHAR(255)    NOT NULL,
                           created_at    TIMESTAMP       NOT NULL,
                           version       INTEGER         NOT NULL DEFAULT 0,
                           CONSTRAINT pk_customers   PRIMARY KEY (id),
                           CONSTRAINT uq_customer_email UNIQUE (email)
);