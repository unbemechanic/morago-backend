package morago.customExceptions.token;

public class InvalidJwtTokenException extends RuntimeException {
    public InvalidJwtTokenException() {
        super("Invalid JWT Token");
    }
}
