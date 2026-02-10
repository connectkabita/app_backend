package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import np.edu.nast.payroll.Payroll.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payrolls")
@CrossOrigin(origins = "http://localhost:5173")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public List<Payroll> getAll() {
        return payrollService.getAllPayrolls();
    }

    /**
     * STEP 1: PREVIEW
     * Calculates payroll based on inputs, but DOES NOT save to database.
     * Updated with robust error handling to catch "Employee Not Found" errors.
     */
    @PostMapping("/preview")
    public ResponseEntity<?> preview(@RequestBody Map<String, Object> payload) {
        try {
            // Debug log: Check IntelliJ console to see exactly what React sent
            System.out.println("Payroll Preview Request Received: " + payload);

            Payroll previewData = payrollService.calculatePreview(payload);
            return ResponseEntity.ok(previewData);
        } catch (RuntimeException e) {
            // This catches the "Employee not found ID: 1" and sends it to the UI
            System.err.println("Preview Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred."));
        }
    }

    /**
     * STEP 2: PROCESS / FINALIZE
     * Performs the actual transaction and saves to the database.
     */
    @PostMapping("/process")
    public ResponseEntity<?> process(@RequestBody Map<String, Object> payload) {
        try {
            Payroll processedPayroll = payrollService.processPayroll(payload);
            return ResponseEntity.ok(processedPayroll);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/employee/{empId}/history")
    public ResponseEntity<List<Payroll>> getEmployeeHistory(@PathVariable Integer empId) {
        List<Payroll> history = payrollService.getPayrollByEmployeeId(empId);
        return ResponseEntity.ok(history);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Payroll> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> statusUpdate) {
        String newStatus = statusUpdate.get("status");
        Payroll updatedPayroll = payrollService.updateStatus(id, newStatus);
        return ResponseEntity.ok(updatedPayroll);
    }

    @PutMapping("/{id}/void")
    public ResponseEntity<Payroll> voidPayroll(@PathVariable Integer id) {
        Payroll voided = payrollService.voidPayroll(id);
        return ResponseEntity.ok(voided);
    }

    @PostMapping("/{id}/send-email")
    public ResponseEntity<?> sendEmail(@PathVariable Integer id) {
        try {
            Payroll payroll = payrollService.getPayrollById(id);
            // Pass "Manual" as transaction ID for UI-triggered emails
            emailService.generateAndSendPayslip(payroll, "MANUAL_DISBURSEMENT");
            return ResponseEntity.ok().body(Map.of("message", "Payslip PDF email sent successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}