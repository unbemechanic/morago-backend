CREATE TABLE interpreter_withdrawal_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    interpreter_profile_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    status ENUM ('PENDING', 'APPROVED', 'REJECTED', 'PAID') NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NULL,

    CONSTRAINT fk_iwr_interpreter_profile
                                             FOREIGN KEY (interpreter_profile_id)
                                             REFERENCES interpreter_profile(ID)
                                             ON DELETE RESTRICT
                                             ON UPDATE CASCADE
);

CREATE INDEX idx_iwr_interpreter
    ON interpreter_withdrawal_requests (interpreter_profile_id);

CREATE INDEX idx_iwr_status
    ON interpreter_withdrawal_requests (status);
