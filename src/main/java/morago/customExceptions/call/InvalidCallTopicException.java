package morago.customExceptions.call;

public class InvalidCallTopicException extends RuntimeException {
    public InvalidCallTopicException() {
        super("Invalid call topic assigment");
    }
}
