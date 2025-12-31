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
    private final PayGroupRepository payGroupRepo; // Required to fix your error

    @Override
    public List<Payroll> getAllPayrolls() {
        return payrollRepo.findAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Payroll processPayroll(Map<String, Object> payload) {
        log.info("Processing payroll for payload: {}", payload);
        try {
            // 1. Extract IDs from the Map (Matches your React keys: empId, accountId, payGroupId)
            Integer empId = Integer.valueOf(payload.get("empId").toString());
            Integer accountId = Integer.valueOf(payload.get("accountId").toString());
            Integer methodId = Integer.valueOf(payload.get("paymentMethodId").toString());
            Integer payGroupId = Integer.valueOf(payload.get("payGroupId").toString());

            // 2. Fetch Entities to satisfy database NOT NULL constraints
            Employee employee = employeeRepo.findById(empId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            BankAccount account = bankAccountRepo.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Bank Account not found"));

            PaymentMethod method = paymentMethodRepo.findById(methodId)
                    .orElseThrow(() -> new RuntimeException("Payment Method not found"));

            // FIX: This fetches the PayGroup that was causing the null error
            PayGroup payGroup = payGroupRepo.findById(payGroupId)
                    .orElseThrow(() -> new RuntimeException("Pay Group not found"));

            // Get first available user as admin for 'processedBy'
            User admin = userRepo.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No Admin user found"));

            // 3. Set values on the Entity
            Payroll payroll = new Payroll();
            payroll.setEmployee(employee);
            payroll.setProcessedBy(admin);
            payroll.setPaymentAccount(account);
            payroll.setPaymentMethod(method);
            payroll.setPayGroup(payGroup); // CRITICAL: This satisfies the DB constraint

            payroll.setPayDate(LocalDate.now());
            payroll.setGrossSalary(Double.parseDouble(payload.get("grossSalary").toString()));
            payroll.setTotalAllowances(Double.parseDouble(payload.get("totalAllowances").toString()));
            payroll.setTotalDeductions(Double.parseDouble(payload.get("totalDeductions").toString()));
            payroll.setStatus("PROCESSED");

            // 4. Calculations (Nepal Labor Act 1% Tax Compliance)
            double grossPlusAllowances = payroll.getGrossSalary() + payroll.getTotalAllowances();
            double tax = grossPlusAllowances * 0.01;
            payroll.setTotalTax(tax);
            payroll.setNetSalary(grossPlusAllowances - (payroll.getTotalDeductions() + tax));

            // 5. Force save and flush
            return payrollRepo.saveAndFlush(payroll);

        } catch (Exception e) {
            log.error("Save failed: {}", e.getMessage());
            throw new RuntimeException("Error saving payroll: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Payroll updateStatus(Integer id, String status) {
        Payroll p = payrollRepo.findById(id).orElseThrow();
        p.setStatus(status);
        return payrollRepo.save(p);
    }
}