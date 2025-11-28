package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.SalaryGrade;
import np.edu.nast.payroll.Payroll.service.SalaryGradeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary-grades")
public class SalaryGradeController {

    private final SalaryGradeService service;

    public SalaryGradeController(SalaryGradeService service) {
        this.service = service;
    }

    @PostMapping
    public SalaryGrade create(@RequestBody SalaryGrade grade) {
        return service.create(grade);
    }

    @PutMapping("/{id}")
    public SalaryGrade update(@PathVariable Long id, @RequestBody SalaryGrade grade) {
        return service.update(id, grade);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public SalaryGrade getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<SalaryGrade> getAll() {
        return service.getAll();
    }
}
