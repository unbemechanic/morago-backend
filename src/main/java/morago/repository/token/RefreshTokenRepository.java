package morago.repository.token;

import morago.model.User;
import morago.model.token.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    @Modifying
    @Query("delete from RefreshToken rt where rt.user = :user")
    void deleteByUserId(@Param("userId") Long userId);

    void deleteByUserAndRefreshToken(User user, String refreshToken);
    long countByUser(User user);
}
