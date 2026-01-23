package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.exception.EmailAlreadyExistsException;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.service.EmailService;
import np.edu.nast.payroll.Payroll.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public User create(User user) {
        // Validation for new user creation
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty for new user creation");
        }

        if (userRepository.findByEmailIgnoreCase(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("A user with the" + user.getEmail() + "email already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User update(Integer id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setStatus(userDetails.getStatus());

        if (userDetails.getRole() != null) {
            existingUser.setRole(userDetails.getRole());
        }

        // Only update password if a new one is provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    @Override
    public List<User> getAll() { return userRepository.findAll(); }

    @Override
    public User getById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public void delete(Integer id) {
        if(!userRepository.existsById(id)) throw new ResourceNotFoundException("User not found");
        userRepository.deleteById(id);
    }

    @Override
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));
        String otp = String.valueOf((int)(Math.random() * 900000 + 100000));
        user.setResetToken(otp);
        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired OTP"));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    @Override
    public void sendOtpToAllUsers() {}

    @Override
    public User setupDefaultAccount(Integer empId) { return new User(); }
}