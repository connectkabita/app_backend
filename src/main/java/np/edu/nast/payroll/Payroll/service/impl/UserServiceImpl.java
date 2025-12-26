package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.*;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.service.UserService;
import np.edu.nast.payroll.Payroll.service.EmailService; // Ensure this is imported
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final EmployeeRepository employeeRepo;
    private final EmailService emailService; // Inject EmailService

    public UserServiceImpl(UserRepository userRepo, RoleRepository roleRepo,
                           EmployeeRepository employeeRepo, EmailService emailService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.employeeRepo = employeeRepo;
        this.emailService = emailService;
    }

    private String generateSimpleToken() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * Sends a new 6-digit OTP to every user currently in the database.
     */
    public void sendOtpToAllUsers() {
        List<User> allUsers = userRepo.findAll();
        for (User user : allUsers) {
            String otp = generateSimpleToken();
            user.setResetToken(otp);
            user.setTokenExpiry(LocalDateTime.now().plusHours(24));
            userRepo.save(user);

            // This sends the OTP to each user's specific email in the table
            emailService.sendOtpEmail(user.getEmail(), otp);
        }
    }

    @Override
    public User setupDefaultAccount(Integer empId) {
        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (employee.getUser() != null) {
            throw new RuntimeException("User already exists for this employee");
        }

        Role role = roleRepo.findById(2).orElseThrow(() -> new RuntimeException("Default Role not found"));

        User user = new User();
        user.setEmployee(employee);
        user.setEmail(employee.getEmail());
        user.setUsername(employee.getFirstName().toLowerCase() + employee.getEmpId());
        user.setPassword("Default@123");
        user.setRole(role);
        user.setStatus("PENDING");

        String otp = generateSimpleToken();
        user.setResetToken(otp);
        user.setTokenExpiry(LocalDateTime.now().plusHours(24));

        User savedUser = userRepo.save(user);
        emailService.sendOtpEmail(savedUser.getEmail(), otp);

        return savedUser;
    }

    public void verifyOtpAndSetPassword(String email, String otp, String newPassword) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getResetToken() != null &&
                user.getResetToken().equals(otp) &&
                user.getTokenExpiry().isAfter(LocalDateTime.now())) {

            user.setPassword(newPassword);
            user.setStatus("ACTIVE");
            user.setResetToken(null);
            user.setTokenExpiry(null);
            userRepo.save(user);
        } else {
            throw new RuntimeException("Invalid or expired OTP");
        }
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userRepo.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        user.setPassword(newPassword);
        user.setStatus("ACTIVE");
        user.setResetToken(null);
        user.setTokenExpiry(null);
        userRepo.save(user);
    }

    @Override
    public User getByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = generateSimpleToken();
        user.setResetToken(otp);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(30));

        User savedUser = userRepo.save(user);
        emailService.sendOtpEmail(savedUser.getEmail(), otp);

        return savedUser;
    }

    @Override public List<User> getAll() { return userRepo.findAll(); }
    @Override public User create(User user) { return userRepo.save(user); }
    @Override public User getById(Integer id) { return userRepo.findById(id).orElse(null); }
    @Override public User update(Integer id, User user) { return userRepo.save(user); }
    @Override public void delete(Integer id) { userRepo.deleteById(id); }
}