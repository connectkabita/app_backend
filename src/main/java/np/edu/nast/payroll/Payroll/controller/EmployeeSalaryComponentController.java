package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.EmployeeSalaryComponent;
import np.edu.nast.payroll.Payroll.service.EmployeeSalaryComponentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-salary-components")
public class EmployeeSalaryComponentController {

    private final EmployeeSalaryComponentService service;

    public EmployeeSalaryComponentController(EmployeeSalaryComponentService service) {
        this.service = service;
    }

    @PostMapping
    public EmployeeSalaryComponent create(@RequestBody EmployeeSalaryComponent esc) {
        return service.create(esc);
    }

    @PutMapping("/{id}")
    public EmployeeSalaryComponent update(@PathVariable Integer id, @RequestBody EmployeeSalaryComponent esc) {
        return service.update(id, esc);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public EmployeeSalaryComponent getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @GetMapping
    public List<EmployeeSalaryComponent> getAll() {
        return service.getAll();
    }
}
