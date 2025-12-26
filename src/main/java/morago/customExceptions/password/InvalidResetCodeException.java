package morago.customExceptions.password;

import morago.customExceptions.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidResetCodeException extends ApiException {
    public InvalidResetCodeException() {
        super(HttpStatus.BAD_REQUEST, "Invalid or expired code", "AUTH_INVALID_RESET_CODE");
    }
}
