ALTER TABLE wallets
    MODIFY created_by VARCHAR(50),
    MODIFY modified_by VARCHAR(50);
ALTER TABLE wallet_transactions
    MODIFY created_by VARCHAR(50),
    MODIFY modified_by VARCHAR(50);
