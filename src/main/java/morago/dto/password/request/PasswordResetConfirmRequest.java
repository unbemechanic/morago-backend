package morago.dto.password.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetConfirmRequest(String token, String newPassword, String newConfirmPassword) {}
