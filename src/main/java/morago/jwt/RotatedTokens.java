package morago.jwt;

import java.time.Instant;

public record RotatedTokens(String newAccessToken, String newRefreshToken, Instant expirationTime) {
}
