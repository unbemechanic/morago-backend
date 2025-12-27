package morago.dto.password.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordResetVerityRequest(
        @NotBlank String phoneNumber,
        @NotNull Integer code){}
