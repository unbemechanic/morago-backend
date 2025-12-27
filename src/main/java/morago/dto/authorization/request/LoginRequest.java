package morago.dto.authorization.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    @NotBlank(message = "Phone number must not be empty")
    private String phoneNumber;

    @NotBlank(message = "Password must not be empty")
    private String password;
}
