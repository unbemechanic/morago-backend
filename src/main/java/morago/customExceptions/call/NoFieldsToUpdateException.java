package morago.customExceptions.call;

public class NoFieldsToUpdateException extends RuntimeException {
    public NoFieldsToUpdateException() {
        super("No fields provided to update");
    }
}
