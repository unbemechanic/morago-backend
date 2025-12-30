package morago.dto.admin;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private BigDecimal balance;

    private List<DepositRequestResponseDto> depositRequests;
    private List<CallResponseDto> calls;
    private List<DepositResponseDto> deposits;

}
