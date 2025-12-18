package morago.service;

import lombok.RequiredArgsConstructor;
import morago.customExceptions.PhoneNumberAlreadyExistsException;
import morago.customExceptions.UserNotFoundException;
import morago.customExceptions.role.InvalidRoleAssigment;
import morago.customExceptions.role.InvalidRoleException;
import morago.dto.request.LoginRequest;
import morago.dto.request.RegisterRequest;
import morago.enums.RoleEnum;
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
            throw new PhoneNumberAlreadyExistsException();
        }
        RoleEnum role = requestUser.getRoles();

        if(role == RoleEnum.ADMIN){
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
