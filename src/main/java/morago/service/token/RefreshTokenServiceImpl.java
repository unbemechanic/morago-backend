package morago.service.token;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import morago.enums.TokenEnum;
import morago.jwt.JWTService;
import morago.jwt.JwtProperties;
import morago.jwt.RotatedTokens;
import morago.model.User;
import morago.model.token.RefreshToken;
import morago.repository.UserRepository;
import morago.repository.token.RefreshTokenRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTService jwtService;

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

    @Override
    public void createRefreshToken(String username, String refreshToken) {
        User user = userRepository.getByPhoneNumber(username).orElseThrow(() -> new UsernameNotFoundException(username));
        RefreshToken token = RefreshToken.builder()
                .refreshToken(refreshToken)
                .user(user)
                .expirationTime(LocalDateTime.now().plus(Duration.ofMillis(jwtProperties.getRefreshTokenExpireTime())))
                .build();
        refreshTokenRepository.save(token);
    }

    @Override
    public Optional<RefreshToken> findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken);
    }

    @Override
    public void deleteByRefreshToken(String token) {
        refreshTokenRepository.findByRefreshToken(token).ifPresent(refreshTokenRepository::delete);
    }

    @Override
    public boolean isRefreshTokenExpired(RefreshToken rt) {
        return rt.getExpirationTime().isBefore(LocalDateTime.now());
    }

    @Override
    public RotatedTokens refreshTokens(String rt) {
        RefreshToken refreshToken = findValidTokenOrThrow(rt);

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        deleteByRefreshToken(rt);
        createRefreshToken((user).getPhoneNumber(), newRefreshToken);

        Instant refreshExp = jwtService.getExpInstant(newRefreshToken, TokenEnum.REFRESH);
        return new RotatedTokens(newAccessToken, newRefreshToken, refreshExp);
    }

    @Override
    public void logout(String username, String refreshTokenNullable) {
        User user = userRepository.getByPhoneNumber(username).orElseThrow(() -> new UsernameNotFoundException(username));

        long beforeLogout = refreshTokenRepository.countByUser(user);
        log.info("logout: delete ALL tokens for user={}, before={}", username, beforeLogout);

        if(refreshTokenNullable == null || refreshTokenNullable.isBlank()){
            refreshTokenRepository.deleteByUserId(user.getId());
        } else {
            String token = refreshTokenNullable.trim();

            boolean  deteleOne = refreshTokenRepository.findByRefreshToken(token)
                    .map(dt -> {
                        refreshTokenRepository.delete(dt);
                        return true;
                    })
                    .orElse(false);
        }

        long after = refreshTokenRepository.countByUser(user);
        log.info("logout: delete ALL tokens for user={}, before={}", username, after);
    }
}
