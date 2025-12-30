package morago.customExceptions.language;

public class InvalidLanguageException extends RuntimeException {
    public InvalidLanguageException() {
        super("Invalid languages assigned");
    }
}
