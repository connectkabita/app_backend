package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.Department;
import np.edu.nast.payroll.Payroll.entity.Designation;
import np.edu.nast.payroll.Payroll.exception.EmailAlreadyExistsException;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.DepartmentRepository;
import np.edu.nast.payroll.Payroll.repository.DesignationRepository;
import np.edu.nast.payroll.Payroll.service.EmployeeService;
import np.edu.nast.payroll.Payroll.service.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository departmentRepo;
    private final DesignationRepository designationRepo;
    private final EmailService emailService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepo,
                               DepartmentRepository departmentRepo,
                               DesignationRepository designationRepo,
                               EmailService emailService) {
        this.employeeRepo = employeeRepo;
        this.departmentRepo = departmentRepo;
        this.designationRepo = designationRepo;
        this.emailService = emailService;
    }

    @Override
    public Employee create(Employee employee) {
        // 1. Email Uniqueness Check
        if (employeeRepo.existsByEmail(employee.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered: " + employee.getEmail());
        }

        // 2. Resolve Relationships
        Department department = departmentRepo.findById(employee.getDepartment().getDeptId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Designation designation = designationRepo.findById(employee.getPosition().getDesignationId())
                .orElseThrow(() -> new RuntimeException("Designation not found"));

        employee.setDepartment(department);
        employee.setPosition(designation);

        // 3. Capture the Transient password from AddEmployee.jsx
        String rawPassword = employee.getPassword();

        // 4. Save to Database
        Employee savedEmployee = employeeRepo.save(employee);

        // 5. Send Email
        if (rawPassword != null && !rawPassword.isEmpty()) {
            try {
                emailService.sendRegistrationEmail(
                        savedEmployee.getEmail(),
                        savedEmployee.getFirstName() + " " + savedEmployee.getLastName(),
                        rawPassword
                );
            } catch (Exception e) {
                System.err.println("Mailing Error: Record saved, but email failed. Reason: " + e.getMessage());
            }
        }

        return savedEmployee;
    }

    @Override
    public Employee update(Integer id, Employee employee) {
        // Fetch existing record to ensure we have the correct persistent identity
        Employee existing = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Email uniqueness check (ignore if email belongs to the current record)
        if (employee.getEmail() != null && !employee.getEmail().equals(existing.getEmail()) &&
                employeeRepo.existsByEmail(employee.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // Resolve updated Department and Designation
        Department department = departmentRepo.findById(employee.getDepartment().getDeptId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Designation designation = designationRepo.findById(employee.getPosition().getDesignationId())
                .orElseThrow(() -> new RuntimeException("Designation not found"));

        // Map values from frontend payload to existing entity
        existing.setDepartment(department);
        existing.setPosition(designation);
        existing.setFirstName(employee.getFirstName());
        existing.setLastName(employee.getLastName());
        existing.setContact(employee.getContact());
        existing.setMaritalStatus(employee.getMaritalStatus());
        existing.setEducation(employee.getEducation());
        existing.setEmploymentStatus(employee.getEmploymentStatus());
        existing.setAddress(employee.getAddress());
        existing.setIsActive(employee.getIsActive());

        if (employee.getJoiningDate() != null) {
            existing.setJoiningDate(employee.getJoiningDate());
        }

        if (employee.getEmail() != null) {
            existing.setEmail(employee.getEmail());
            if (existing.getUser() != null) {
                existing.getUser().setEmail(employee.getEmail());
            }
        }

        // The 'existing' object still holds the correct empId mapping for the WHERE clause
        return employeeRepo.save(existing);
    }

    @Override
    public void delete(Integer id) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employeeRepo.delete(employee);
    }

    @Override
    public Employee getById(Integer id) {
        return employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    @Override
    public List<Employee> getAll() {
        return employeeRepo.findAll();
    }

    @Override
    public Map<Integer, Long> getActiveEmployeeStats() {
        List<Object[]> result = employeeRepo.countActiveEmployeesPerMonth();
        Map<Integer, Long> stats = new HashMap<>();
        for (Object[] row : result) {
            stats.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }
        return stats;
    }
}