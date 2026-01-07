package morago.customExceptions.call;

public class CallNotFoundException extends RuntimeException {
    public CallNotFoundException() {
        super("Call not found");
    }
}
