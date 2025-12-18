package morago.customExceptions.role;

public class InvalidRoleAssigment extends RuntimeException {
    public InvalidRoleAssigment(String roleName) {
        super(roleName + " can not be assigned");
    }

}
