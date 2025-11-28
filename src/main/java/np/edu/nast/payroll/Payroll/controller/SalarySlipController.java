package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.SalarySlip;
import np.edu.nast.payroll.Payroll.service.SalarySlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary-slips")
public class SalarySlipController {

    @Autowired
    private SalarySlipService service;

    @PostMapping
    public SalarySlip createSalarySlip(@RequestBody SalarySlip slip) {
        return service.saveSalarySlip(slip);
    }

    @GetMapping
    public List<SalarySlip> getAllSalarySlips() {
        return service.getAllSalarySlips();
    }

    @GetMapping("/{id}")
    public SalarySlip getSalarySlip(@PathVariable Integer id) {
        return service.getSalarySlipById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteSalarySlip(@PathVariable Integer id) {
        service.deleteSalarySlip(id);
    }
}
