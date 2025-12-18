package morago.customExceptions.token;

public class ExpiredJwtTokenException extends RuntimeException {
    public ExpiredJwtTokenException() {
        super("Expired JWT Token");
    }
}
