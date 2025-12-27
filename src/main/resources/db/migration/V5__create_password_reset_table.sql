CREATE TABLE password_reset (
    id BIGINT NOT NULL AUTO_INCREMENT,

    user_id BIGINT NOT NULL,

    reset_code INT,
    token VARCHAR(64),

    code_verified BOOLEAN NOT NULL DEFAULT FALSE,
    used BOOLEAN NOT NULL DEFAULT FALSE,

    expires_at DATETIME(6) NOT NULL,
    verified_at DATETIME(6),

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_password_reset PRIMARY KEY (id),

    CONSTRAINT fk_password_reset_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES users (id)
                                        ON DELETE CASCADE
                                        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_password_reset_user_id
    ON password_reset (user_id);

CREATE INDEX idx_password_reset_token
    ON password_reset (token);

CREATE INDEX idx_password_reset_code_active
    ON password_reset (user_id, reset_code, used, code_verified, expires_at);

