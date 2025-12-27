package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.dto.auth.PayrollRequest;
import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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

    @PostMapping
    public Payroll create(@RequestBody PayrollRequest request) {
        // This links the frontend form to the logic that searches for Employee ID 8
        return payrollService.processPayrollRequest(request);
    }
}