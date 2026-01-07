package morago.dto.admin.client;

import lombok.*;
import morago.model.CallTopic;
import morago.model.interpreter.InterpreterProfile;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterpreterResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Boolean isVerified;
    private Boolean isActive;
    private BigDecimal hourlyRate;

    public static InterpreterResponse from(InterpreterProfile profile) {
        return InterpreterResponse.builder()
                .id(profile.getId())
                .firstName(profile.getUser().getFirstName())
                .lastName(profile.getUser().getLastName())
                .phoneNumber(profile.getUser().getPhoneNumber())
                .isVerified(profile.getUser().getIsVerified())
                .isActive(profile.getIsActive())
                .hourlyRate(profile.getHourlyRate())
                .build();
    }
}
