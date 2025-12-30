package morago.dto.password.request;

public record PasswordResetConfirmRequest(String token, String newPassword, String newConfirmPassword) {}
