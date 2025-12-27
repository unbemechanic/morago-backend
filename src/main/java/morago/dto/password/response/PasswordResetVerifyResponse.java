package morago.dto.password.response;

import java.time.Instant;

public class PasswordResetVerifyResponse {
    private String resetToken;
    private Instant expiresAt;
}
