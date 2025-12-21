package morago.customExceptions.token;

public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException() {
        super(
                "Refresh token not found. Please try again later."
        );
    }
}
