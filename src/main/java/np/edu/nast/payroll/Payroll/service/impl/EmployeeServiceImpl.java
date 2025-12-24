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
        if (employee.getUser() == null || employee.getUser().getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (employee.getDepartment() == null || employee.getDepartment().getDeptId() == null) {
            throw new IllegalArgumentException("Department ID is required");
        }
        if (employee.getPosition() == null || employee.getPosition().getDesignationId() == null) {
            throw new IllegalArgumentException("Designation ID is required");
        }

        // ---------- FK EXISTENCE CHECK ----------
        User user = userRepo.findById(employee.getUser().getUserId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + employee.getUser().getUserId()));

        Department department = departmentRepo.findById(employee.getDepartment().getDeptId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Department not found with id: " + employee.getDepartment().getDeptId()));

        Designation designation = designationRepo.findById(employee.getPosition().getDesignationId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Designation not found with id: " + employee.getPosition().getDesignationId()));

        // ---------- ATTACH MANAGED ENTITIES ----------
        employee.setUser(user);
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
                .orElseThrow(() ->
                        new RuntimeException("Employee not found with id: " + id));

        // ---------- FK NULL CHECK ----------
        if (employee.getUser() == null || employee.getUser().getUserId() == null ||
                employee.getDepartment() == null || employee.getDepartment().getDeptId() == null ||
                employee.getPosition() == null || employee.getPosition().getDesignationId() == null) {
            throw new IllegalArgumentException(
                    "User, Department, and Designation IDs are required");
        }

        // ---------- FK EXISTENCE CHECK ----------
        User user = userRepo.findById(employee.getUser().getUserId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + employee.getUser().getUserId()));

        Department department = departmentRepo.findById(employee.getDepartment().getDeptId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Department not found with id: " + employee.getDepartment().getDeptId()));

        Designation designation = designationRepo.findById(employee.getPosition().getDesignationId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Designation not found with id: " + employee.getPosition().getDesignationId()));

        // ---------- UPDATE VALUES ----------
        existing.setUser(user);
        existing.setDepartment(department);
        existing.setPosition(designation);
        existing.setFirstName(employee.getFirstName());
        existing.setLastName(employee.getLastName());
        existing.setEmail(employee.getEmail());
        existing.setContact(employee.getContact());
        existing.setMaritalStatus(employee.getMaritalStatus());
        existing.setEducation(employee.getEducation());
        existing.setEmploymentStatus(employee.getEmploymentStatus());
        existing.setJoiningDate(employee.getJoiningDate());
        existing.setAddress(employee.getAddress());
        existing.setIsActive(employee.getIsActive());

        return employeeRepo.save(existing);
    }

    /* =========================
       DELETE EMPLOYEE
       ========================= */
    @Override
    public void delete(Integer id) {
        if (!employeeRepo.existsById(id)) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        employeeRepo.deleteById(id);
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
