ALTER TABLE wallets
    ADD COLUMN created_by BIGINT NULL,
    ADD COLUMN modified_by BIGINT NULL;
ALTER TABLE wallet_transactions
    ADD COLUMN created_by BIGINT NULL,
    ADD COLUMN modified_by BIGINT NULL;
