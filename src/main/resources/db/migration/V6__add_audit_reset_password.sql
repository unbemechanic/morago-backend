ALTER TABLE password_reset
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN modified_by VARCHAR(255) NULL;

