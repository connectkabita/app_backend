package np.edu.nast.payroll.Payroll.controller;

import lombok.extern.slf4j.Slf4j;
import np.edu.nast.payroll.Payroll.dto.auth.PayrollDashboardDTO;
import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import np.edu.nast.payroll.Payroll.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payrolls")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    /**
     * ANDROID APP ENDPOINT: Fetches payroll data for the Salary Fragment.
     * Updated to allow any authenticated employee (USER, EMPLOYEE, or ADMIN)
     * to view their own salary history on the mobile device.
     */
    @GetMapping("/command-center")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<?> getPayrollCommandCenter(
            @RequestParam(name = "month") int month,
            @RequestParam(name = "year") int year) {
        try {
            // Get the username from the JWT context set by JwtFilter
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("Mobile request: Fetching payroll for user [{}] for period {}-{}", username, month, year);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            // Ensure the user actually has an associated employee profile in the DB
            if (user.getEmployee() == null) {
                log.warn("Access Warning: User [{}] exists but has no linked Employee record.", username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Account is not linked to an active Employee profile"));
            }

            Integer empId = user.getEmployee().getEmpId();
            Payroll record = payrollService.getPayrollByEmployeeAndMonth(empId, month, year);

            // Wrap in a list as expected by the Android Adapter to prevent null-pointer crashes
            return ResponseEntity.ok(Map.of("employeeRows", record != null ? List.of(record) : List.of()));

        } catch (Exception e) {
            log.error("Internal Error in command-center for user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Server Error: " + e.getMessage()));
        }
    }

    // --- ADMINISTRATIVE ENDPOINTS ---

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<Payroll> getAll() {
        return payrollService.getAllPayrolls();
    }

    @GetMapping("/batch-calculate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getBatchCalculation(
            @RequestParam(name = "month") String month,
            @RequestParam(name = "year") int year) {
        try {
            List<PayrollDashboardDTO> batchData = payrollService.getBatchCalculation(month, year);
            return ResponseEntity.ok(batchData != null ? batchData : List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/preview")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> preview(@RequestBody Map<String, Object> payload) {
        try {
            Payroll previewData = payrollService.calculatePreview(payload);
            return ResponseEntity.ok(previewData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/process")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> process(@RequestBody Map<String, Object> payload) {
        try {
            Payroll processedPayroll = payrollService.processPayroll(payload);
            return ResponseEntity.ok(processedPayroll);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/employee/{empId}/history")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Payroll>> getEmployeeHistory(@PathVariable Integer empId) {
        return ResponseEntity.ok(payrollService.getPayrollByEmployeeId(empId));
    }

    @PostMapping("/{id}/finalize")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> finalizePayroll(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        try {
            String transactionRef = payload.getOrDefault("transactionRef", "N/A");
            payrollService.finalizePayroll(id, transactionRef);
            return ResponseEntity.ok(Map.of("message", "Payroll finalized."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Payroll> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> statusUpdate) {
        return ResponseEntity.ok(payrollService.updateStatus(id, statusUpdate.get("status")));
    }

    @PostMapping("/{id}/send-email")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> sendEmail(@PathVariable Integer id) {
        try {
            Payroll payroll = payrollService.getPayrollById(id);
            emailService.generateAndSendPayslip(payroll, "MANUAL_DISBURSEMENT");
            return ResponseEntity.ok().body(Map.of("message", "Email sent!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}