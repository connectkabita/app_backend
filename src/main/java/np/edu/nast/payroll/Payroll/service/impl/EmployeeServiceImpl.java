package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.Department;
import np.edu.nast.payroll.Payroll.entity.Designation;
import np.edu.nast.payroll.Payroll.exception.EmailAlreadyExistsException;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.DepartmentRepository;
import np.edu.nast.payroll.Payroll.repository.DesignationRepository;
import np.edu.nast.payroll.Payroll.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository departmentRepo;
    private final DesignationRepository designationRepo;

    public EmployeeServiceImpl(EmployeeRepository employeeRepo,
                               DepartmentRepository departmentRepo,
                               DesignationRepository designationRepo) {
        this.employeeRepo = employeeRepo;
        this.departmentRepo = departmentRepo;
        this.designationRepo = designationRepo;
    }

    /* =========================
       CREATE EMPLOYEE
       ========================= */
    @Override
    public Employee create(Employee employee) {

        // EMAIL UNIQUENESS CHECK
        if (employeeRepo.existsByEmail(employee.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // FK NULL CHECK
        if (employee.getDepartment() == null || employee.getDepartment().getDeptId() == null) {
            throw new IllegalArgumentException("Department ID is required");
        }
        if (employee.getPosition() == null || employee.getPosition().getDesignationId() == null) {
            throw new IllegalArgumentException("Designation ID is required");
        }

        // FK EXISTENCE CHECK
        Department department = departmentRepo.findById(employee.getDepartment().getDeptId())
                .orElseThrow(() -> new RuntimeException(
                        "Department not found with id: " + employee.getDepartment().getDeptId()
                ));

        Designation designation = designationRepo.findById(employee.getPosition().getDesignationId())
                .orElseThrow(() -> new RuntimeException(
                        "Designation not found with id: " + employee.getPosition().getDesignationId()
                ));

        employee.setDepartment(department);
        employee.setPosition(designation);

        return employeeRepo.save(employee);
    }

    /* =========================
       UPDATE EMPLOYEE
       ========================= */
    @Override
    public Employee update(Integer id, Employee employee) {

        Employee existing = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // EMAIL UNIQUENESS CHECK (ALLOW SAME EMPLOYEE)
        if (employee.getEmail() != null &&
                !employee.getEmail().equals(existing.getEmail()) &&
                employeeRepo.existsByEmail(employee.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // FK NULL CHECK
        if (employee.getDepartment() == null || employee.getDepartment().getDeptId() == null ||
                employee.getPosition() == null || employee.getPosition().getDesignationId() == null) {
            throw new IllegalArgumentException("Department and Designation IDs are required");
        }

        // FK EXISTENCE CHECK
        Department department = departmentRepo.findById(employee.getDepartment().getDeptId())
                .orElseThrow(() -> new RuntimeException(
                        "Department not found with id: " + employee.getDepartment().getDeptId()
                ));

        Designation designation = designationRepo.findById(employee.getPosition().getDesignationId())
                .orElseThrow(() -> new RuntimeException(
                        "Designation not found with id: " + employee.getPosition().getDesignationId()
                ));

        // UPDATE VALUES
        existing.setDepartment(department);
        existing.setPosition(designation);
        existing.setFirstName(employee.getFirstName());
        existing.setLastName(employee.getLastName());
        existing.setContact(employee.getContact());
        existing.setMaritalStatus(employee.getMaritalStatus());
        existing.setEducation(employee.getEducation());
        existing.setEmploymentStatus(employee.getEmploymentStatus());
        existing.setJoiningDate(employee.getJoiningDate());
        existing.setAddress(employee.getAddress());
        existing.setIsActive(employee.getIsActive());

        // EMAIL = SOURCE OF TRUTH
        if (employee.getEmail() != null) {
            existing.setEmail(employee.getEmail());

            if (existing.getUser() != null) {
                existing.getUser().setEmail(employee.getEmail());
            }
        }

        return employeeRepo.save(existing);
    }

    /* =========================
       DELETE EMPLOYEE
       ========================= */
    @Override
    public void delete(Integer id) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employeeRepo.delete(employee);
    }

    /* =========================
       GET EMPLOYEE BY ID
       ========================= */
    @Override
    public Employee getById(Integer id) {
        return employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    /* =========================
       GET ALL EMPLOYEES
       ========================= */
    @Override
    public List<Employee> getAll() {
        return employeeRepo.findAll();
    }

    /* =========================
       ACTIVE EMPLOYEE STATS
       ========================= */
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
