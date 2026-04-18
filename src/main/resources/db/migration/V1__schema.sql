-- =============================================
-- V1 — Schema completo (estado final V14)
-- =============================================

CREATE TABLE varieties (
                           id               UUID            NOT NULL,
                           name             VARCHAR(100)    NOT NULL,
                           product_category VARCHAR(100)    NOT NULL,
                           image_url        VARCHAR(500),
                           CONSTRAINT pk_varieties PRIMARY KEY (id)
);

CREATE TABLE products (
                          id         UUID            NOT NULL,
                          name       VARCHAR(150)    NOT NULL,
                          variety_id UUID            NOT NULL,
                          price      NUMERIC(10, 2)  NOT NULL,
                          currency   VARCHAR(3)      NOT NULL DEFAULT 'EUR',
                          unit       VARCHAR(20)     NOT NULL,
                          stock      INTEGER         NOT NULL DEFAULT 0,
                          available  BOOLEAN         NOT NULL DEFAULT TRUE,
                          image_url  VARCHAR(500),
                          version    INTEGER         NOT NULL DEFAULT 0,
                          CONSTRAINT pk_products       PRIMARY KEY (id),
                          CONSTRAINT fk_product_variety FOREIGN KEY (variety_id) REFERENCES varieties (id)
);

CREATE TABLE customers (
                           id                  UUID         NOT NULL,
                           name                VARCHAR(150) NOT NULL,
                           email               VARCHAR(255) NOT NULL,
                           password_hash       VARCHAR(255) NOT NULL,
                           phone_country_code  VARCHAR(10),
                           phone_number        VARCHAR(20),
                           address_street_type VARCHAR(50),
                           address_street      VARCHAR(255),
                           address_number      VARCHAR(20),
                           address_floor       VARCHAR(20),
                           address_city        VARCHAR(100),
                           address_postal_code VARCHAR(20),
                           address_province    VARCHAR(100),
                           created_at          TIMESTAMP    NOT NULL,
                           version             INTEGER      NOT NULL DEFAULT 0,
                           CONSTRAINT pk_customers      PRIMARY KEY (id),
                           CONSTRAINT uq_customer_email UNIQUE (email)
);

CREATE TABLE administrators (
                                id            UUID         NOT NULL,
                                name          VARCHAR(150) NOT NULL,
                                email         VARCHAR(255) NOT NULL,
                                password_hash VARCHAR(255) NOT NULL,
                                permission    VARCHAR(20)  NOT NULL,
                                active        BOOLEAN      NOT NULL DEFAULT TRUE,
                                created_at    TIMESTAMP    NOT NULL,
                                version       INTEGER      NOT NULL DEFAULT 0,
                                CONSTRAINT pk_administrators      PRIMARY KEY (id),
                                CONSTRAINT uq_administrator_email UNIQUE (email)
);

CREATE TABLE orders (
                        id         UUID        NOT NULL,
                        visible_id VARCHAR(20) NOT NULL,
                        customer_id UUID       NOT NULL,
                        status     VARCHAR(30) NOT NULL,
                        created_at TIMESTAMP   NOT NULL,
                        version    INTEGER     NOT NULL DEFAULT 0,
                        CONSTRAINT pk_orders         PRIMARY KEY (id),
                        CONSTRAINT uq_visible_id     UNIQUE (visible_id),
                        CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE order_lines (
                             id         UUID    NOT NULL,
                             order_id   UUID    NOT NULL,
                             product_id UUID    NOT NULL,
                             quantity   INTEGER NOT NULL,
                             CONSTRAINT pk_order_lines  PRIMARY KEY (id),
                             CONSTRAINT fk_line_order   FOREIGN KEY (order_id)   REFERENCES orders (id),
                             CONSTRAINT fk_line_product FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE notifications (
                               id              UUID         NOT NULL,
                               type            VARCHAR(30)  NOT NULL,
                               customer_id     UUID         NOT NULL,
                               recipient_phone VARCHAR(30),
                               template_id     VARCHAR(100),
                               message_text    TEXT,
                               media_id        VARCHAR(200),
                               delivery_status VARCHAR(30)  NOT NULL,
                               attempts        INT          NOT NULL DEFAULT 0,
                               created_at      TIMESTAMP    NOT NULL,
                               sent_at         TIMESTAMP,
                               CONSTRAINT pk_notifications PRIMARY KEY (id)
);

CREATE INDEX idx_notifications_delivery_status ON notifications (delivery_status);
CREATE INDEX idx_notifications_customer_id     ON notifications (customer_id);