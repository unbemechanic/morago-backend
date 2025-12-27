package morago.customExceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("User '%s' not found".formatted(username));
    }

    public UserNotFoundException(Long id) {
        super("User with id=%d not found".formatted(id));
    }
    public UserNotFoundException() {
        super("No user found");
    }
}
