package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.PayrollAudit;
import np.edu.nast.payroll.Payroll.service.PayrollAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll-audits")
public class PayrollAuditController {

    @Autowired
    private PayrollAuditService service;

    @PostMapping
    public PayrollAudit createPayrollAudit(@RequestBody PayrollAudit audit) {
        return service.savePayrollAudit(audit);
    }

    @GetMapping
    public List<PayrollAudit> getAllPayrollAudits() {
        return service.getAllPayrollAudits();
    }

    @GetMapping("/{id}")
    public PayrollAudit getPayrollAudit(@PathVariable Integer id) {
        return service.getPayrollAuditById(id);
    }

    @DeleteMapping("/{id}")
    public void deletePayrollAudit(@PathVariable Integer id) {
        service.deletePayrollAudit(id);
    }
}
