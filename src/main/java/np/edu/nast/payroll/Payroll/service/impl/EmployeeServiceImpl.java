package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.Department;
import np.edu.nast.payroll.Payroll.entity.Designation;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.exception.EmailAlreadyExistsException;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.DepartmentRepository;
import np.edu.nast.payroll.Payroll.repository.DesignationRepository;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
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
    private final UserRepository userRepo; // NEW DEPENDENCY

    public EmployeeServiceImpl(EmployeeRepository employeeRepo,
                               DepartmentRepository departmentRepo,
                               DesignationRepository designationRepo,
                               UserRepository userRepo) {
        this.employeeRepo = employeeRepo;
        this.departmentRepo = departmentRepo;
        this.designationRepo = designationRepo;
        this.userRepo = userRepo;
    }

    /* =========================
       CREATE EMPLOYEE
       ========================= */
    @Override
    public Employee create(Employee employee) {
        // 1. EMAIL UNIQUENESS CHECK
        if (employeeRepo.existsByEmail(employee.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + employee.getEmail());
        }

        // 2. NEW LOGIC: MANDATORY USER CHECK
        // Without a user account with this email, employee cannot be registered
        User associatedUser = userRepo.findByEmailIgnoreCase(employee.getEmail())
                .orElseThrow(() -> new RuntimeException("No user account found with email: " + employee.getEmail() +
                        ". Please create the User account first."));

        // 3. Prevent duplicate linkage (One User -> One Employee)
        if (employeeRepo.findByUser_UserId(associatedUser.getUserId()).isPresent()) {
            throw new RuntimeException("This user is already registered as an employee.");
        }

        // 4. Attach User as Foreign Key
        employee.setUser(associatedUser);

        // Validate and attach managed entities for Department and Designation
        validateAndAttachForeignKeys(employee);

        return employeeRepo.save(employee);
    }

    /* =========================
       UPDATE EMPLOYEE (Full Logic)
       ========================= */
    @Override
    public Employee update(Integer id, Employee employee) {
        // 1. Check if employee exists
        Employee existing = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // 2. Email uniqueness check
        if (employee.getEmail() != null &&
                !employee.getEmail().equalsIgnoreCase(existing.getEmail())) {

            if(employeeRepo.existsByEmail(employee.getEmail())) {
                throw new EmailAlreadyExistsException("Email already exists: " + employee.getEmail());
            }

            // If updating email, check if the NEW email exists in the User table
            User newUserAccount = userRepo.findByEmailIgnoreCase(employee.getEmail())
                    .orElseThrow(() -> new RuntimeException("Cannot update email: No User account found for " + employee.getEmail()));

            existing.setUser(newUserAccount);
            existing.setEmail(employee.getEmail());
        }

        // 3. Validate and fetch managed objects for the Foreign Keys
        validateAndAttachForeignKeys(employee);

        // 4. Map values from the request to the existing managed entity (PREVIOUS LOGIC PRESERVED)
        existing.setFirstName(employee.getFirstName());
        existing.setLastName(employee.getLastName());
        existing.setContact(employee.getContact());
        existing.setMaritalStatus(employee.getMaritalStatus());
        existing.setEducation(employee.getEducation());
        existing.setEmploymentStatus(employee.getEmploymentStatus());
        existing.setJoiningDate(employee.getJoiningDate());
        existing.setAddress(employee.getAddress());
        existing.setIsActive(employee.getIsActive());
        existing.setBasicSalary(employee.getBasicSalary());

        // Update the actual relationship objects
        existing.setDepartment(employee.getDepartment());
        existing.setPosition(employee.getPosition());

        // 5. Synchronize Email with User account if it exists (PREVIOUS LOGIC PRESERVED)
        if (existing.getEmail() != null && existing.getUser() != null) {
            existing.getUser().setEmail(existing.getEmail());
        }

        return employeeRepo.save(existing);
    }

    /* =========================
       PRIVATE HELPER: VALIDATE KEYS
       ========================= */
    private void validateAndAttachForeignKeys(Employee employee) {
        // Check for Department
        if (employee.getDepartment() == null || employee.getDepartment().getDeptId() == null) {
            throw new IllegalArgumentException("Department ID is required");
        }

        // Check for Position (Designation)
        if (employee.getPosition() == null || employee.getPosition().getDesignationId() == null) {
            throw new IllegalArgumentException("Designation (Position) ID is required");
        }

        // Fetch managed instances
        Department dept = departmentRepo.findById(employee.getDepartment().getDeptId())
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + employee.getDepartment().getDeptId()));

        Designation desig = designationRepo.findById(employee.getPosition().getDesignationId())
                .orElseThrow(() -> new RuntimeException("Designation not found with ID: " + employee.getPosition().getDesignationId()));

        // Re-attach
        employee.setDepartment(dept);
        employee.setPosition(desig);
    }

    /* =========================
       DELETE EMPLOYEE
       ========================= */
    @Override
    public void delete(Integer id) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
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
       STATS MODULE (Used by Dashboard)
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