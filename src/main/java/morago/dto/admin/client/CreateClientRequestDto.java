package morago.dto.admin.client;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateClientRequestDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
}
