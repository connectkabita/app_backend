package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Department;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.repository.DepartmentRepository;
import np.edu.nast.payroll.Payroll.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository repo;

    public DepartmentServiceImpl(DepartmentRepository repo) {
        this.repo = repo;
    }

    @Override
    public Department create(Department dept) {
        if (dept == null || dept.getDeptName() == null || dept.getDeptName().isBlank()) {
            throw new IllegalArgumentException("Department name must not be empty");
        }
        return repo.save(dept);
    }

    @Override
    public Department update(Integer id, Department dept) {
        if (dept == null || dept.getDeptName() == null || dept.getDeptName().isBlank()) {
            throw new IllegalArgumentException("Department name must not be empty");
        }

        Department existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        existing.setDeptName(dept.getDeptName());
        return repo.save(existing);
    }

    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Department not found with ID: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    public Department getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
    }

    @Override
    public List<Department> getAll() {
        return repo.findAll();
    }
}
