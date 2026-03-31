CREATE TABLE orders (
                        id          UUID        NOT NULL,
                        visible_id  VARCHAR(20) NOT NULL,
                        customer_id UUID        NOT NULL,
                        status      VARCHAR(30) NOT NULL,
                        created_at  TIMESTAMP   NOT NULL,
                        version     INTEGER     NOT NULL DEFAULT 0,
                        CONSTRAINT pk_orders        PRIMARY KEY (id),
                        CONSTRAINT uq_visible_id    UNIQUE (visible_id),
                        CONSTRAINT fk_order_customer FOREIGN KEY (customer_id)
                            REFERENCES customers (id)
);

CREATE TABLE order_lines (
                             id         UUID    NOT NULL,
                             order_id   UUID    NOT NULL,
                             product_id UUID    NOT NULL,
                             quantity   INTEGER NOT NULL,
                             CONSTRAINT pk_order_lines       PRIMARY KEY (id),
                             CONSTRAINT fk_line_order        FOREIGN KEY (order_id)
                                 REFERENCES orders (id),
                             CONSTRAINT fk_line_product      FOREIGN KEY (product_id)
                                 REFERENCES products (id)
);