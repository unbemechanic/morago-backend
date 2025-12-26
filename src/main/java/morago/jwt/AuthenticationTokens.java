package morago.jwt;

import lombok.Builder;
import lombok.Getter;
import morago.dto.request.RegisterRequest;
import morago.model.User;

import java.time.Instant;


@Getter
@Builder
public class AuthenticationTokens {
    private final String accessToken;
    private final String refreshToken;
    private final Instant refreshExpAt;
    private final User user;
}
