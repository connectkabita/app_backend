package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.entity.Department;
import np.edu.nast.payroll.Payroll.entity.Designation;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.repository.DepartmentRepository;
import np.edu.nast.payroll.Payroll.repository.DesignationRepository;
import np.edu.nast.payroll.Payroll.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepo;
    private final UserRepository userRepo;
    private final DepartmentRepository departmentRepo;
    private final DesignationRepository designationRepo;

    public EmployeeServiceImpl(EmployeeRepository employeeRepo,
                               UserRepository userRepo,
                               DepartmentRepository departmentRepo,
                               DesignationRepository designationRepo) {
        this.employeeRepo = employeeRepo;
        this.userRepo = userRepo;
        this.departmentRepo = departmentRepo;
        this.designationRepo = designationRepo;
    }

    /* =========================
       CREATE EMPLOYEE
       ========================= */
    @Override
    public Employee create(Employee employee) {

        // ---------- FK NULL CHECK ----------
        if (employee.getDepartment() == null || employee.getDepartment().getDeptId() == null) {
            throw new IllegalArgumentException("Department ID is required");
        }

        if (employee.getPosition() == null || employee.getPosition().getDesignationId() == null) {
            throw new IllegalArgumentException("Designation ID is required");
        }

        // ---------- FK EXISTENCE CHECK ----------
        Department department = departmentRepo.findById(employee.getDepartment().getDeptId())
                .orElseThrow(() ->
                        new RuntimeException("Department not found with id: "
                                + employee.getDepartment().getDeptId()));

        Designation designation = designationRepo.findById(employee.getPosition().getDesignationId())
                .orElseThrow(() ->
                        new RuntimeException("Designation not found with id: "
                                + employee.getPosition().getDesignationId()));

        // ---------- ATTACH MANAGED ENTITIES ----------
        employee.setDepartment(department);
        employee.setPosition(designation);

        Employee savedEmployee = employeeRepo.save(employee);

        // ---------- OPTIONAL EMAIL SYNC (Employee → User) ----------
        Optional<User> userOpt = userRepo.findByEmployee(savedEmployee);
        userOpt.ifPresent(user -> {
            user.setEmail(savedEmployee.getEmail());
            userRepo.save(user);
        });

        return savedEmployee;
    }

    /* =========================
       UPDATE EMPLOYEE
       ========================= */
    @Override
    public Employee update(Integer id, Employee employee) {

        Employee existing = employeeRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found with id: " + id));

        // ---------- FK NULL CHECK ----------
        if (employee.getDepartment() == null || employee.getDepartment().getDeptId() == null ||
                employee.getPosition() == null || employee.getPosition().getDesignationId() == null) {
            throw new IllegalArgumentException(
                    "Department and Designation IDs are required");
        }

        // ---------- FK EXISTENCE CHECK ----------
        Department department = departmentRepo.findById(employee.getDepartment().getDeptId())
                .orElseThrow(() ->
                        new RuntimeException("Department not found with id: "
                                + employee.getDepartment().getDeptId()));

        Designation designation = designationRepo.findById(employee.getPosition().getDesignationId())
                .orElseThrow(() ->
                        new RuntimeException("Designation not found with id: "
                                + employee.getPosition().getDesignationId()));

        // ---------- UPDATE VALUES ----------
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

        // ---------- EMAIL SYNC ----------
        if (employee.getEmail() != null &&
                !employee.getEmail().equals(existing.getEmail())) {

            existing.setEmail(employee.getEmail());

            userRepo.findByEmployee(existing).ifPresent(user -> {
                user.setEmail(employee.getEmail());
                userRepo.save(user);
            });
        }

        return employeeRepo.save(existing);
    }

    /* =========================
       DELETE EMPLOYEE
       ========================= */
    @Override
    public void delete(Integer id) {

        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found with id: " + id));

        // Prevent delete if user exists
        if (userRepo.findByEmployee(employee).isPresent()) {
            throw new RuntimeException(
                    "Cannot delete employee while user account exists");
        }

        employeeRepo.delete(employee);
    }

    /* =========================
       GET EMPLOYEE BY ID
       ========================= */
    @Override
    public Employee getById(Integer id) {
        return employeeRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found with id: " + id));
    }

    /* =========================
       GET ALL EMPLOYEES
       ========================= */
    @Override
    public List<Employee> getAll() {
        return employeeRepo.findAll();
    }
}
