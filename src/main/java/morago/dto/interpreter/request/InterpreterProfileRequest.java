package morago.dto.interpreter.request;


import lombok.*;
import morago.enums.TopikLevel;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterpreterProfileRequest {
    private String firstName;
    private String lastName;

    private TopikLevel level;
    private BigDecimal hourlyRate;

    private Set<Long> callTopicIds;

    private Set<Long> languageIds;
}
