package morago.dto.admin.interpreter;

import lombok.*;
import morago.dto.admin.CallResponseDto;
import morago.enums.TopikLevel;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminIPResponseDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;

    private TopikLevel level;

    private Boolean isVerified;

    private List<CallResponseDto> calls;

    private List<InterpreterWithdrawalRequestDto> withdrawalRequest;
    private List<WithdrawalDto> withdraw;
}
