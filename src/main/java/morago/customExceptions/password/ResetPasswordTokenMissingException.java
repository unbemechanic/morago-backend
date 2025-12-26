package morago.customExceptions.password;

public class ResetPasswordTokenMissingException extends RuntimeException {
    public ResetPasswordTokenMissingException() {
        super("Wrong Reset Password Token");
    }
}
