package morago.dto.admin.client;

import lombok.*;
import morago.model.client.ClientProfile;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientProfileDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private BigDecimal balance;

    public static ClientProfileDto from(ClientProfile profile) {
        return ClientProfileDto.builder()
                .id(profile.getId())
                .firstName(profile.getUser().getFirstName())
                .lastName(profile.getUser().getLastName())
                .phoneNumber(profile.getUser().getPhoneNumber())
                .balance(profile.getUser().getWallet().getBalance())
                .build();
    }
}
