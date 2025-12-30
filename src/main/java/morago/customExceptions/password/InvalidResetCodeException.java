package morago.customExceptions.password;

import morago.customExceptions.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidResetCodeException extends ApiException {
    public InvalidResetCodeException() {
        super(HttpStatus.BAD_REQUEST, "AUTH_INVALID_RESET_CODE", "Invalid or expired code");
    }
}
