package np.edu.nast.payroll.Payroll.security;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Ensure the role name is valid and prefixed correctly for Spring Security
        String rawRole = user.getRole().getRoleName().toUpperCase();
        String roleWithPrefix = rawRole.startsWith("ROLE_") ? rawRole : "ROLE_" + rawRole;

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix))
        );
    }
}