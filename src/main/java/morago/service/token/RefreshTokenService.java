package morago.service.token;

import morago.customExceptions.token.ExpiredJwtTokenException;
import morago.customExceptions.token.RefreshTokenNotFoundException;
import morago.jwt.RotatedTokens;
import morago.model.token.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    void createRefreshToken(String username, String refreshToken);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    default RefreshToken findByTokenOrThrow(String token) {
        return findByRefreshToken(token).orElseThrow(RefreshTokenNotFoundException::new);
    }
    default RefreshToken findValidTokenOrThrow(String token) {
        RefreshToken rt = findByTokenOrThrow(token);
        if(isRefreshTokenExpired(rt)){
            deleteByRefreshToken(token);
            throw new ExpiredJwtTokenException();
        }
        return rt;
    }

    void deleteByRefreshToken(String token);

    boolean isRefreshTokenExpired(RefreshToken rt);
    RotatedTokens refreshTokens(String rt);

    void logout(String username, String refreshTokenNullable);
}
