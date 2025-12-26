package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class UserController {

    @Autowired
    private UserService userService;

    // Handles POST to http://localhost:8080/api/users/add
    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        // Warning: Ensure the emp_id in 'user' exists in your employee table
        return userService.create(user);
    }

    // NEW: Trigger OTP sending to ALL users in the user table
    @PostMapping("/send-otp-to-all")
    public ResponseEntity<String> sendOtpToAll() {
        try {
            userService.sendOtpToAllUsers();
            return ResponseEntity.ok("OTPs have been sent to all registered user emails.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/invite/{empId}")
    public String inviteUser(@PathVariable Integer empId) {
        try {
            // Service handles account setup and email sending now
            User user = userService.setupDefaultAccount(empId);
            return "Invitation with OTP sent to " + user.getEmail();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam("email") String email) {
        try {
            // getByEmail now generates a new 6-digit OTP and sends it
            userService.getByEmail(email);
            return "Reset OTP sent to your email.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            // Token here is the 6-digit OTP
            userService.resetPassword(token, newPassword);
            return "Success: Account is now ACTIVE.";
        } catch (Exception e) {
            return "Failed: " + e.getMessage();
        }
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Integer id) {
        userService.delete(id);
        return "User deleted successfully";
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAll();
    }
}