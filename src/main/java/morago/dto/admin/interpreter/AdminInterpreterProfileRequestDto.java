package morago.dto.admin.interpreter;

import lombok.*;
import morago.enums.TopikLevel;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminInterpreterProfileRequestDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;

    private TopikLevel level;
    private BigDecimal hourlyRate;

    private Set<Long> callTopicIds;

    private Set<Long> languageIds;
}
