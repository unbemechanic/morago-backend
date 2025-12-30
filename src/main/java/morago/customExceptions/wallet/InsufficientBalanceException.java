package morago.customExceptions.wallet;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Insufficient Wallet Balance");
    }
}
