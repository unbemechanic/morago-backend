package morago.model.wallet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import morago.customExceptions.wallet.InsufficientBalanceException;
import morago.model.User;
import morago.monitor.Audit;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wallets")
public class Wallet extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, precision = 19, scale = 0)
    private BigDecimal balance = BigDecimal.ZERO;

    @Version
    private Long version;


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
