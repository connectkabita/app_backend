package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.SalaryComponentType;
import np.edu.nast.payroll.Payroll.service.SalaryComponentTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary-component-types")
@CrossOrigin(origins = "http://localhost:5173")
public class SalaryComponentTypeController {

    private final SalaryComponentTypeService service;

    public SalaryComponentTypeController(SalaryComponentTypeService service) {
        this.service = service;
    }

    @PostMapping
    public SalaryComponentType create(@RequestBody SalaryComponentType type) {
        return service.create(type);
    }

    @PutMapping("/{id}")
    public SalaryComponentType update(@PathVariable Long id, @RequestBody SalaryComponentType type) {
        return service.update(id, type);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public SalaryComponentType getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<SalaryComponentType> getAll() {
        return service.getAll();
    }
}
