package morago.dto.admin.interpreter;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterpreterWithdrawalRequestDto {
    private BigDecimal requestedAmount;
    private String withdrawalAccount;
    private LocalDateTime requestedAt;
    private String withdrawalDetails;
}
