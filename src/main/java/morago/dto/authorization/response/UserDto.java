package morago.dto.authorization.response;

import lombok.*;
import morago.enums.RoleEnum;
import morago.model.Role;
import morago.model.User;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String accessToken;
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Set<String> roles;

    public static UserDto from(User user) {
        Set<String> roleEnums = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .roles(roleEnums)
                .build();
    }
}
