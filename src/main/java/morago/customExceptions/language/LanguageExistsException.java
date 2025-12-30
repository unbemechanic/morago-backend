package morago.customExceptions.language;

public class LanguageExistsException extends RuntimeException {
    public LanguageExistsException() {
        super("Language already exists");
    }
}
