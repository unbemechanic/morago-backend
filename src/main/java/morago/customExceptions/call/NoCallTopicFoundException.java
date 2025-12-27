package morago.customExceptions.call;

public class NoCallTopicFoundException extends RuntimeException {
    public NoCallTopicFoundException() {
        super("No call topic found");
    }
}
