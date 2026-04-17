CREATE TABLE notifications (
                               id               UUID        NOT NULL,
                               type             VARCHAR(30) NOT NULL,
                               customer_id      UUID        NOT NULL,
                               template_id      VARCHAR(100),
                               message_text     TEXT,
                               media_id         VARCHAR(200),
                               delivery_status  VARCHAR(30) NOT NULL,
                               attempts         INT         NOT NULL DEFAULT 0,
                               created_at       TIMESTAMP   NOT NULL,
                               sent_at          TIMESTAMP,

                               CONSTRAINT pk_notifications PRIMARY KEY (id),
                               CONSTRAINT fk_notifications_customer
                                   FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE INDEX idx_notifications_delivery_status ON notifications(delivery_status);
CREATE INDEX idx_notifications_customer_id ON notifications(customer_id);