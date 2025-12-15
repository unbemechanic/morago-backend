package morago.model.interpreter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import morago.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "withdrawals")
public class Withdrawal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinTable(name = "interpreterProfileId")
    private InterpreterProfile interpreterProfile;

    private BigDecimal requestedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @NotBlank
    private String withdrawalAccount;

    @NotBlank
    private String withdrawalDetails;

    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
}
