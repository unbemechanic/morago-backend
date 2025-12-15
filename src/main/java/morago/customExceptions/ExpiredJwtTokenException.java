package morago.customExceptions;

public class ExpiredJwtTokenException extends RuntimeException {
    public ExpiredJwtTokenException() {
        super("Expired JWT Token");
    }
}
