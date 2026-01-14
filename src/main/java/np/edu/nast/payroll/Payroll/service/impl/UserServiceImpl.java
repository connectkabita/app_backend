package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.service.UserService;
import np.edu.nast.payroll.Payroll.service.EmailService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * Spring Security integration to load user by username.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getRoleName()))
        );
    }

    /**
     * Logic for the "Forgot Password" feature.
     * Generates a 6-digit OTP and sends it via EmailService.
     */
    @Override
    public void initiatePasswordReset(String email) {
        // Find user ignoring case to prevent "Email not found" errors
        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new RuntimeException("Email not found in our records."));

        // Generate a random 6-digit OTP
        String otp = String.valueOf((int)((Math.random() * 900000) + 100000));

        // Save the OTP token to the user record
        user.setResetToken(otp);
        userRepository.save(user);

        // Send the OTP to the user's actual device via Gmail
        emailService.sendOtpEmail(user.getEmail(), otp);

        System.out.println("OTP sent to: " + email + " | Debug OTP: " + otp);
    }

    /**
     * Logic for the "Reset Password" form.
     * Matches the OTP token and saves the new encoded password.
     */
    @Override
    public void resetPassword(String token, String newPassword) {
        // Find user by the 6-digit OTP token
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired OTP code."));

        // Encode the new password before saving it to the database
        user.setPassword(passwordEncoder.encode(newPassword));

        // Clear the token so it cannot be used again
        user.setResetToken(null);
        userRepository.save(user);
    }

    /**
     * Standard User Management Methods
     */
    @Override
    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    @Override
    public void sendOtpToAllUsers() {
        // Logic for mass notifications if required
    }

    @Override
    public User setupDefaultAccount(Integer empId) {
        // Logic to create a default account for a new employee
        return new User();
    }
}