package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.Role;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.RoleRepository;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final RoleRepository roleRepo;
    private final EmployeeRepository employeeRepo;

    public UserServiceImpl(
            UserRepository repo,
            RoleRepository roleRepo,
            EmployeeRepository employeeRepo
    ) {
        this.repo = repo;
        this.roleRepo = roleRepo;
        this.employeeRepo = employeeRepo;
    }

    /**
     * CREATE USER (Admin creates login for an existing employee)
     * - emp_id MUST exist
     * - email copied from employee
     */
    @Override
    public User create(User user) {

        // 1. Fetch Employee
        if (user.getEmployee() == null || user.getEmployee().getEmpId() == null) {
            throw new RuntimeException("Employee is required to create user");
        }

        Employee employee = employeeRepo.findById(user.getEmployee().getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // 2. Fetch Role
        Role role = roleRepo.findById(user.getRole().getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // 3. Sync email from employee
        user.setEmail(employee.getEmail());

        // 4. Defaults
        user.setRole(role);
        if (user.getStatus() == null) user.setStatus("ACTIVE");
        if (user.getCreatedAt() == null) user.setCreatedAt(LocalDateTime.now());

        return repo.save(user);
    }

    /**
     * UPDATE USER
     * - Updates user email
     * - ALSO updates employee email
     */
    @Override
    public User update(Integer id, User user) {

        User existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existing.setUsername(user.getUsername());
        existing.setPassword(user.getPassword());

        // 1. EMAIL SYNC (User → Employee)
        if (user.getEmail() != null && !user.getEmail().equals(existing.getEmail())) {
            existing.setEmail(user.getEmail());

            Employee employee = existing.getEmployee();
            employee.setEmail(user.getEmail());
            employeeRepo.save(employee);
        }

        // 2. Role update
        if (user.getRole() != null && user.getRole().getRoleId() != null) {
            Role role = roleRepo.findById(user.getRole().getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            existing.setRole(role);
        }

        if (user.getStatus() != null) {
            existing.setStatus(user.getStatus());
        }

        return repo.save(existing);
    }

    /**
     * DELETE USER
     * - Employee remains
     */
    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    @Override
    public User getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<User> getAll() {
        return repo.findAll();
    }

    /**
     * OPTIONAL: Update employee email & sync user
     */
    public void updateEmployeeEmail(Integer empId, String newEmail) {

        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setEmail(newEmail);
        employeeRepo.save(employee);

        repo.findByEmployee(employee).ifPresent(user -> {
            user.setEmail(newEmail);
            repo.save(user);
        });
    }
}
