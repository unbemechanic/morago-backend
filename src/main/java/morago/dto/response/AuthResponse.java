package morago.dto.response;

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
