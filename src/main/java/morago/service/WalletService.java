package morago.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morago.customExceptions.UserNotFoundException;
import morago.customExceptions.wallet.InsufficientBalanceException;
import morago.customExceptions.wallet.WalletBalanceInsufficient;
import morago.enums.WalletTransactionType;
import morago.model.User;
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
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Wallet wallet = getOrCreateWallet(user);
        BigDecimal before = wallet.getBalance();

        validateAmount(amount);
        wallet.credit(amount);
        walletRepository.save(wallet);
        walletTransactionRepository.save(new WalletTransaction(
                wallet,
                WalletTransactionType.TOP_UP,
                amount,
                before,
                wallet.getBalance(),
                ref
        ));

        log.info("Top Up Wallet Transaction Successfully for userId={} amount={}", userId, amount);
    }

    @Transactional
    public void charge(User client, User interpreter, BigDecimal amount, String ref){
        Wallet clientWallet = getOrCreateWallet(client);
        Wallet interpreterWallet = getOrCreateWallet(interpreter);

        BigDecimal before = clientWallet.getBalance();
        BigDecimal beforeBalanceInt = interpreterWallet.getBalance();

        if (before.compareTo(amount) < 0) {
            log.warn("Insufficient balance client={} amount={}", client.getPhoneNumber(), amount);
            throw new InsufficientBalanceException();
        }

        clientWallet.debit(amount);
        interpreterWallet.credit(amount);


        //Client wallet transaction
        walletTransactionRepository.save(new WalletTransaction(
                clientWallet,
                WalletTransactionType.CHARGE,
                amount,
                before,
                clientWallet.getBalance(),
                ref
        ));

        walletTransactionRepository.save(new WalletTransaction(
                interpreterWallet,
                WalletTransactionType.EARNING,
                amount,
                beforeBalanceInt,
                interpreterWallet.getBalance(),
                ref
        ));
    }
    private Wallet getOrCreateWallet(User user) {

        return walletRepository.findByUser(user)
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setUser(user);
                    user.setWallet(wallet);
                    return walletRepository.save(wallet);
                });
    }

    private void validateAmount(BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new WalletBalanceInsufficient();
        }
    }
}
