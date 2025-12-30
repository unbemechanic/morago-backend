package morago.customExceptions.wallet;

public class WalletBalanceInsufficient extends RuntimeException {
    public WalletBalanceInsufficient() {
        super("Amount must be positive");
    }
}
