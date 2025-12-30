package morago.customExceptions.call;

import morago.enums.CallState;

public class InvalidCallStateException extends RuntimeException {
    public InvalidCallStateException(CallState from, CallState to) {
        super(
                "Invalid transition from " + from + " to " + to
        );
    }
}
