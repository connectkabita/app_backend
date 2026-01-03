package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.service.EmailService;
import np.edu.nast.payroll.Payroll.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // ===================================================
    // Business logic methods only (no UserDetailsService)
    // ===================================================

    @Override
    public User create(User user) {
        // Encode password using the configured PasswordEncoder
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
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new RuntimeException("Email not found"));
        String otp = String.valueOf((int)(Math.random() * 900000 + 100000));
        user.setResetToken(otp);
        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    @Override
    public void sendOtpToAllUsers() {
        // Implement as needed
    }

    @Override
    public User setupDefaultAccount(Integer empId) {
        return new User();
    }

}
