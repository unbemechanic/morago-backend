package morago.dto.admin;

import lombok.*;
import morago.dto.admin.interpreter.InterpreterWithdrawalRequestDto;
import morago.model.CallTopic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallResponseDto {
    private Long id;
    private String phoneNumber;
    private LocalDateTime startedAt;
    private BigInteger duration;

    private BigDecimal totalPrice;
    private CallTopic callTopic;

    private InterpreterWithdrawalRequestDto interpreterWithdrawalRequestDto;
}
