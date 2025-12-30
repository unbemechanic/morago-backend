package morago.dto.admin;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleUserResponseDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private BigDecimal balance;
}
