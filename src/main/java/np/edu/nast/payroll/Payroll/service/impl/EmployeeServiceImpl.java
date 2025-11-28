package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repo;

    public EmployeeServiceImpl(EmployeeRepository repo) { this.repo = repo; }

    @Override
    public Employee create(Employee employee) { return repo.save(employee); }

    @Override
    public Employee update(Integer id, Employee employee) {
        Employee existing = repo.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
        existing.setFirstName(employee.getFirstName());
        existing.setLastName(employee.getLastName());
        existing.setEmail(employee.getEmail());
        existing.setContact(employee.getContact());
        existing.setMaritalStatus(employee.getMaritalStatus());
        existing.setPosition(employee.getPosition());
        existing.setEducation(employee.getEducation());
        existing.setEmploymentStatus(employee.getEmploymentStatus());
        existing.setJoiningDate(employee.getJoiningDate());
        existing.setAddress(employee.getAddress());
        existing.setDepartment(employee.getDepartment());
        existing.setIsActive(employee.getIsActive());
        return repo.save(existing);
    }

    @Override
    public void delete(Integer id) { repo.deleteById(id); }

    @Override
    public Employee getById(Integer id) { return repo.findById(id).orElseThrow(() -> new RuntimeException("Employee not found")); }

    @Override
    public List<Employee> getAll() { return repo.findAll(); }
}
