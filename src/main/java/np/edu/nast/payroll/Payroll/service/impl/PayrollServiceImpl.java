package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.dto.auth.PayrollRequest;
import np.edu.nast.payroll.Payroll.entity.*;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepo;
    private final EmployeeRepository employeeRepo;
    private final BankAccountRepository bankAccountRepo;
    private final PaymentMethodRepository paymentMethodRepo;
    private final PayGroupRepository payGroupRepo;
    private final UserRepository userRepo;
    private final EmployeeSalaryComponentRepository escRepo;
    private final TaxSlabRepository taxSlabRepo;
    private final JavaMailSender mailSender; // For Email Dispatch

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')") // RBAC
    public Payroll processPayrollRequest(PayrollRequest request) {
        Employee employee = employeeRepo.findById(request.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found ID: " + request.getEmpId()));

        List<EmployeeSalaryComponent> components = escRepo.findByEmployeeEmpIdAndIsActiveTrue(request.getEmpId());

        double basicSalary = 0.0;
        double totalAllowances = 0.0;
        double totalDeductions = 0.0;

        for (EmployeeSalaryComponent esc : components) {
            if (esc.getSalaryComponent().getComponentName().equalsIgnoreCase("Basic Salary")) {
                basicSalary = esc.getValue();
            }
        }

        for (EmployeeSalaryComponent esc : components) {
            SalaryComponent sc = esc.getSalaryComponent();
            double value = esc.getValue();
            double calculatedAmt = sc.getCalculationMethod().equalsIgnoreCase("percentage_of_basic")
                    ? (value / 100) * basicSalary : value;

            String typeName = sc.getComponentType().getName();
            if (typeName.equalsIgnoreCase("allowance")) {
                totalAllowances += calculatedAmt;
            } else if (typeName.equalsIgnoreCase("deduction")) {
                totalDeductions += calculatedAmt;
            }
        }

        double taxableIncomeMonthly = (basicSalary + totalAllowances) - totalDeductions;
        double annualTaxable = taxableIncomeMonthly * 12;
        double annualTax = calculateAnnualTax(annualTaxable, "Single");
        double monthlyTax = annualTax / 12;

        Payroll payroll = Payroll.builder()
                .employee(employee)
                .grossSalary(basicSalary + totalAllowances)
                .totalAllowances(totalAllowances)
                .totalDeductions(totalDeductions)
                .totalTax(monthlyTax)
                .netSalary((basicSalary + totalAllowances) - (totalDeductions + monthlyTax))
                .status("PROCESSED")
                .payPeriodStart(LocalDate.now().withDayOfMonth(1))
                .payPeriodEnd(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()))
                .payDate(LocalDate.now())
                .processedAt(LocalDateTime.now())
                .build();

        payroll.setPaymentAccount(bankAccountRepo.findById(request.getAccountId() != null ? request.getAccountId() : 1).orElseThrow());
        payroll.setPaymentMethod(paymentMethodRepo.findById(request.getPaymentMethodId() != null ? request.getPaymentMethodId() : 1).orElseThrow());
        payroll.setProcessedBy(userRepo.findById(6).orElseThrow());

        return payrollRepo.save(payroll);
    }

    // NEW: Method to dispatch email to employee
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public void sendEmailSlip(Integer payrollId) {
        Payroll payroll = payrollRepo.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Payroll record not found"));

        String recipientEmail = payroll.getEmployee().getEmail();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject("Salary Slip - " + payroll.getPayPeriodStart().getMonth());
            helper.setText("Dear " + payroll.getEmployee().getFirstName() + ",\n\nYour payroll has been processed. Please find the details attached.");

            // Logic to attach PDF would go here
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    private double calculateAnnualTax(double income, String status) {
        List<TaxSlab> slabs = taxSlabRepo.findAllByOrderByMinAmountAsc();
        double tax = 0;
        for (TaxSlab slab : slabs) {
            if (income > slab.getMinAmount()) {
                double taxableInSlab = Math.min(income, slab.getMaxAmount()) - slab.getMinAmount();
                tax += taxableInSlab * (slab.getRatePercentage() / 100);
            }
        }
        return tax;
    }

    @Override public List<Payroll> getAllPayrolls() { return payrollRepo.findAll(); }
    @Override public Payroll savePayroll(Payroll p) { return payrollRepo.save(p); }
    @Override public Payroll getPayrollById(Integer id) { return payrollRepo.findById(id).orElse(null); }
    @Override public void deletePayroll(Integer id) { payrollRepo.deleteById(id); }
    @Override public Payroll updateStatus(Integer id, String status) {
        Payroll p = getPayrollById(id);
        if(p != null) p.setStatus(status);
        return payrollRepo.save(p);
    }
}