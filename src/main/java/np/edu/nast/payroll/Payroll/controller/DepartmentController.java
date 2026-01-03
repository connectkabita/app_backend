package np.edu.nast.payroll.Payroll.controller;
import np.edu.nast.payroll.Payroll.entity.Department;
import np.edu.nast.payroll.Payroll.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    private final DepartmentService svc;
    public DepartmentController(DepartmentService svc) { this.svc = svc; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Department create(@RequestBody Department d){ return svc.create(d); }
    @GetMapping public List<Department> getAll(){ return svc.getAll(); }
    @GetMapping("/{id}")
    public Department getById(@PathVariable Integer id){ return svc.getById(id); }
    @PutMapping("/{id}")
    public Department update(@PathVariable Integer id,@RequestBody Department d){ return svc.update(id,d); }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Integer id){ svc.delete(id); }
}
