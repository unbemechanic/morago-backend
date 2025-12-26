package morago.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import morago.customExceptions.PhoneNumberAlreadyExistsException;
import morago.customExceptions.UserNotFoundException;
import morago.customExceptions.password.PasswordMismatchException;
import morago.customExceptions.password.PasswordRequiredException;
import morago.customExceptions.role.InvalidRoleAssigment;
import morago.customExceptions.role.InvalidRoleException;
import morago.dto.authorization.request.LoginRequest;
import morago.dto.authorization.request.RegisterRequest;
import morago.enums.RoleEnum;
import morago.enums.TokenEnum;
import morago.jwt.AuthenticationTokens;
import morago.jwt.JWTService;
import morago.model.Role;
import morago.model.User;
import morago.repository.RoleRepository;
import morago.repository.UserRepository;
import morago.repository.token.RefreshTokenRepository;
import morago.security.CustomUserDetails;
import morago.service.token.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final RefreshTokenRepository refreshTokenRepository;

    public User findByUsernameOrThrow(String username) {
        return userRepository.getByPhoneNumber(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public void createNewUser(RegisterRequest requestUser) {
        if(userRepository.existsByPhoneNumber(requestUser.getPhoneNumber())){
            throw new PhoneNumberAlreadyExistsException();
        }
        RoleEnum role = requestUser.getRoles();

        if(role == RoleEnum.ADMIN){
            throw new InvalidRoleAssigment(role.name());
        }

        if (role != RoleEnum.CLIENT && role != RoleEnum.INTERPRETER) {
            throw new InvalidRoleAssigment(role.name());
        }

        Role entityRole = roleRepository.findRoleByName(role.name())
                .orElseThrow(() -> new InvalidRoleException(role.name()));
        User user = new User();
        user.setFirstName(requestUser.getFirstName());
        user.setLastName(requestUser.getLastName());
        user.setPhoneNumber(requestUser.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(requestUser.getPassword()));
        user.getRoles().add(entityRole);
        user.setIsVerified(false);
        userRepository.save(user);
    }
    public AuthenticationTokens verify( LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getPhoneNumber(),
                                loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails authenticated = (CustomUserDetails) authentication.getPrincipal();
        User user = authenticated.getUser();

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("Tokens generated successfully");
        authentication.getAuthorities().forEach(a ->
                log.warn("LOGIN AUTHORITY: {}", a.getAuthority())
        );

        refreshTokenService.createRefreshToken(authenticated.getUsername(), refreshToken);

        log.info("Refresh token saved");

        User authUser = findByUsernameOrThrow(authenticated.getUsername());

        Instant refreshExp = jwtService.getExpInstant(refreshToken, TokenEnum.REFRESH);

        log.info("Refresh token expiration extracted");

       return AuthenticationTokens.builder()
               .accessToken(accessToken)
               .refreshToken(refreshToken)
               .refreshExpAt(refreshExp)
               .user(authUser)
               .build();
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    // Password reset

    @Transactional
    public void setPasswordResetedPassword(Long userId, String newPassword, String confirmNewPassword) {
        User user = findByIdOrThrow(userId);
        applyNewPassword(user, newPassword, confirmNewPassword);
    }

    private void applyNewPassword(User user, String newPassword, String confirmNewPassword) {
        validPassword(newPassword, confirmNewPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        refreshTokenRepository.deleteByUser(user);
    }

    private void validPassword(String newPassword, String confirmNewPassword) {
        if (newPassword == null || newPassword.isEmpty() || confirmNewPassword == null || confirmNewPassword.isEmpty()) {
            throw new PasswordRequiredException();
        }
        if (!newPassword.equals(confirmNewPassword)) {
            throw new PasswordMismatchException();
        }
    }

    private User findByIdOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
