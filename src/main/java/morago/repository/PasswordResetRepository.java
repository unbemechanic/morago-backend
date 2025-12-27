package morago.repository;

import morago.model.User;
import morago.model.password.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findByToken(String token);
    boolean existsByToken(String token);

    Optional<PasswordReset> findByTokenAndExpiresAt(String token, LocalDateTime expiresAt);

    Optional<PasswordReset> findByUser_PhoneNumberAndResetCodeAndUsedFalseAndCodeVerifiedFalseAndExpiresAtAfter
            (String phoneNumber, Integer resetCode, LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update PasswordReset pr
              set pr.used = true, pr.expiresAt = :now
            where pr.user = :user
              and pr.used = false
           """)
    int invalidateAllActiveByUser(@Param("user") User user, @Param("now") LocalDateTime now);
}
