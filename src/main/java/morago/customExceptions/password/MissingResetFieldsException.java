package morago.customExceptions.password;

import morago.customExceptions.ApiException;
import org.springframework.http.HttpStatus;

public class MissingResetFieldsException extends ApiException {
    public MissingResetFieldsException() {
        super(HttpStatus.BAD_REQUEST, "AUTH_RESET_MISSING_FIELDS", "Token and newPassword are required");
    }
}
