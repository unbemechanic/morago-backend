package morago.dto.admin;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DepositResponseDto {
    private Long id;
    private BigDecimal amount;
    private String method;
    private LocalDateTime depositedAt;
}
