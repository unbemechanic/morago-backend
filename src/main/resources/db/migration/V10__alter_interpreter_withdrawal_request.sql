ALTER TABLE interpreter_withdrawal_requests
    ADD COLUMN created_by BIGINT NULL,
    ADD COLUMN modified_by BIGINT NULL;
