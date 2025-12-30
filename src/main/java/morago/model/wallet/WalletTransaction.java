package morago.model.wallet;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import morago.enums.WalletTransactionType;
import morago.monitor.Audit;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "wallet_transactions")
public class WalletTransaction extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    private WalletTransactionType type;

    private BigDecimal amount;
    @Column(name = "balance_before")
    private BigDecimal balanceBefore;

    @Column(name = "balance_after")
    private BigDecimal balanceAfter;

    @Column(name = "reference_id")
    private String referenceId;

    protected WalletTransaction() {}
    public WalletTransaction(
            Wallet wallet,
            WalletTransactionType type,
            BigDecimal amount,
            BigDecimal balanceBefore,
            BigDecimal balanceAfter,
            String referenceId
    ){
        validate(wallet, type, amount, balanceBefore, balanceAfter);
        this.wallet = wallet;
        this.type = type;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.referenceId = referenceId;
    }
    private void validate(
            Wallet wallet,
            WalletTransactionType type,
            BigDecimal amount,
            BigDecimal before,
            BigDecimal after
    ) {
        if (wallet == null) throw new IllegalArgumentException("Wallet is required");
        if (type == null) throw new IllegalArgumentException("Transaction type is required");
        if (amount == null || amount.signum() <= 0)
            throw new IllegalArgumentException("Amount must be positive");
        if (before == null || after == null)
            throw new IllegalArgumentException("Balances must not be null");
    }
}
