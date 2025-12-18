package morago.customExceptions;

public class PhoneNumberAlreadyExistsException extends RuntimeException {
    public PhoneNumberAlreadyExistsException() {
        super("Phone number already exists");
    }
}
