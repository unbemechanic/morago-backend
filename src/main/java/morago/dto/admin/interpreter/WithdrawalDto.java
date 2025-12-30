package morago.dto.admin.interpreter;

import lombok.*;
import morago.enums.WithdrawalStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalDto {
    private String withdrawalAccount;
    private LocalDateTime processedAt;
    private String withdrawalDetails;
    private WithdrawalStatus status;
}
