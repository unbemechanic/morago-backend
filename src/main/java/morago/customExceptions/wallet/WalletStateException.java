package morago.customExceptions.wallet;

public class WalletStateException extends RuntimeException {
    public WalletStateException(String message) {
        super(message);
    }
}
