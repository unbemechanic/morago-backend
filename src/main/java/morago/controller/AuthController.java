package morago.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import morago.customExceptions.token.ExpiredJwtTokenException;
import morago.customExceptions.token.RefreshTokenNotFoundException;
import morago.dto.authorization.request.LoginRequest;
import morago.dto.authorization.request.RegisterRequest;
import morago.dto.authorization.response.AuthResponse;
import morago.dto.authorization.response.token.AccessTokenResponse;
import morago.jwt.AuthenticationTokens;
import morago.jwt.RotatedTokens;
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

@Slf4j
@Tag(name = "Authentication", description = "Register, login, refresh, logout")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @Operation(
            summary = "Register user",
            description = "Registers a new user with role ROLE_USER." +
                    "Business rules:\n" +
                    "        - ADMIN role cannot be assigned during registration\n" +
                    "        - Phone number must be unique\n" +
                    "        - Password must meet security requirements",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created",
                            content = @Content(schema = @Schema(implementation = RegisterRequest.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "403", description = "ADMIN role can not be assigned"),
                    @ApiResponse(responseCode = "409", description = "Phone already registered")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<RegisterRequest> register(@Valid @RequestBody RegisterRequest request) {
        log.info("REGISTER attempt by phoneNumber={}", request.getPhoneNumber());
        userService.createNewUser(request);
        log.info("REGISTER completed successfully phoneNumber={}", request.getPhoneNumber());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Login user with phone number and password",
            description = "Authenticates the user and returns an access token and user data in the body, Refresh token is set as an HttpOnly cookei",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "Example",
                                    value = """
                                            {
                                                "username":""01098329832",
                                                "password":"Password"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful authentication",
                            headers = @Header(
                                    name = "Set-Cookie",
                                    description = "HttpOnly refresh token cookei"
                            ),
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Bad credentials, invalid username or password, please try again"
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Credentials must not be empty"
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("LOGIN attempt by phoneNumber={}", request.getPhoneNumber());
        AuthenticationTokens authTokens = userService.verify(request);

        boolean secure = false;
        String sameSite = "Lax";
        String path = "/auth";

        log.info("LOGIN completed successfully phoneNumber={}", request.getPhoneNumber());

        ResponseCookie cookie = CookieUtil.refreshCookie(
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

    @Operation(
            summary = "Refresh access token",
            description = "Reads the refresh token from an HttpOnly cookie, rotates it, and returns a new access token in the body.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "New access token generated",
                            headers = @Header(
                                    name = "Set-Cookie",
                                    description = "Rotated HttpOnly refresh token cookie"
                            ),
                            content = @Content(schema = @Schema(implementation = AccessTokenResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "Missing or invalid refresh token cookie"),

            }
    )
    @PostMapping("/token/refresh")
    public ResponseEntity<AccessTokenResponse> refreshToken(
            @CookieValue(name = CookieUtil.REFRESH_TOKEN, required = false)
            String refreshToken) {
        if (refreshToken == null ||  refreshToken.isBlank()) {
            log.warn("REFRESH_TOKEN cookie is empty");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        RotatedTokens rotatedTokens;
        try{
            rotatedTokens = refreshTokenService.refreshTokens(refreshToken);
            log.info("REFRESH_TOKEN rotated successfully");
        }catch (RefreshTokenNotFoundException | ExpiredJwtTokenException e){
            log.warn("REFRESH_TOKEN rejected: {}", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ResponseCookie cookie = CookieUtil.refreshCookie(
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

    @Operation(
            summary = "Log out",
            description = "Invalidates refresh tokens and deletes the refresh cookie. Requires a valid Bearer token.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Logged out (no content)"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(
            Authentication authentication,
            @CookieValue(name = CookieUtil.REFRESH_TOKEN, required = false ) String refreshToken)
    {
        String username = authentication.getName();
        log.info("Logout requested by phoneNumber={}", username);
        refreshTokenService.logout(username, refreshToken);

        log.info("Logout completed successfully");

        ResponseCookie delete = CookieUtil.deleteRefreshCookie(
                "/auth",
                true,
                "Lax"
        );

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, delete.toString())
                .build();
    }
}
