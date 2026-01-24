package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import np.edu.nast.payroll.Payroll.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
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
            // Logic to find the specific payroll record to get employee email and salary data
            Payroll payroll = payrollService.getAllPayrolls().stream()
                    .filter(p -> p.getPayrollId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Payroll not found"));

            // Triggers the email using your Gmail configuration
            emailService.sendPayslipEmail(
                    payroll.getEmployee().getEmail(),
                    payroll.getEmployee().getFirstName(),
                    payroll.getNetSalary(),
                    payroll.getPayDate().toString()
            );

            return ResponseEntity.ok().body(Map.of("message", "Email sent successfully!"));
        } catch (Exception e) {
            // Stops the 500 error and provides feedback to the UI
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}