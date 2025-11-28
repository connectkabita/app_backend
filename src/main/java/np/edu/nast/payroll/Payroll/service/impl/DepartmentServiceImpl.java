package np.edu.nast.payroll.Payroll.service.impl;
import np.edu.nast.payroll.Payroll.entity.Department;
import np.edu.nast.payroll.Payroll.repository.DepartmentRepository;
import np.edu.nast.payroll.Payroll.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository repo;
    public DepartmentServiceImpl(DepartmentRepository repo) { this.repo = repo; }

    @Override public Department create(Department dept) { return repo.save(dept); }
    @Override public Department update(Integer id, Department dept) {
        Department ex = repo.findById(id).orElseThrow(() -> new RuntimeException("Department not found"));
        ex.setDeptName(dept.getDeptName());
        return repo.save(ex);
    }
    @Override public void delete(Integer id) { repo.deleteById(id); }
    @Override public Department getById(Integer id) { return repo.findById(id).orElseThrow(() -> new RuntimeException("Department not found")); }
    @Override public List<Department> getAll() { return repo.findAll(); }
}
