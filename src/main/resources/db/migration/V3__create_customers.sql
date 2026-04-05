CREATE TABLE customers (
                           id                   UUID         NOT NULL,
                           name                 VARCHAR(150) NOT NULL,
                           email                VARCHAR(255) NOT NULL,
                           password_hash        VARCHAR(255) NOT NULL,
                           phone_country_code   VARCHAR(10),
                           phone_number         VARCHAR(20),
                           address_street_type  VARCHAR(50),
                           address_street       VARCHAR(255),
                           address_number       VARCHAR(20),
                           address_floor        VARCHAR(20),
                           address_city         VARCHAR(100),
                           address_postal_code  VARCHAR(20),
                           address_province     VARCHAR(100),
                           created_at           TIMESTAMP    NOT NULL,
                           version              INTEGER      NOT NULL DEFAULT 0,
                           CONSTRAINT pk_customers      PRIMARY KEY (id),
                           CONSTRAINT uq_customer_email UNIQUE (email)
);