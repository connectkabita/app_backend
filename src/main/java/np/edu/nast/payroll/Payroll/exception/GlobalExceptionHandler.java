package np.edu.nast.payroll.Payroll.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ==========================================
       AUTH FAILURE (Bad Credentials, Locked, etc)
       ========================================== */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(
            AuthenticationException ex) {

        Map<String, String> response = new HashMap<>();
        // This will capture "Bad credentials" or custom messages from AuthServiceImpl
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401 Unauthorized
                .body(response);
    }

    /* ============================
       EMAIL ALREADY EXISTS
       ============================ */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex) {

        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409
                .body(response);
    }

    /* ============================
       ILLEGAL ARGUMENT / VALIDATION
       ============================ */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(
            IllegalArgumentException ex) {

        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(response);
    }

    /* ============================
       GENERIC RUNTIME ERRORS
       ============================ */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(
            RuntimeException ex) {

        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());

        // For login "User not found" it usually flows here if not caught by AuthException
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // Changed to 400 for general logic errors
                .body(response);
    }

    /* ============================
       FALLBACK (UNEXPECTED ERRORS)
       ============================ */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(
            Exception ex) {

        Map<String, String> response = new HashMap<>();
        response.put("message", "Internal server error: " + ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(response);
    }
}