package morago.service;

import lombok.RequiredArgsConstructor;
import morago.customExceptions.UserNotFoundException;
import morago.dto.request.LoginRequest;
import morago.dto.request.RegisterRequest;
import morago.jwt.AuthenticationTokens;
import morago.jwt.JWTService;
import morago.model.Role;
import morago.model.User;
import morago.repository.RoleRepository;
import morago.repository.UserRepository;
import morago.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;

    public User findByUsernameOrThrow(String username) {
        return userRepository.getByPhoneNumber(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public void createNewUser(RegisterRequest requestUser) {
        if(userRepository.existsByPhoneNumber(requestUser.getPhoneNumber())){
            throw new IllegalArgumentException("Phone number already exists");
        }
        String roleName = requestUser.getRoles().name();
        Role role = roleRepository.findRoleByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid role"));
        User user = new User();
        user.setFirstName(requestUser.getFirstName());
        user.setLastName(requestUser.getLastName());
        user.setPhoneNumber(requestUser.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(requestUser.getPassword()));
        user.getRoles().add(role);
        user.setIsVerified(false);
        userRepository.save(user);
    }
    public AuthenticationTokens verify(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getPhoneNumber(),
                                loginRequest.getPassword())
        );
        UserDetails authenticated = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateToken((CustomUserDetails) authenticated);

        User user = findByUsernameOrThrow(authenticated.getUsername());

       return AuthenticationTokens.builder()
               .accessToken(accessToken)
               .user(user)
               .build();
    }
}
