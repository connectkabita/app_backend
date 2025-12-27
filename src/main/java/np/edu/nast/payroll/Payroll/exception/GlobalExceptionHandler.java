package np.edu.nast.payroll.Payroll.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
       RESOURCE NOT FOUND
       ============================ */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(
            RuntimeException ex) {

        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404
                .body(response);
    }

    /* ============================
       FALLBACK (UNEXPECTED ERRORS)
       ============================ */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(
            Exception ex) {

        Map<String, String> response = new HashMap<>();
        response.put("message", "Internal server error. Please contact admin.");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(response);
    }
}
