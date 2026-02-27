package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.dto.auth.LoginRequestDTO;
import np.edu.nast.payroll.Payroll.dto.auth.LoginResponseDTO;
import np.edu.nast.payroll.Payroll.dto.auth.SignupRequestDTO;
import np.edu.nast.payroll.Payroll.service.AuthService;
import np.edu.nast.payroll.Payroll.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    public AuthController(AuthService authService, EmailService emailService) {
        this.authService = authService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.authenticateUser(request));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDTO request) {
        try {
            authService.registerUser(request);
            return ResponseEntity.ok(Map.of("success", true, "message", "User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email required"));
        }

        try {
            // Generating an OTP for communication only
            String otp = String.format("%06d", new Random().nextInt(1000000));
            emailService.sendOtpEmail(email, otp);

            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "OTP sent to " + email + ". No reset record created in DB."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}