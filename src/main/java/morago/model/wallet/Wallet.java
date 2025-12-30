package morago.model.wallet;

import jakarta.persistence.*;
import morago.customExceptions.wallet.InsufficientBalanceException;
import morago.monitor.Audit;

import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
public class Wallet extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Version
    private Long version;

    protected Wallet() {}
    public Wallet(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {return userId;}
    public BigDecimal getBalance() {return balance;}

    public void credit(BigDecimal amount){
        validateAmount(amount);
        balance = balance.add(amount);
    }

    public void debit(BigDecimal amount){
        validateAmount(amount);
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        balance = balance.subtract(amount);
    }

    private void validateAmount(BigDecimal amount){
        if(amount == null || amount.signum() <= 0){
            throw new InsufficientBalanceException();
        }
    }
}
