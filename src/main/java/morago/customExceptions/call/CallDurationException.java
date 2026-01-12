package morago.customExceptions.call;

public class CallDurationException extends RuntimeException {
    public CallDurationException() {
        super("Invalid call duration");
    }
}
