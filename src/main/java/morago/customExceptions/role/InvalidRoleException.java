package morago.customExceptions.role;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String roleName) {
        super("Invalid role: " +  roleName + " Allowed roles: INTERPRETER, CLIENT");
    }
}
