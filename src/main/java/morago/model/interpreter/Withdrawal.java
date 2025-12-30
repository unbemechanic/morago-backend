package morago.model.interpreter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import morago.enums.WithdrawalStatus;
import morago.monitor.Audit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "withdrawals")
public class Withdrawal extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "interpreter_profile_id")
    private InterpreterProfile interpreterProfile;

    @Column(name = "requested_amount")
    private BigDecimal requestedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WithdrawalStatus status;

    @NotBlank
    @Column(name = "withdrawal_account")
    private String withdrawalAccount;

    @NotBlank
    @Column(name = "withdrawal_details")
    private String withdrawalDetails;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}
