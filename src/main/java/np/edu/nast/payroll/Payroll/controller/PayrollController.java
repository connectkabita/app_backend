package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.service.PayrollService;
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

    @GetMapping
    public List<Payroll> getAll() {
        return payrollService.getAllPayrolls();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Payroll> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> statusUpdate) {
        String newStatus = statusUpdate.get("status");
        Payroll updatedPayroll = payrollService.updateStatus(id, newStatus);
        return ResponseEntity.ok(updatedPayroll);
    }
}