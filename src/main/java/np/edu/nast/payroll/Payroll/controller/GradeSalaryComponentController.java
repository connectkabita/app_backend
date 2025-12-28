package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.GradeSalaryComponent;
import np.edu.nast.payroll.Payroll.service.GradeSalaryComponentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grade-salary-components")
@CrossOrigin(origins = "http://localhost:5173")
public class GradeSalaryComponentController {

    private final GradeSalaryComponentService service;

    public GradeSalaryComponentController(GradeSalaryComponentService service) {
        this.service = service;
    }

    @PostMapping
    public GradeSalaryComponent create(@RequestBody GradeSalaryComponent gsc) {
        return service.create(gsc);
    }

    @PutMapping("/{id}")
    public GradeSalaryComponent update(@PathVariable Long id, @RequestBody GradeSalaryComponent gsc) {
        return service.update(id, gsc);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public GradeSalaryComponent getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<GradeSalaryComponent> getAll() {
        return service.getAll();
    }
}
