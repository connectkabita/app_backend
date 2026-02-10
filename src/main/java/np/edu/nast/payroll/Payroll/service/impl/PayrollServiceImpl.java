package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.edu.nast.payroll.Payroll.entity.*;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepo;
    private final EmployeeRepository employeeRepo;
    private final SalaryComponentRepository salaryComponentRepo;
    private final TaxSlabRepository taxSlabRepo;
    private final MonthlyInfoRepository monthlyInfoRepo;
    private final UserRepository userRepo;
    private final PayGroupRepository payGroupRepo;
    private final PaymentMethodRepository paymentMethodRepo;

    @Override
    public List<Payroll> getAllPayrolls() {
        return payrollRepo.findAll();
    }

    @Override
    public Payroll calculatePreview(Map<String, Object> payload) {
        log.info("--- START PAYROLL CALCULATION ---");

        // 1. EXTRACT EMPLOYEE ID
        Object empIdObj = payload.get("empId");
        if (empIdObj == null && payload.get("employee") instanceof Map) {
            Map<?, ?> empMap = (Map<?, ?>) payload.get("employee");
            empIdObj = empMap.get("empId");
        }

        if (empIdObj == null || empIdObj.toString().equalsIgnoreCase("undefined")) {
            throw new RuntimeException("Validation Error: Employee ID is missing.");
        }

        Integer empId = Double.valueOf(empIdObj.toString()).intValue();
        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found for ID: " + empId));

        // 2. ASSIGN DEFAULT PAYGROUP IF NULL
        if (employee.getPayGroup() == null) {
            PayGroup defaultGroup = payGroupRepo.findById(4)
                    .orElseThrow(() -> new RuntimeException("Default PayGroup 4 not found."));
            employee.setPayGroup(defaultGroup);
            employeeRepo.save(employee);
        }

        // 3. FETCH DYNAMIC COMPONENTS
        List<SalaryComponent> components = salaryComponentRepo.findAll();
        double basicSalary = (employee.getBasicSalary() != null && employee.getBasicSalary() > 0)
                ? employee.getBasicSalary() : 0.0;

        // Fallback for basic if not on employee record
        if (basicSalary == 0) {
            basicSalary = components.stream()
                    .filter(c -> c.getComponentName().equalsIgnoreCase("Basic Salary"))
                    .mapToDouble(SalaryComponent::getDefaultValue).findFirst().orElse(0.0);
        }

        double dearnessAmt = components.stream()
                .filter(c -> c.getComponentName().equalsIgnoreCase("Dearness Allowance"))
                .mapToDouble(SalaryComponent::getDefaultValue).findFirst().orElse(7380.0);

        double hraPercentage = components.stream()
                .filter(c -> c.getComponentName().equalsIgnoreCase("House Rent Allowance"))
                .mapToDouble(SalaryComponent::getDefaultValue).findFirst().orElse(15.0);

        double ssfPercentage = components.stream()
                .filter(c -> c.getComponentName().toLowerCase().contains("ssf"))
                .mapToDouble(SalaryComponent::getDefaultValue).findFirst().orElse(11.0);

        // 4. PERIOD DATE LOGIC
        LocalDate periodStart = LocalDate.now().withDayOfMonth(1); // Start of current month
        LocalDate periodEnd = periodStart.plusDays(28);            // 28 days after start

        // 5. MATH CALCULATIONS
        double festivalBonus = Double.parseDouble(payload.getOrDefault("festivalBonus", "0").toString());
        double otherBonuses = Double.parseDouble(payload.getOrDefault("bonuses", "0").toString());
        double citContribution = Double.parseDouble(payload.getOrDefault("citContribution", "0").toString());

        double totalAllowances = dearnessAmt + (basicSalary * (hraPercentage / 100.0));
        double ssfContribution = basicSalary * (ssfPercentage / 100.0);
        double monthlyGross = basicSalary + totalAllowances + festivalBonus + otherBonuses;
        double taxableMonthly = monthlyGross - (ssfContribution + citContribution);

        double annualTax = calculateNepalTax(taxableMonthly * 12, employee.getMaritalStatus(), ssfContribution > 0);
        double monthlyTax = annualTax / 12;

        return Payroll.builder()
                .employee(employee)
                .payGroup(employee.getPayGroup())
                .basicSalary(basicSalary)
                .totalAllowances(totalAllowances)
                .festivalBonus(festivalBonus)
                .otherBonuses(otherBonuses)
                .ssfContribution(ssfContribution)
                .citContribution(citContribution)
                .grossSalary(monthlyGross)
                .taxableIncome(taxableMonthly)
                .totalTax(monthlyTax)
                .totalDeductions(ssfContribution + citContribution + monthlyTax)
                .netSalary(monthlyGross - (ssfContribution + citContribution + monthlyTax))
                .payPeriodStart(periodStart) // Setting the required fields
                .payPeriodEnd(periodEnd)     // Setting the required fields
                .status("PREVIEW")
                .build();
    }

    @Override
    @Transactional
    public Payroll processPayroll(Map<String, Object> payload) {
        log.info("Stage 1: Processing Payroll Persistence");
        Payroll payroll = calculatePreview(payload);
        Employee employee = payroll.getEmployee();
        // --- NEW: CLEANUP LOGIC ---
        // Find if there's already a PENDING record for this employee this month
        // If found, we delete it to avoid "Record already exists" or "Locked" UI states
        LocalDate now1 = LocalDate.now();
        payrollRepo.findByEmployeeEmpId(employee.getEmpId()).stream()
                .filter(p -> "PENDING_PAYMENT".equals(p.getStatus())
                        && p.getPayDate().getMonth() == now1.getMonth())
                .forEach(p -> {
                    log.warn("Deleting orphaned PENDING record ID: {}", p.getPayrollId());
                    payrollRepo.delete(p);
                });

        BankAccount primaryAccount = employee.getPrimaryBankAccount();
        if (primaryAccount == null) throw new RuntimeException("Primary bank account missing for " + employee.getFirstName());

        // Resolve Current User
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String principalName = (auth != null) ? auth.getName() : "system";
        User loggedInUser = userRepo.findByEmail(principalName)
                .or(() -> userRepo.findByUsername(principalName))
                .orElseThrow(() -> new RuntimeException("User not found: " + principalName));

        // LINK TO MONTHLY BATCH
        LocalDate now = LocalDate.now();
        MonthlyInfo summary = monthlyInfoRepo.findByMonthNameAndStatus(now.getMonth().name(), "PROCESSING")
                .stream()
                .filter(m -> m.getPayGroup().getPayGroupId().equals(employee.getPayGroup().getPayGroupId()))
                .findFirst()
                .orElseGet(() -> createNewMonthlyBatch(employee, now, loggedInUser));

        // MAP PAYMENT METHOD
        Integer methodId = Integer.valueOf(payload.get("paymentMethodId").toString());
        PaymentMethod selectedMethod = paymentMethodRepo.findById(methodId)
                .orElseThrow(() -> new RuntimeException("Payment Method not found."));

        payroll.setMonthlyInfo(summary);
        payroll.setStatus("PENDING_PAYMENT");
        payroll.setProcessedBy(loggedInUser);
        payroll.setPaymentAccount(primaryAccount);
        payroll.setPaymentMethod(selectedMethod);
        payroll.setPayDate(now);
        payroll.setCurrencyCode("NPR");

        return payrollRepo.save(payroll);
    }

    @Override
    @Transactional
    public void finalizePayroll(Integer payrollId, String transactionRef) {
        log.info("Stage 2: Finalizing Payroll ID: {}", payrollId);
        Payroll payroll = payrollRepo.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Payroll record missing."));

        if ("PAID".equals(payroll.getStatus())) return;

        payroll.setStatus("PAID");
        payroll.setTransactionRef(transactionRef);
        payroll.setProcessedAt(LocalDateTime.now());

        updateMonthlyTotals(payroll.getMonthlyInfo(), payroll);
        payrollRepo.save(payroll);
    }

    @Override
    @Transactional
    public void rollbackPayroll(Integer payrollId) {
        log.warn("Rollback triggered for Payroll ID: {}", payrollId);
        payrollRepo.deleteById(payrollId);
    }

    private double calculateNepalTax(double taxableIncome, String status, boolean isSsfEnrolled) {
        if (taxableIncome <= 0) return 0.0;
        List<TaxSlab> slabs = taxSlabRepo.findByTaxpayerStatusOrderByMinAmountAsc(status);
        double totalTax = 0.0;
        for (TaxSlab slab : slabs) {
            double currentSlabMax = slab.getMaxAmount();
            double previousSlabEnd = slab.getPreviousLimit();
            if (taxableIncome > previousSlabEnd) {
                double amountInThisBucket = Math.min(taxableIncome, currentSlabMax) - previousSlabEnd;
                if (amountInThisBucket > 0) {
                    double rate = slab.getRatePercentage() / 100.0;
                    if (slab.getMinAmount() == 0 && isSsfEnrolled) rate = 0.0;
                    totalTax += amountInThisBucket * rate;
                }
            } else { break; }
        }
        return Math.round(totalTax * 100.0) / 100.0;
    }

    private MonthlyInfo createNewMonthlyBatch(Employee emp, LocalDate date, User creator) {
        return monthlyInfoRepo.save(MonthlyInfo.builder()
                .monthName(date.getMonth().name())
                .monthStart(date.withDayOfMonth(1))
                .monthEnd(date.withDayOfMonth(date.lengthOfMonth()))
                .payGroup(emp.getPayGroup())
                .totalEmployeesProcessed(0)
                .totalGrossSalary(0.0)
                .totalAllowances(0.0)
                .totalDeductions(0.0)
                .totalTax(0.0)
                .totalNetSalary(0.0)
                .currency("NPR")
                .status("PROCESSING")
                .generatedBy(creator)
                .generatedAt(LocalDateTime.now())
                .build());
    }

    private void updateMonthlyTotals(MonthlyInfo summary, Payroll p) {
        summary.setTotalEmployeesProcessed((summary.getTotalEmployeesProcessed() == null ? 0 : summary.getTotalEmployeesProcessed()) + 1);
        summary.setTotalGrossSalary((summary.getTotalGrossSalary() == null ? 0.0 : summary.getTotalGrossSalary()) + p.getGrossSalary());
        summary.setTotalAllowances((summary.getTotalAllowances() == null ? 0.0 : summary.getTotalAllowances()) + p.getTotalAllowances());
        summary.setTotalDeductions((summary.getTotalDeductions() == null ? 0.0 : summary.getTotalDeductions()) + p.getTotalDeductions());
        summary.setTotalTax((summary.getTotalTax() == null ? 0.0 : summary.getTotalTax()) + p.getTotalTax());
        summary.setTotalNetSalary((summary.getTotalNetSalary() == null ? 0.0 : summary.getTotalNetSalary()) + p.getNetSalary());
        monthlyInfoRepo.save(summary);
    }

    @Override public List<Payroll> getPayrollByEmployeeId(Integer empId) { return payrollRepo.findByEmployeeEmpId(empId); }
    @Override public Payroll updateStatus(Integer id, String status) {
        Payroll p = getPayrollById(id);
        p.setStatus(status);
        return payrollRepo.save(p);
    }
    @Override public Payroll voidPayroll(Integer id) { return updateStatus(id, "VOIDED"); }
    @Override public Payroll getPayrollById(Integer id) {
        return payrollRepo.findById(id).orElseThrow(() -> new RuntimeException("Payroll not found: " + id));
    }
}