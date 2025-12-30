CREATE TABLE wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BIGINT NOT NULL,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uk_wallet_user UNIQUE (user_id),
    CONSTRAINT chk_wallet_balance_non_negative CHECK (balance >= 0)
);

CREATE INDEX idx_wallet_user_id
    ON wallets (user_id);

CREATE TABLE wallet_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    wallet_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,

    amount DECIMAL(19,2) NOT NULL,
    balance_before DECIMAL(19,2) NOT NULL,
    balance_after DECIMAL(19,2) NOT NULL,

    reference_id VARCHAR(100),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_wallet_transaction_wallet
        FOREIGN KEY (wallet_id)
        REFERENCES wallets (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_wallet_tx_amount_positive
        CHECK (amount > 0)
);

CREATE INDEX idx_wallet_transactions_wallet_id
    ON wallet_transactions (wallet_id);

CREATE INDEX idx_wallet_transactions_reference_id
    ON wallet_transactions (reference_id);

CREATE INDEX idx_wallet_transactions_type
    ON wallet_transactions (type);

