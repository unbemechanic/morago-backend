package morago.dto.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import morago.enums.RoleEnum;

import java.time.LocalDate;

@Setter
@Getter
public class RegisterRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleEnum roles;

    private LocalDate createAt;
}
