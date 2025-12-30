package morago.customExceptions.password;

public class WeakPasswordException extends RuntimeException {
    public WeakPasswordException() {
        super("Password must be at least 6 characters long and contain at least "
                + "one uppercase letter, one lowercase letter, one number, and one "
                + "special character (@$!%*?&).");
    }
}
