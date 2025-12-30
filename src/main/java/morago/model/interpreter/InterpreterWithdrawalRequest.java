package morago.model.interpreter;

import jakarta.persistence.*;
import lombok.*;
import morago.enums.WithdrawalStatus;
import morago.monitor.Audit;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interpreter_withdrawal_requests")
public class InterpreterWithdrawalRequest extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "interpreter_profile_id")
    private InterpreterProfile interpreterProfile;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private WithdrawalStatus status;
}
