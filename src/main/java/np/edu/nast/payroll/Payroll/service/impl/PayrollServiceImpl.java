package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.dto.auth.PayrollRequest;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PayrollServiceImpl implements PayrollService {

    @Autowired private PayrollRepository payrollRepo;
    @Autowired private EmployeeRepository employeeRepo;
    @Autowired private BankAccountRepository bankAccountRepo;
    @Autowired private PaymentMethodRepository paymentMethodRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private PayGroupRepository payGroupRepo;

    @Override
    public List<Payroll> getAllPayrolls() {
        return payrollRepo.findAll();
    }

    @Override
    @Transactional
    public Payroll updateStatus(Integer id, String newStatus) {
        Payroll payroll = payrollRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll record not found with id: " + id));
        payroll.setStatus(newStatus);
        return payrollRepo.save(payroll);
    }

    @Override
    @Transactional
    public Payroll processPayrollRequest(PayrollRequest request) {
        var employee = employeeRepo.findById(request.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee ID not found."));

        Payroll payroll = new Payroll();
        payroll.setEmployee(employee);

        Integer pGroupId = (request.getPayGroupId() != null) ? request.getPayGroupId() : 1;
        var payGroup = payGroupRepo.findById(pGroupId).orElseThrow();
        payroll.setPayGroup(payGroup);

        Integer accId = (request.getAccountId() != null) ? request.getAccountId() : 1;
        var account = bankAccountRepo.findById(accId).orElseThrow();
        payroll.setPaymentAccount(account);

        Integer methodId = (request.getPaymentMethodId() != null) ? request.getPaymentMethodId() : 1;
        var method = paymentMethodRepo.findById(methodId).orElseThrow();
        payroll.setPaymentMethod(method);

        var adminUser = userRepo.findById(6).orElseThrow();
        payroll.setProcessedBy(adminUser);

        payroll.setGrossSalary(request.getGrossSalary() != null ? request.getGrossSalary() : 0.0);
        payroll.setTotalAllowances(request.getTotalAllowances() != null ? request.getTotalAllowances() : 0.0);
        payroll.setTotalDeductions(request.getTotalDeductions() != null ? request.getTotalDeductions() : 0.0);

        return calculateAndSave(payroll);
    }

    private Payroll calculateAndSave(Payroll payroll) {
        double gross = payroll.getGrossSalary();
        double allow = payroll.getTotalAllowances();
        double deduct = payroll.getTotalDeductions();
        double tax = (gross + allow) * 0.01;
        double net = (gross + allow) - (deduct + tax);

        payroll.setTotalTax(tax);
        payroll.setNetSalary(net);
        payroll.setStatus("PROCESSED");
        payroll.setProcessedAt(LocalDateTime.now());
        payroll.setPayPeriodStart(LocalDate.now().withDayOfMonth(1));
        payroll.setPayPeriodEnd(LocalDate.now());
        payroll.setPayDate(LocalDate.now());

        return payrollRepo.save(payroll);
    }

    @Override public Payroll savePayroll(Payroll p) { return payrollRepo.save(p); }
    @Override public Payroll getPayrollById(Integer id) { return payrollRepo.findById(id).orElse(null); }
    @Override public void deletePayroll(Integer id) { payrollRepo.deleteById(id); }
}