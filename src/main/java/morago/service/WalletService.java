package morago.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morago.customExceptions.wallet.InsufficientBalanceException;
import morago.customExceptions.wallet.WalletBalanceInsufficient;
import morago.enums.WalletTransactionType;
import morago.model.wallet.Wallet;
import morago.model.wallet.WalletTransaction;
import morago.repository.UserRepository;
import morago.repository.wallet.WalletRepository;
import morago.repository.wallet.WalletTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void topUp(Long userId, BigDecimal amount, String ref){
        Wallet wallet = getOrCreateWallet(userId);
        BigDecimal before = wallet.getBalance();

        validateAmount(amount);
        wallet.credit(amount);
        walletRepository.save(wallet);
        walletTransactionRepository.save(new WalletTransaction(
                wallet, WalletTransactionType.TOP_UP,
                amount,
                before,
                wallet.getBalance(),
                ref
        ));

        userRepository.updateBalance(userId, wallet.getBalance());

        log.info("Top Up Wallet Transaction Successfully for userId={} amount={}", userId, amount);
    }

    @Transactional
    public void charge(Long userId, BigDecimal amount, String ref){
        Wallet wallet = getOrCreateWallet(userId);
        BigDecimal before = wallet.getBalance();

        if (before.compareTo(amount) < 0) {
            log.warn("Insufficient balance userId={} amount={}", userId, amount);
            throw new InsufficientBalanceException();
        }

        wallet.debit(amount);

        walletTransactionRepository.save(new WalletTransaction(
                wallet,
                WalletTransactionType.CHARGE,
                amount,
                before,
                wallet.getBalance(),
                ref
        ));
    }
    private Wallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId).orElseGet(() -> walletRepository.save(new Wallet(userId)));
    }

    private void validateAmount(BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new WalletBalanceInsufficient();
        }
    }
}
