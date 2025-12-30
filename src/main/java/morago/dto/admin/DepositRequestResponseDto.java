package morago.dto.admin;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositRequestResponseDto {
    private Long id;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
}
