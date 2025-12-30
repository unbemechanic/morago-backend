package morago.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morago.customExceptions.UserNotFoundException;
import morago.customExceptions.password.InvalidResetCodeException;
import morago.customExceptions.password.MissingResetFieldsException;
import morago.customExceptions.password.ResetPasswordTokenMissingException;
import morago.model.User;
import morago.model.password.PasswordReset;
import morago.repository.PasswordResetRepository;
import morago.repository.UserRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    private final PasswordResetRepository passwordResetRepository;
    private final UserService userService;
    private final Environment env;
    private final UserRepository userRepository;


    private static final Integer VERIFICATION_CODE = 1234;
    private static final Duration EXPIRES_TIME = Duration.ofMinutes(5);
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String ADMIN_PHONE_NUMBER = "01082547679";
    private Integer generateOtp(){
        return 1000 + RANDOM.nextInt(9000);
    };

    private boolean envProfileDev() {
        return Arrays.stream(env.getActiveProfiles())
                .anyMatch(p -> p.equalsIgnoreCase("dev") || p.equalsIgnoreCase("local"));
    }

    @Transactional
    public void startReset(String phone){
        final User user = userService.findByUsernameOrThrow(phone);
        log.info("Starting password reset for {}", phone);


        passwordResetRepository.invalidateAllActiveByUser(user, LocalDateTime.now());

        int code = envProfileDev() ? VERIFICATION_CODE : generateOtp();

        PasswordReset pr = PasswordReset.builder()
                .user(user)
                .resetCode(code)
                .expiresAt(LocalDateTime.now().plus(EXPIRES_TIME))
                .used(false)
                .codeVerified(false)
                .build();

        log.info("Password reset for {}", phone);
        passwordResetRepository.save(pr);
    }

    @Transactional
    public String verifyCode(String phoneNumber, Integer code){
        if (!userRepository.existsByPhoneNumber(phoneNumber)){
            log.warn("Phone number not found for {}", phoneNumber);
            throw new UserNotFoundException();
        }

        boolean isDev = envProfileDev();
        LocalDateTime now = LocalDateTime.now();

        PasswordReset passwordReset = passwordResetRepository
                .findByUser_PhoneNumberAndResetCodeAndUsedFalseAndCodeVerifiedFalseAndExpiresAtAfter(phoneNumber,code, now)
                .orElseThrow(InvalidResetCodeException::new);

        if(!isDev && !code.equals(passwordReset.getResetCode())){
            log.warn("Reset code is invalid");
            throw new InvalidResetCodeException();
        }

        passwordReset.setCodeVerified(true);
        passwordReset.setVerifiedAt(now);
        passwordReset.setResetCode(null);

        String resetToken = UUID.randomUUID().toString().replace("-", "");
        passwordReset.setToken(resetToken);

        passwordResetRepository.save(passwordReset);

        return resetToken;
    }

    @Transactional
    public void confirmReset(String token, String newPassword, String newConfirmPassword){
        if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()){
            log.warn("Invalid token or new password or new confirm password");
            throw new MissingResetFieldsException();
        }

        if (!passwordResetRepository.existsByToken(token)){
            log.warn("Password Reset Token not found");
            throw new ResetPasswordTokenMissingException();
        }
        final LocalDateTime now = LocalDateTime.now();

        PasswordReset passwordReset = passwordResetRepository.findByToken(token)
                .orElseThrow(InvalidResetCodeException::new);

        if (Boolean.TRUE.equals(passwordReset.getUsed())
        || passwordReset.getExpiresAt().isBefore(now)
                || !Boolean.TRUE.equals(passwordReset.getCodeVerified())) {
            log.warn("Password Reset Token has expired");
            throw new InvalidResetCodeException();
        }

        User user = passwordReset.getUser();

        userService.setPasswordResetedPassword(user.getId(), newPassword, newConfirmPassword);

        passwordReset.setUsed(true);
        passwordReset.setToken(null);

        log.info("Password reset for {} completed successfully", newPassword);
        passwordResetRepository.save(passwordReset);
        passwordResetRepository.invalidateAllActiveByUser(user, now);
    }
}
