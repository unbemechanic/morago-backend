package morago.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import morago.dto.request.LoginRequest;
import morago.dto.request.RegisterRequest;
import morago.dto.response.AuthResponse;
import morago.dto.response.token.AccessTokenResponse;
import morago.jwt.AuthenticationTokens;
import morago.jwt.RotatedTokens;
import morago.model.User;
import morago.security.CustomUserDetails;
import morago.service.UserService;
import morago.service.token.RefreshTokenService;
import morago.utils.CookieUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }


    @PostMapping("/register")
    public ResponseEntity<RegisterRequest> register(@Valid @RequestBody RegisterRequest request) {
        userService.createNewUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletResponse httpServletResponse) {
        AuthenticationTokens authTokens = userService.verify(request);

        boolean secure = false;
        String sameSite = "Lax";
        String path = "/auth";

        ResponseCookie cookie = CookieUtil.rerfreshCookie(
                authTokens.getRefreshToken(),
                authTokens.getRefreshExpAt(),
                path,
                secure,
                sameSite
        );

        AuthResponse res = AuthResponse.builder()
                .accessToken(authTokens.getAccessToken()).user(authTokens.getUser())
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(res);
    }

    @PostMapping("refresh")
    public ResponseEntity<AccessTokenResponse> refreshToken(@CookieValue(name = CookieUtil.REFRESH_TOKEN) String refreshToken) {
        if (refreshToken == null ||  refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        RotatedTokens rotatedTokens = refreshTokenService.refreshTokens(refreshToken);

        ResponseCookie cookie = CookieUtil.rerfreshCookie(
                rotatedTokens.newRefreshToken(),
                rotatedTokens.expirationTime(),
                "/auth",
                true,
                "Lax"
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AccessTokenResponse(rotatedTokens.newAccessToken()));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(
            Authentication authentication,
            @CookieValue(name = CookieUtil.REFRESH_TOKEN, required = false ) String refreshToken)
    {
        String username = authentication.getName();
        refreshTokenService.logout(username, refreshToken);

        ResponseCookie delete = CookieUtil.deleteRefreshCookie(
                "/auth",
                true,
                "Lax"
        );

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, delete.toString())
                .build();
    }

    @PreAuthorize("hasAnyRole('INTERPRETER', 'CLIENT')")
    @GetMapping("/user")
    public ResponseEntity<?> getUser(Authentication authentication) {
//        String phoneNumber = authentication.getName();
//        UserDetails user = userService.loadUserByUsername(phoneNumber);
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();
        return ResponseEntity.ok(user);
    }
}
