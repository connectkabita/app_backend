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

    @Override
    public LoginResponseDTO adminLogin(LoginRequestDTO request) {
        // 1. Basic Validation
        if (request.getUsernameOrEmail() == null || request.getUsernameOrEmail().trim().isEmpty()) {
            throw new InvalidCredentialsException("Username or email must not be empty");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new InvalidCredentialsException("Password must not be empty");
        }

        // REMOVED: request.getRole() check to stop the frontend error

        String input = request.getUsernameOrEmail().trim();
        String inputPassword = request.getPassword().trim();

        // 2. Find user
        User user = userRepo.findByUsernameOrEmail(input, input)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or email"));

        // 3. Compare password (Ensure you trim both to avoid hidden space issues)
        if (!user.getPassword().trim().equals(inputPassword)) {
            throw new InvalidCredentialsException("Invalid password");
        }

        // 4. Role Authorization (Check database role instead of request role)
        if (user.getRole() == null) {
            throw new InvalidCredentialsException("User has no assigned role");
        }

        return new LoginResponseDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getRoleName()
        );
    }
}