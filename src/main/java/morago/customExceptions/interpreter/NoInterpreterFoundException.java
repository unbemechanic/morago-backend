package morago.customExceptions.interpreter;

public class NoInterpreterFoundException extends RuntimeException {
    public NoInterpreterFoundException() {
        super("No interpreter found with id ");
    }
}
