CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,

    first_name VARCHAR(255),
    last_name VARCHAR(255),

    password VARCHAR(255) NOT NULL,

    phone_number VARCHAR(50) NOT NULL,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,

    is_verified BOOLEAN NOT NULL DEFAULT FALSE,

    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),

    CONSTRAINT pk_users PRIMARY KEY (id),

    CONSTRAINT uk_users_phone_number UNIQUE (phone_number)
) ENGINE=InnoDB default charset=utf8mb4;


CREATE TABLE roles (
    id BIGINT NOT NULL AUTO_INCREMENT,

    name VARCHAR(50) NOT NULL,

    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),

    CONSTRAINT pk_roles PRIMARY KEY (id),

    CONSTRAINT uk_roles_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_users_roles PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_users_roles_user FOREIGN KEY (user_id)
                                 REFERENCES users (id)
                                 ON DELETE CASCADE
                                 ON UPDATE CASCADE,

    CONSTRAINT fk_users_roles_role FOREIGN KEY (role_id)
                                 REFERENCES roles (id)
                                 ON DELETE CASCADE
                                 ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4;
CREATE INDEX idx_users_roles_role_id ON users_roles (role_id);

CREATE TABLE interpreter_profile (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    level VARCHAR(255),
    hourly_rate DECIMAL(19,2),
    is_active BOOLEAN DEFAULT FALSE,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_interpreter PRIMARY KEY (id),
    CONSTRAINT fk_interpreter_user FOREIGN KEY (user_id)
                                  REFERENCES users (id)
                                  ON DELETE CASCADE
                                  ON UPDATE CASCADE,

    CONSTRAINT uq_interpreter_user UNIQUE (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE languages (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_languages PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_languages_name ON languages (name);

CREATE TABLE interpreter_languages (
    interpreter_id BIGINT NOT NULL,
    language_id BIGINT NOT NULL,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_interpreter_languages PRIMARY KEY (interpreter_id, language_id),
    CONSTRAINT fk_interpreter_languages_interpreter FOREIGN KEY (interpreter_id)
                                           REFERENCES interpreter_profile (id)
                                           ON DELETE CASCADE
                                           ON UPDATE CASCADE,
    CONSTRAINT fk_interpreter_languages_language FOREIGN KEY (language_id)
                                           REFERENCES languages (id)
                                           ON DELETE CASCADE
                                           ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE refresh_token (
    id BIGINT NOT NULL AUTO_INCREMENT,
    token VARCHAR(512) NOT NULL,
    expiration_time DATETIME(6) NOT NULL,
    user_id BIGINT NOT NULL,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_refresh_token PRIMARY KEY (id),
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id)
                                   REFERENCES users (id)
                                   ON DELETE CASCADE
                                   ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE UNIQUE INDEX idx_refresh_token_token ON refresh_token (token);

CREATE INDEX idx_refresh_token_user_id ON refresh_token (user_id);

CREATE TABLE file (
    id BIGINT NOT NULL AUTO_INCREMENT,
    interpreter_profile_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(1024) NOT NULL,
    file_type VARCHAR(100) NOT NULL,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_file PRIMARY KEY (id),
    CONSTRAINT fk_file_interpreter_profile FOREIGN KEY (interpreter_profile_id)
                          REFERENCES interpreter_profile (id)
                          ON DELETE CASCADE
                          ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_file_interpreter_profile ON file (interpreter_profile_id);

CREATE TABLE client_profile (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_client_profile PRIMARY KEY (id),
    CONSTRAINT fk_client_profile_user FOREIGN KEY (user_id)
                                    REFERENCES users (id)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_client_profile_user ON client_profile (user_id);

CREATE TABLE deposits (
    id BIGINT NOT NULL AUTO_INCREMENT,
    client_profile_id BIGINT NOT NULL,
    note VARCHAR(255),
    status VARCHAR(50),
    amount DECIMAL(19,2) NOT NULL,
    deposited_at DATETIME(6),
    processed_at DATETIME(6),

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_deposits PRIMARY KEY (id),
    CONSTRAINT fk_deposits_client_profile FOREIGN KEY (client_profile_id)
                              REFERENCES client_profile (id)
                              ON DELETE CASCADE
                              ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_deposits_client_profile ON deposits (client_profile_id);

CREATE TABLE charges (
    id BIGINT NOT NULL AUTO_INCREMENT,
    client_profile_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    note VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    requested_at DATETIME(6),
    processed_at DATETIME(6),

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_charges PRIMARY KEY (id),
    CONSTRAINT fk_charges_client_profile FOREIGN KEY (client_profile_id)
                             REFERENCES client_profile (id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_charges_client_profile ON charges (client_profile_id);

CREATE TABLE call_topics (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_call_topics PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_call_topics_is_active ON call_topics (is_active);

CREATE TABLE calls (
    id BIGINT NOT NULL AUTO_INCREMENT,

    client_profile_id BIGINT NOT NULL,
    interpreter_profile_id BIGINT NOT NULL,
    call_started_at DATETIME(6),
    call_ended_at DATETIME(6),
    duration BIGINT,
    total_price DECIMAL(19,2),
    status VARCHAR(50),
    call_topic BIGINT,

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_calls PRIMARY KEY (id),
    CONSTRAINT fk_calls_client_profile FOREIGN KEY (client_profile_id) REFERENCES client_profile(id),
    CONSTRAINT fk_calls_interpreter_profile FOREIGN KEY (interpreter_profile_id) REFERENCES interpreter_profile(id),
    CONSTRAINT fk_calls_call_topic FOREIGN KEY (call_topic) REFERENCES call_topics(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE withdrawals (
    id BIGINT NOT NULL AUTO_INCREMENT,

    interpreter_profile_id BIGINT NOT NULL,
    requested_amount DECIMAL(19,2),
    status VARCHAR(50) NOT NULL,
    withdrawal_account VARCHAR(255) NOT NULL,
    withdrawal_details VARCHAR(255) NOT NULL,
    requested_at DATETIME(6),
    processed_at DATETIME(6),

    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_withdrawals PRIMARY KEY (id),
    CONSTRAINT fk_withdrawals_interpreter_profile FOREIGN KEY (interpreter_profile_id) REFERENCES interpreter_profile(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;




