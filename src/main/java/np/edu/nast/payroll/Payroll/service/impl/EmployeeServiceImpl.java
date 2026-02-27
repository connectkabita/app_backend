package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.*;
import np.edu.nast.payroll.Payroll.reportdto.AttendanceSummaryDTO;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.service.EmployeeService;
import np.edu.nast.payroll.Payroll.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository departmentRepo;
    private final DesignationRepository designationRepo;
    private final UserRepository userRepo;
    private final AttendanceRepository attendanceRepo;
    private final BankRepository bankRepo;
    private final BankAccountRepository bankAccountRepo;

    public EmployeeServiceImpl(EmployeeRepository employeeRepo,
                               DepartmentRepository departmentRepo,
                               DesignationRepository designationRepo,
                               UserRepository userRepo,
                               AttendanceRepository attendanceRepo,
                               BankRepository bankRepo,
                               BankAccountRepository bankAccountRepo) {
        this.employeeRepo = employeeRepo;
        this.departmentRepo = departmentRepo;
        this.designationRepo = designationRepo;
        this.userRepo = userRepo;
        this.attendanceRepo = attendanceRepo;
        this.bankRepo = bankRepo;
        this.bankAccountRepo = bankAccountRepo;
    }

    /**
     * FIX: Implementation of the method that was missing in your build.
     * This bridges the Security Username (from JWT) to the Employee ID.
     */
    @Override
    @Transactional(readOnly = true)
    public Integer findIdByUsername(String username) {
        return employeeRepo.findByUser_Username(username)
                .map(Employee::getEmpId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for user: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getByUserId(Integer userId) {
        return employeeRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No Employee profile linked to User ID: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats(Integer id) {
        Employee emp = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        LocalDate now = LocalDate.now();
        List<Object[]> summaryResult = attendanceRepo.summary(now.getYear(), now.getMonthValue());

        long present = 0, absent = 0, leave = 0;
        if (!summaryResult.isEmpty() && summaryResult.get(0) != null) {
            Object[] row = summaryResult.get(0);
            present = row[0] != null ? ((Number) row[0]).longValue() : 0;
            absent  = row[1] != null ? ((Number) row[1]).longValue() : 0;
            leave   = row[2] != null ? ((Number) row[2]).longValue() : 0;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("lastSalary", emp.getBasicSalary() != null ? emp.getBasicSalary() : 0.0);
        stats.put("remainingLeaves", 12);
        stats.put("attendanceSummary", new AttendanceSummaryDTO(present, absent, leave));
        return stats;
    }

    @Override
    public Employee create(Employee employee) {
        if (employeeRepo.existsByEmail(employee.getEmail())) {
            throw new EmailAlreadyExistsException("Employee email already exists: " + employee.getEmail());
        }

        User associatedUser = userRepo.findByEmailIgnoreCase(employee.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No system user found with email: " + employee.getEmail()));

        if (employeeRepo.findByUser_UserId(associatedUser.getUserId()).isPresent()) {
            throw new RuntimeException("This user account is already linked to another employee.");
        }

        employee.setUser(associatedUser);
        validateAndAttachForeignKeys(employee);

        List<BankAccount> incomingBankAccounts = employee.getBankAccounts();
        employee.setBankAccounts(null);

        Employee savedEmployee = employeeRepo.save(employee);

        if (incomingBankAccounts != null && !incomingBankAccounts.isEmpty()) {
            BankAccount bankDetails = incomingBankAccounts.get(0);
            if (bankDetails.getBank() != null && bankDetails.getAccountNumber() != null) {
                mapAndSaveBankAccount(savedEmployee, bankDetails, true);
            }
        }

        return savedEmployee;
    }

    @Override
    public Employee update(Integer id, Employee employee) {
        Employee existing = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        if (employee.getEmail() != null && !employee.getEmail().equalsIgnoreCase(existing.getEmail())) {
            if (employeeRepo.existsByEmail(employee.getEmail())) throw new EmailAlreadyExistsException("Email exists");
            User newUser = userRepo.findByEmailIgnoreCase(employee.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("No User found for the new email"));
            existing.setUser(newUser);
            existing.setEmail(employee.getEmail());
        }

        validateAndAttachForeignKeys(employee);

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
        existing.setDepartment(employee.getDepartment());
        existing.setPosition(employee.getPosition());
        existing.setPayGroup(employee.getPayGroup());

        if (existing.getUser() != null) existing.getUser().setEmail(existing.getEmail());

        if (employee.getBankAccounts() != null && !employee.getBankAccounts().isEmpty()) {
            mapAndSaveBankAccount(existing, employee.getBankAccounts().get(0), true);
        }

        return employeeRepo.save(existing);
    }

    private void mapAndSaveBankAccount(Employee employee, BankAccount details, boolean isPrimary) {
        Bank bank = bankRepo.findById(details.getBank().getBankId())
                .orElseThrow(() -> new ResourceNotFoundException("Bank not found"));

        BankAccount account = bankAccountRepo.findByEmployeeEmpId(employee.getEmpId()).stream()
                .filter(BankAccount::getIsPrimary)
                .findFirst()
                .orElse(new BankAccount());

        account.setEmployee(employee);
        account.setBank(bank);
        account.setAccountNumber(details.getAccountNumber());
        account.setAccountType(details.getAccountType());
        account.setCurrency(details.getCurrency());
        account.setIsPrimary(isPrimary);

        bankAccountRepo.save(account);
    }

    private void validateAndAttachForeignKeys(Employee employee) {
        if (employee.getDepartment() == null || employee.getDepartment().getDeptId() == null)
            throw new IllegalArgumentException("Department ID is mandatory");
        if (employee.getPosition() == null || employee.getPosition().getDesignationId() == null)
            throw new IllegalArgumentException("Designation ID is mandatory");

        Department dept = departmentRepo.findById(employee.getDepartment().getDeptId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        Designation desig = designationRepo.findById(employee.getPosition().getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));

        employee.setDepartment(dept);
        employee.setPosition(desig);
    }

    @Override public void delete(Integer id) { employeeRepo.deleteById(id); }
    @Override public Employee getById(Integer id) { return employeeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee not found")); }
    @Override public List<Employee> getAll() { return employeeRepo.findAll(); }
    @Override public Employee getByEmail(String email) { return employeeRepo.findByUser_Email(email).or(() -> employeeRepo.findByEmail(email)).orElseThrow(() -> new ResourceNotFoundException("Email not found")); }

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