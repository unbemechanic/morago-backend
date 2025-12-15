package morago.service;

import morago.model.User;
import morago.repository.UserRepository;
import morago.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getByPhoneNumber(username).orElseThrow(
                ()-> new UsernameNotFoundException("User not found with phone number")
        );
        return new CustomUserDetails(user);
    }
}
