package morago.customExceptions.language;

public class NoLanguageFoundException extends RuntimeException {
    public NoLanguageFoundException() {
        super("No language found");
    }
    public NoLanguageFoundException(Long id) {
        super("No language found with id " + id);
    }
}
