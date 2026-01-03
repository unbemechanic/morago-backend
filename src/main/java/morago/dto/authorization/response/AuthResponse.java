package morago.dto.authorization.response;

import lombok.*;
import morago.enums.RoleEnum;
import morago.model.Role;
import morago.model.User;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private User user;

}
