package morago.dto.authorization.response;

import lombok.*;
import morago.model.User;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private User user;
}
