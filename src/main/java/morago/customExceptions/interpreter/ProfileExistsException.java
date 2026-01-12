package morago.customExceptions.interpreter;

public class ProfileExistsException extends RuntimeException {
    public ProfileExistsException(String message) {
        super(message);
    }
}
