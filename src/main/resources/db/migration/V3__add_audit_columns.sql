ALTER TABLE roles
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN modified_by VARCHAR(255) NULL;

ALTER TABLE users
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN modified_by VARCHAR(255) NULL;



ALTER TABLE users_roles
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN modified_by VARCHAR(255) NULL;

ALTER TABLE interpreter_profile
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN modified_by VARCHAR(255) NULL;

ALTER TABLE interpreter_languages
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN modified_by VARCHAR(255) NULL;

ALTER TABLE refresh_token
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN modified_by VARCHAR(255) NULL;

ALTER TABLE file
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN modified_by VARCHAR(255) NULL;

ALTER TABLE client_profile
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN modified_by VARCHAR(255) NULL;

ALTER TABLE deposits
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN modified_by VARCHAR(255) NULL;

ALTER TABLE calls
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN modified_by VARCHAR(255) NULL;

ALTER TABLE call_topics
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN modified_by VARCHAR(255) NULL;

ALTER TABLE withdrawals
    ADD COLUMN created_by VARCHAR(255) NULL,
    ADD COLUMN modified_by VARCHAR(255) NULL;
