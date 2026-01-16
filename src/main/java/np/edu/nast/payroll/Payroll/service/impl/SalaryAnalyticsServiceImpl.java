package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.BankAccount;
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.PayrollRepository;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.salaryDTO.EmployeeSalaryAnalyticsDTO;
import np.edu.nast.payroll.Payroll.service.SalaryAnalyticsService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class SalaryAnalyticsServiceImpl implements SalaryAnalyticsService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PayrollRepository payrollRepository;

    @Override
    public EmployeeSalaryAnalyticsDTO getSalaryForUser(String username, YearMonth yearMonth) {

        // 1️⃣ Find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 2️⃣ Find employee
        Employee employee = employeeRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        // 3️⃣ Find payroll for the month
        String year = String.valueOf(yearMonth.getYear());
        String month = String.format("%02d", yearMonth.getMonthValue());

        Payroll payroll = payrollRepository.findPayrollForMonth(
                employee.getEmpId(),
                year,
                month
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No payroll record found for the selected month"
        ));



        // 4️⃣ Primary bank account + bank name
        BankAccount primaryAccount = employee.getBankAccounts().stream()
                .filter(b -> Boolean.TRUE.equals(b.getIsPrimary()))
                .findFirst()
                .orElse(null);

        String bankAccount = primaryAccount != null ? primaryAccount.getAccountNumber() : "N/A";
        String bankName = primaryAccount != null ? primaryAccount.getBank().getBankName() : "N/A";

        // 5️⃣ Taxable amount
        Double taxableAmount = payroll.getGrossSalary() - payroll.getTotalDeductions() - payroll.getTotalTax();

        // 6️⃣ Build DTO
        return new EmployeeSalaryAnalyticsDTO(
                employee.getFirstName() + " " + employee.getLastName(),
                bankAccount,
                bankName,
                employee.getPosition().getDesignationTitle(),
                employee.getEmploymentStatus(),
                employee.getBasicSalary(),
                payroll.getGrossSalary(),
                payroll.getTotalAllowances(),
                payroll.getTotalDeductions(),
                taxableAmount,
                payroll.getNetSalary()
        );
    }
}
