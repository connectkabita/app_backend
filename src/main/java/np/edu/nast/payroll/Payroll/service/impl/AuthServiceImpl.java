package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.dto.auth.LoginRequestDTO;
import np.edu.nast.payroll.Payroll.dto.auth.LoginResponseDTO;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.service.AuthService;
import np.edu.nast.payroll.Payroll.security.JwtUtils; // New import
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils; // New dependency

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public LoginResponseDTO authenticateUser(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String roleName = "";
        long roleId = user.getRole().getRoleId();

        if (roleId == 6) roleName = "ROLE_ADMIN";
        else if (roleId == 7) roleName = "ROLE_ACCOUNTANT";
        else if (roleId == 8) roleName = "ROLE_EMPLOYEE";
        else roleName = "ROLE_USER";

        // Generate Token
        String token = jwtUtils.generateToken(user.getUsername(), roleName);

        return new LoginResponseDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                roleName,
                token // Pass the token to DTO
        );
    }
}