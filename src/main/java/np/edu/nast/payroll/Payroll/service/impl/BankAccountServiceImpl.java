package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.BankAccount;
import np.edu.nast.payroll.Payroll.entity.Bank;
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.repository.BankAccountRepository;
import np.edu.nast.payroll.Payroll.repository.BankRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.service.BankAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository repo;
    private final EmployeeRepository employeeRepo;
    private final BankRepository bankRepo;

    public BankAccountServiceImpl(
            BankAccountRepository repo,
            EmployeeRepository employeeRepo,
            BankRepository bankRepo
    ) {
        this.repo = repo;
        this.employeeRepo = employeeRepo;
        this.bankRepo = bankRepo;
    }

    @Override
    public BankAccount create(BankAccount account) {
        // FK null check
        if (account.getEmployee() == null || account.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        if (account.getBank() == null || account.getBank().getBankId() == null) {
            throw new IllegalArgumentException("Bank ID is required");
        }

        // FK existence check
        Employee employee = employeeRepo.findById(account.getEmployee().getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with ID: " + account.getEmployee().getEmpId()));

        Bank bank = bankRepo.findById(account.getBank().getBankId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bank not found with ID: " + account.getBank().getBankId()));

        // Attach managed entities
        account.setEmployee(employee);
        account.setBank(bank);

        // Only one primary account per employee
        if (Boolean.TRUE.equals(account.getIsPrimary())) {
            repo.findByEmployeeEmpId(employee.getEmpId())
                    .forEach(existing -> {
                        existing.setIsPrimary(false);
                        repo.save(existing);
                    });
        }

        return repo.save(account);
    }

    @Override
    public BankAccount update(Integer id, BankAccount account) {
        BankAccount existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount not found with ID: " + id));

        // Only update allowed fields
        existing.setAccountNumber(account.getAccountNumber());
        existing.setAccountType(account.getAccountType());
        existing.setCurrency(account.getCurrency());
        existing.setIsPrimary(account.getIsPrimary());

        return repo.save(existing);
    }

    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("BankAccount not found with ID: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    public BankAccount getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount not found with ID: " + id));
    }

    @Override
    public List<BankAccount> getAll() {
        return repo.findAll();
    }

    @Override
    public List<BankAccount> findByEmployeeId(Integer empId) {
        // Validate employee exists
        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + empId));

        return repo.findByEmployeeEmpId(empId);
    }
}
