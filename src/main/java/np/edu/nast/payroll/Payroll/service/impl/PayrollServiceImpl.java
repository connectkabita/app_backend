package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.edu.nast.payroll.Payroll.entity.*;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepo;
    private final EmployeeRepository employeeRepo;
    private final UserRepository userRepo;
    private final BankAccountRepository bankAccountRepo;
    private final PaymentMethodRepository paymentMethodRepo;
    private final PayGroupRepository payGroupRepo;

    @Override
    public List<Payroll> getAllPayrolls() {
        return payrollRepo.findAll();
    }

    @Override
    public List<Payroll> getPayrollByEmployeeId(Integer empId) {
        // Fixes Axios 500 error by correctly querying history
        return payrollRepo.findByEmployeeEmpId(empId);
    }

    @Override
    @Transactional
    public Payroll voidPayroll(Integer id) {
        // Correctly overrides the interface method to handle UI "Void" action
        Payroll p = payrollRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));
        p.setStatus("VOIDED");
        return payrollRepo.save(p);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Payroll processPayroll(Map<String, Object> payload) {
        try {
            Integer empId = Integer.valueOf(payload.get("empId").toString());
            Integer accountId = Integer.valueOf(payload.get("accountId").toString());
            Integer methodId = Integer.valueOf(payload.get("paymentMethodId").toString());
            Integer payGroupId = Integer.valueOf(payload.get("payGroupId").toString());

            Employee employee = employeeRepo.findById(empId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            BankAccount account = bankAccountRepo.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Bank Account not found"));
            PaymentMethod method = paymentMethodRepo.findById(methodId)
                    .orElseThrow(() -> new RuntimeException("Payment Method not found"));
            PayGroup payGroup = payGroupRepo.findById(payGroupId)
                    .orElseThrow(() -> new RuntimeException("Pay Group not found"));

            // Get current logged in user or first admin for processing
            User admin = userRepo.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No admin user found"));

            Payroll payroll = new Payroll();
            payroll.setEmployee(employee);
            payroll.setProcessedBy(admin);
            payroll.setPaymentAccount(account);
            payroll.setPaymentMethod(method);
            payroll.setPayGroup(payGroup);
            payroll.setPayDate(LocalDate.now());
            payroll.setGrossSalary(Double.parseDouble(payload.get("grossSalary").toString()));
            payroll.setTotalAllowances(Double.parseDouble(payload.get("totalAllowances").toString()));
            payroll.setTotalDeductions(Double.parseDouble(payload.get("totalDeductions").toString()));
            payroll.setStatus("PAID"); // Default status for new records

            double grossPlusAllowances = payroll.getGrossSalary() + payroll.getTotalAllowances();
            double tax = grossPlusAllowances * 0.01;
            payroll.setTotalTax(tax);
            payroll.setNetSalary(grossPlusAllowances - (payroll.getTotalDeductions() + tax));

            return payrollRepo.saveAndFlush(payroll);
        } catch (Exception e) {
            log.error("Save failed: {}", e.getMessage());
            throw new RuntimeException("Error saving payroll: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Payroll updateStatus(Integer id, String status) {
        Payroll p = payrollRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));
        p.setStatus(status);
        return payrollRepo.save(p);
    }
}