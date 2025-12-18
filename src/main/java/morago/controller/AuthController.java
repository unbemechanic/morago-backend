package morago.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import morago.dto.request.LoginRequest;
import morago.dto.request.RegisterRequest;
import morago.dto.response.AuthResponse;
import morago.jwt.AuthenticationTokens;
import morago.model.User;
import morago.security.CustomUserDetails;
import morago.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<RegisterRequest> register(@Valid @RequestBody RegisterRequest request) {
        userService.createNewUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletResponse httpServletResponse) {
        AuthenticationTokens authTokens = userService.verify(request);

        AuthResponse res = AuthResponse.builder()
                .accessToken(authTokens.getAccessToken()).user(authTokens.getUser())
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE).body(res);
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
