package morago.utils;

import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.time.Instant;

public class CookieUtil {
    private CookieUtil() {}
    public static final String REFRESH_TOKEN = "refresh_token";

    public static ResponseCookie refreshCookie(
            String token, Instant expiresAt, String path, boolean secure, String sameSite
    ){
        long maxAge = Math.max(0, expiresAt.getEpochSecond() - Instant.now().getEpochSecond());
        return ResponseCookie.from(REFRESH_TOKEN, token)
                .httpOnly(true)
                .secure(secure)
                .path(path != null ? path: "/")
                .maxAge(Duration.ofSeconds(maxAge))
                .sameSite(sameSite)
                .build();
    }

    public static ResponseCookie deleteRefreshCookie(String path, boolean secure, String sameSite){
        return ResponseCookie.from(REFRESH_TOKEN, "")
                .httpOnly(true)
                .secure(secure)
                .path(path != null ? path: "/")
                .sameSite(sameSite)
                .build();
    }
}
