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
public class SingleInterpreterProfileDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;

    private TopikLevel level;
    private BigDecimal hourlyRate;

    private Set<Long> callTopicIds;

    private Set<Long> languageIds;
    private Boolean isVerified;
}
