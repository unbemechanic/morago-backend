package morago.utils;

public final class PasswordValidator {
    private PasswordValidator() {}
    public static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";

    public static boolean isValid(String password) {
        return password != null && password.matches(PASSWORD_REGEX);
    }
}
