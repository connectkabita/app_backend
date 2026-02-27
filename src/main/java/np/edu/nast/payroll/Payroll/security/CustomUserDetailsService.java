package np.edu.nast.payroll.Payroll.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("DB Sync: Fetching credentials for [{}]", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Authentication Failed: User [{}] not found in database", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        // 1. Validate Account Status
        // If isEnabled is false, Spring Security will throw a DisabledException
        if (!user.isEnabled()) {
            log.warn("Security Alert: Attempted login to disabled account [{}]", username);
        }

        // 2. Strict Role Normalization
        // Bridges the gap between DB strings ("ADMIN") and Security Requirements ("ROLE_ADMIN")
        String authorityName = "ROLE_USER"; // Secure default

        if (user.getRole() != null && user.getRole().getRoleName() != null) {
            String rawRole = user.getRole().getRoleName().trim().toUpperCase();
            authorityName = rawRole.startsWith("ROLE_") ? rawRole : "ROLE_" + rawRole;
        } else {
            log.warn("Data Integrity Issue: User [{}] has no assigned role. Using ROLE_USER.", username);
        }

        log.info("Identity Verified: [{}] mapped to authority [{}]", username, authorityName);

        // 3. Construct Spring-Compatible UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(authorityName)))
                .disabled(!user.isEnabled()) // Maps to isEnabled()
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }
}