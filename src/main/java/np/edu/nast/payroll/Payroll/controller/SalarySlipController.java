package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.SalarySlip;
import np.edu.nast.payroll.Payroll.service.SalarySlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary-slips")
public class SalarySlipController {

    @Autowired
    private SalarySlipService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalarySlip> createSalarySlip(@RequestBody SalarySlip slip) {
        return ResponseEntity.ok(service.saveSalarySlip(slip));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<SalarySlip> getAllSalarySlips() {
        return service.getAllSalarySlips();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<SalarySlip> getSalarySlip(@PathVariable Integer id) {
        SalarySlip slip = service.getSalarySlipById(id);
        return (slip != null) ? ResponseEntity.ok(slip) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSalarySlip(@PathVariable Integer id) {
        service.deleteSalarySlip(id);
        return ResponseEntity.noContent().build();
    }
}