package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payrolls")
public class PayrollController {

    @Autowired
    private PayrollService service;

    @PostMapping
    public Payroll createPayroll(@RequestBody Payroll payroll) {
        return service.savePayroll(payroll);
    }

    @GetMapping
    public List<Payroll> getAllPayrolls() {
        return service.getAllPayrolls();
    }

    @GetMapping("/{id}")
    public Payroll getPayroll(@PathVariable Integer id) {
        return service.getPayrollById(id);
    }

    @DeleteMapping("/{id}")
    public void deletePayroll(@PathVariable Integer id) {
        service.deletePayroll(id);
    }
}
