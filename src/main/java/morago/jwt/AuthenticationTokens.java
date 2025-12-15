package morago.jwt;

import lombok.Builder;
import lombok.Getter;
import morago.dto.request.RegisterRequest;
import morago.model.User;


@Getter
@Builder
public class AuthenticationTokens {
    private final String accessToken;
    private final User user;
}
