CREATE TABLE administrators (
                                id            UUID            NOT NULL,
                                name          VARCHAR(150)    NOT NULL,
                                email         VARCHAR(255)    NOT NULL,
                                password_hash VARCHAR(255)    NOT NULL,
                                permission    VARCHAR(20)     NOT NULL,
                                active        BOOLEAN         NOT NULL DEFAULT TRUE,
                                created_at    TIMESTAMP       NOT NULL,
                                version       INTEGER         NOT NULL DEFAULT 0,
                                CONSTRAINT pk_administrators     PRIMARY KEY (id),
                                CONSTRAINT uq_administrator_email UNIQUE (email)
);