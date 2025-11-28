package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.SalaryComponent;
import np.edu.nast.payroll.Payroll.service.SalaryComponentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary-components")
public class SalaryComponentController {

    private final SalaryComponentService service;

    public SalaryComponentController(SalaryComponentService service) {
        this.service = service;
    }

    @PostMapping
    public SalaryComponent create(@RequestBody SalaryComponent component) {
        return service.create(component);
    }

    @PutMapping("/{id}")
    public SalaryComponent update(@PathVariable Long id, @RequestBody SalaryComponent component) {
        return service.update(id, component);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public SalaryComponent getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<SalaryComponent> getAll() {
        return service.getAll();
    }
}
