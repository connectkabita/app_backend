package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.dto.auth.LoginRequestDTO;
import np.edu.nast.payroll.Payroll.dto.auth.LoginResponseDTO;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.exception.InvalidCredentialsException;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;

    public AuthServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Login method for Admin, Accountant, and Employee roles
     * @param request LoginRequestDTO containing username/email, password, and expected role
     * @return LoginResponseDTO
     */
    @Override
    public LoginResponseDTO adminLogin(LoginRequestDTO request) {

        // 1. Validate input
        if (request.getUsernameOrEmail() == null || request.getUsernameOrEmail().trim().isEmpty()) {
            throw new InvalidCredentialsException("Username or email must not be empty");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new InvalidCredentialsException("Password must not be empty");
        }
        if (request.getRole() == null || request.getRole().trim().isEmpty()) {
            throw new InvalidCredentialsException("Role must be specified");
        }

        String input = request.getUsernameOrEmail().trim();
        String inputPassword = request.getPassword().trim();
        String expectedRole = request.getRole().trim().toUpperCase();

        // ------------------------------
        // 🔹 DEBUG LOGS
        System.out.println("Login attempt for: " + input + " with role: " + expectedRole);
        // ------------------------------

        // 2. Find user by username or email
        User user = userRepo
                .findByUsernameOrEmail(input, input)
                .orElseThrow(() -> {
                    System.out.println("User not found for input: " + input);
                    return new InvalidCredentialsException("Invalid username or email");
                });

        System.out.println("User found: " + user.getUsername() + ", role: " +
                (user.getRole() != null ? user.getRole().getRoleName() : "null"));

        // 3. Trim DB password and compare
        String dbPassword = user.getPassword() != null ? user.getPassword().trim() : "";

        if (!dbPassword.equals(inputPassword)) {
            System.out.println("Password mismatch: input='" + inputPassword + "' db='" + dbPassword + "'");
            throw new InvalidCredentialsException("Invalid password");
        }

        // 4. Validate role dynamically
        if (user.getRole() == null || !expectedRole.equalsIgnoreCase(user.getRole().getRoleName().trim())) {
            System.out.println("Unauthorized role: " + (user.getRole() != null ? user.getRole().getRoleName() : "null"));
            throw new InvalidCredentialsException("Unauthorized role");
        }

        // 5. Success: return login response
        System.out.println("Login successful for user: " + user.getUsername());

        return new LoginResponseDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getRoleName()
        );
    }

}
