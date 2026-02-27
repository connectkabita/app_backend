package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.edu.nast.payroll.Payroll.dto.auth.PayrollDashboardDTO;
import np.edu.nast.payroll.Payroll.entity.*;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    private final AttendanceRepository attendanceRepo;
    private final EmployeeLeaveRepository employeeLeaveRepo;

    /**
     * DASHBOARD BATCH CALCULATION
     */
    @Override
    public List<PayrollDashboardDTO> getBatchCalculation(String month, int year) {
        log.info("Batch calculating payroll for {}/{}", month, year);
        List<Employee> employees = employeeRepo.findAll();
        int monthValue = parseMonth(month);

        if (monthValue == -1) {
            log.error("Aborting batch calculation: Invalid month {}", month);
            return new ArrayList<>();
        }

        LocalDate periodStart = LocalDate.of(year, monthValue, 1);
        LocalDate periodEnd = periodStart.plusMonths(1);
        List<SalaryComponent> components = salaryComponentRepo.findAll();
        double fallbackBasic = getFallbackBasicFromComponents(components);

        return employees.stream().map(emp -> {
            try {
                double physicalHours = calculateHoursForPeriodInternal(emp.getEmpId(), periodStart, periodEnd);
                double paidLeaveHours = calculatePaidLeaveHoursInternal(emp.getEmpId(), periodStart, periodEnd);
                double combinedHours = physicalHours + paidLeaveHours;

                double standardTotalHours = 28.0 * 8.0;
                double baseSalary = (emp.getBasicSalary() != null && emp.getBasicSalary() > 0)
                        ? emp.getBasicSalary() : fallbackBasic;

                double hourlyRate = baseSalary / standardTotalHours;
                double actualEarned = (combinedHours >= standardTotalHours) ? baseSalary : (combinedHours * hourlyRate);

                return PayrollDashboardDTO.builder()
                        .empId(emp.getEmpId())
                        .fullName(emp.getFirstName() + " " + emp.getLastName())
                        .basicSalary(baseSalary)
                        .earnedSalary(round(actualEarned))
                        .totalWorkedHours(round(combinedHours))
                        .maritalStatus(emp.getMaritalStatus())
                        .build();
            } catch (Exception e) {
                log.error("Error in batch for Employee {}: {}", emp.getEmpId(), e.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    /**
     * CORE CALCULATION PREVIEW (Single Employee)
     */
    @Override
    public Payroll calculatePreview(Map<String, Object> payload) {
        Integer empId = resolveEmpId(payload);
        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found for ID: " + empId));

        LocalDate periodStart = LocalDate.now().withDayOfMonth(1);
        LocalDate periodEnd = periodStart.plusMonths(1).withDayOfMonth(1);

        validatePayrollPeriod(empId, periodStart);

        double physicalWorkedHours = calculateHoursForPeriodInternal(empId, periodStart, periodEnd);
        double paidLeaveHours = calculatePaidLeaveHoursInternal(empId, periodStart, periodEnd);
        double totalPayableHours = physicalWorkedHours + paidLeaveHours;

        double standardTotalHours = 208.0; // 26 days * 8 hours
        List<SalaryComponent> components = salaryComponentRepo.findAll();
        double baseSalary = (employee.getBasicSalary() != null && employee.getBasicSalary() > 0)
                ? employee.getBasicSalary() : getFallbackBasicFromComponents(components);

        double hourlyRate = baseSalary / standardTotalHours;
        double overtimePay = Math.max(0, (totalPayableHours - standardTotalHours) * hourlyRate);
        double actualBasicEarned = (totalPayableHours >= standardTotalHours) ? baseSalary : (totalPayableHours * hourlyRate);

        // Component mapping
        double dearnessAmt = getComponentValue(components, "Dearness Allowance", 0.0);
        double hraPercentage = getComponentValue(components, "HRA", 0.0);
        double ssfPercentage = getComponentValue(components, "SSF", 11.0);

        double festivalBonus = parseDouble(payload, "festivalBonus");
        double otherBonuses = parseDouble(payload, "bonuses");
        double citContribution = parseDouble(payload, "citContribution");

        double totalAllowances = dearnessAmt + (actualBasicEarned * (hraPercentage / 100.0));
        double ssfContribution = actualBasicEarned * (ssfPercentage / 100.0);

        double grossSalary = actualBasicEarned + totalAllowances + festivalBonus + otherBonuses + overtimePay;
        double taxableMonthly = grossSalary - (ssfContribution + citContribution);

        // Nepal Tax Logic
        double annualTax = calculateNepalTax(taxableMonthly * 12, employee.getMaritalStatus(), ssfContribution > 0);
        double monthlyTax = annualTax / 12;

        return Payroll.builder()
                .employee(employee)
                .payGroup(employee.getPayGroup() != null ? employee.getPayGroup() : fetchDefaultPayGroup())
                .basicSalary(round(actualBasicEarned))
                .totalAllowances(round(totalAllowances))
                .festivalBonus(festivalBonus)
                .otherBonuses(otherBonuses)
                .overtimePay(round(overtimePay))
                .ssfContribution(round(ssfContribution))
                .citContribution(citContribution)
                .grossSalary(round(grossSalary))
                .taxableIncome(round(taxableMonthly))
                .totalTax(round(monthlyTax))
                .totalDeductions(round(ssfContribution + citContribution + monthlyTax))
                .netSalary(round(grossSalary - (ssfContribution + citContribution + monthlyTax)))
                .payPeriodStart(periodStart)
                .payPeriodEnd(periodEnd)
                .remarks(String.format("Worked: %.1f | Leave: %.1f", physicalWorkedHours, paidLeaveHours))
                .status("PREVIEW")
                .currencyCode("NPR")
                .isVoided(false)
                .build();
    }

    @Override
    @Transactional
    public Payroll processPayroll(Map<String, Object> payload) {
        Payroll payroll = calculatePreview(payload);
        Employee employee = payroll.getEmployee();

        User loggedInUser = getCurrentUser();
        LocalDate now = LocalDate.now();

        // Manage Monthly Batch Info
        MonthlyInfo summary = monthlyInfoRepo.findByMonthNameAndStatus(now.getMonth().name(), "PROCESSING").stream()
                .filter(m -> m.getPayGroup().getPayGroupId().equals(employee.getPayGroup().getPayGroupId())).findFirst()
                .orElseGet(() -> createNewMonthlyBatch(employee, now, loggedInUser));

        Integer paymentMethodId = Integer.valueOf(payload.getOrDefault("paymentMethodId", "1").toString());
        PaymentMethod method = paymentMethodRepo.findById(paymentMethodId).orElseThrow();

        payroll.setMonthlyInfo(summary);
        payroll.setStatus("PENDING_PAYMENT");
        payroll.setProcessedBy(loggedInUser);
        payroll.setPaymentAccount(employee.getPrimaryBankAccount());
        payroll.setPaymentMethod(method);
        payroll.setPayDate(now);

        return payrollRepo.save(payroll);
    }

    @Override
    @Transactional
    public void finalizePayroll(Integer id, String ref) {
        Payroll p = payrollRepo.findById(id).orElseThrow();
        p.setStatus("PAID");
        p.setTransactionRef(ref);
        p.setProcessedAt(LocalDateTime.now());
        updateMonthlyTotals(p.getMonthlyInfo(), p);
        payrollRepo.save(p);
    }

    // --- HELPERS ---

    private User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return userRepo.findById(1).orElseThrow();
        String identifier = auth.getName();
        return userRepo.findByUsername(identifier)
                .or(() -> userRepo.findByEmail(identifier))
                .orElseGet(() -> userRepo.findById(1).orElseThrow());
    }

    private double calculateNepalTax(double taxableIncome, String status, boolean isSsfEnrolled) {
        List<TaxSlab> slabs = taxSlabRepo.findByTaxpayerStatusOrderByMinAmountAsc(status);
        double totalTax = 0.0;
        for (TaxSlab slab : slabs) {
            if (taxableIncome > slab.getPreviousLimit()) {
                double taxableInThisSlab = Math.min(taxableIncome, slab.getMaxAmount()) - slab.getPreviousLimit();
                // Nepal Rule: 1% Social Security Tax is waived if SSF is already being contributed
                double rate = (slab.getMinAmount() == 0 && isSsfEnrolled) ? 0.0 : (slab.getRatePercentage() / 100.0);
                totalTax += taxableInThisSlab * rate;
            }
        }
        return totalTax;
    }

    private double calculateHoursForPeriodInternal(Integer empId, LocalDate start, LocalDate end) {
        return attendanceRepo.findByEmployee_EmpIdAndAttendanceDateGreaterThanEqualAndAttendanceDateLessThan(empId, start, end)
                .stream()
                .filter(a -> a.getCheckInTime() != null && a.getCheckOutTime() != null)
                .mapToDouble(a -> Duration.between(a.getCheckInTime(), a.getCheckOutTime()).toMinutes() / 60.0)
                .sum();
    }

    private double calculatePaidLeaveHoursInternal(Integer empId, LocalDate start, LocalDate end) {
        return employeeLeaveRepo.findRelevantLeaves(empId, "Approved", start, end.minusDays(1))
                .stream()
                .filter(l -> l.getLeaveType() != null && Boolean.TRUE.equals(l.getLeaveType().getPaid()))
                .mapToDouble(l -> (ChronoUnit.DAYS.between(l.getStartDate().isBefore(start) ? start : l.getStartDate(),
                        l.getEndDate().isAfter(end.minusDays(1)) ? end.minusDays(1) : l.getEndDate()) + 1) * 8.0)
                .sum();
    }

    private int parseMonth(String month) {
        try {
            return month.matches("\\d+") ? Integer.parseInt(month) : java.time.Month.valueOf(month.toUpperCase()).getValue();
        } catch (Exception e) { return -1; }
    }

    private Integer resolveEmpId(Map<String, Object> payload) {
        Object id = payload.get("empId");
        if (id == null) throw new RuntimeException("Employee ID missing");
        return Double.valueOf(id.toString()).intValue();
    }

    private double getFallbackBasicFromComponents(List<SalaryComponent> components) {
        return components.stream().filter(c -> c.getComponentName().equalsIgnoreCase("Basic Salary"))
                .mapToDouble(SalaryComponent::getDefaultValue).findFirst().orElse(0.0);
    }

    private double getComponentValue(List<SalaryComponent> components, String name, double fallback) {
        return components.stream().filter(c -> c.getComponentName().toUpperCase().contains(name.toUpperCase()))
                .mapToDouble(SalaryComponent::getDefaultValue).findFirst().orElse(fallback);
    }

    private void validatePayrollPeriod(Integer empId, LocalDate start) {
        boolean exists = payrollRepo.findByEmployeeEmpId(empId).stream()
                .anyMatch(p -> !p.getIsVoided() && p.getPayPeriodStart().equals(start) && !"PREVIEW".equals(p.getStatus()));
        if (exists) throw new RuntimeException("Payroll already processed for this period.");
    }

    private double parseDouble(Map<String, Object> payload, String key) {
        try { return Double.parseDouble(payload.getOrDefault(key, "0").toString()); } catch (Exception e) { return 0.0; }
    }

    private double round(double val) { return Math.round(val * 100.0) / 100.0; }

    private MonthlyInfo createNewMonthlyBatch(Employee emp, LocalDate date, User creator) {
        return monthlyInfoRepo.save(MonthlyInfo.builder().monthName(date.getMonth().name()).monthStart(date.withDayOfMonth(1))
                .monthEnd(date.withDayOfMonth(date.lengthOfMonth())).payGroup(emp.getPayGroup()).totalEmployeesProcessed(0)
                .totalGrossSalary(0.0).totalNetSalary(0.0).status("PROCESSING").generatedBy(creator).generatedAt(LocalDateTime.now()).build());
    }

    private void updateMonthlyTotals(MonthlyInfo summary, Payroll p) {
        summary.setTotalEmployeesProcessed((summary.getTotalEmployeesProcessed() == null ? 0 : summary.getTotalEmployeesProcessed()) + 1);
        summary.setTotalGrossSalary((summary.getTotalGrossSalary() == null ? 0.0 : summary.getTotalGrossSalary()) + p.getGrossSalary());
        summary.setTotalNetSalary((summary.getTotalNetSalary() == null ? 0.0 : summary.getTotalNetSalary()) + p.getNetSalary());
        monthlyInfoRepo.save(summary);
    }

    @Override public List<Payroll> getAllPayrolls() { return payrollRepo.findAll(); }
    @Override public List<Payroll> getPayrollByEmployeeId(Integer id) { return payrollRepo.findByEmployeeEmpId(id); }
    @Override public Payroll getPayrollById(Integer id) { return payrollRepo.findById(id).orElseThrow(); }
    @Override public Payroll updateStatus(Integer id, String status) { Payroll p = getPayrollById(id); p.setStatus(status); if ("VOIDED".equals(status)) p.setIsVoided(true); return payrollRepo.save(p); }
    @Override public Payroll voidPayroll(Integer id) { return updateStatus(id, "VOIDED"); }
    @Override @Transactional public void rollbackPayroll(Integer id) { payrollRepo.deleteById(id); }
    private PayGroup fetchDefaultPayGroup() { return payGroupRepo.findById(4).orElseThrow(); }

    @Override
    public Payroll getPayrollByEmployeeAndMonth(Integer empId, int month, int year) {
        return payrollRepo.findByEmployeeEmpIdAndMonth(empId, year, month)
                .orElseThrow(() -> new RuntimeException("No payroll record found for the selected period."));
    }
}