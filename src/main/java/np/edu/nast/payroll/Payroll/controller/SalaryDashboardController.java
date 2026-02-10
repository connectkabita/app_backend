package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.dto.auth.SalarySummaryDTO;
import np.edu.nast.payroll.Payroll.dto.auth.CommandCenterDTO;
import np.edu.nast.payroll.Payroll.entity.SalaryComponent;
import np.edu.nast.payroll.Payroll.entity.Department;
import np.edu.nast.payroll.Payroll.service.SalaryComponentService;
import np.edu.nast.payroll.Payroll.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/salary-summary")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor // Modern injection
public class SalaryDashboardController {

    private final SalaryComponentService componentService;
    private final DepartmentService departmentService;

    @GetMapping
    public SalarySummaryDTO getSummary() {
        List<SalaryComponent> components = componentService.getAll();
        List<Department> departments = departmentService.getAll();

        SalarySummaryDTO dto = new SalarySummaryDTO();
        dto.departments = new ArrayList<>();

        double totalGross = 0;
        double totalDeductions = 0;

        for (Department dept : departments) {
            // Logic: Components describe which department they belong to in the 'description'
            // and we filter 'Basic Salary' as Gross and 'Income Tax' as Deduction
            double deptGross = components.stream()
                    .filter(c -> String.valueOf(dept.getDeptId()).equals(c.getDescription()) &&
                            c.getComponentName().equalsIgnoreCase("Basic Salary"))
                    .mapToDouble(SalaryComponent::getDefaultValue).sum();

            double deptTax = components.stream()
                    .filter(c -> String.valueOf(dept.getDeptId()).equals(c.getDescription()) &&
                            c.getComponentName().equalsIgnoreCase("Income Tax"))
                    .mapToDouble(SalaryComponent::getDefaultValue).sum();

            if (deptGross > 0 || deptTax > 0) {
                totalGross += deptGross;
                totalDeductions += deptTax;
                dto.departments.add(new SalarySummaryDTO.DeptBreakdown(
                        dept.getDeptName(),
                        deptGross - deptTax, // Net
                        deptTax
                ));
            }
        }

        dto.totalGross = totalGross;
        dto.totalDeductions = totalDeductions;
        dto.totalNet = totalGross - totalDeductions;

        return dto;
    }

    @GetMapping("/command-center")
    public CommandCenterDTO getCommandCenterStats() {
        List<SalaryComponent> components = componentService.getAll();
        CommandCenterDTO dto = new CommandCenterDTO();

        // Total Payroll is the sum of all "FIXED" salary components
        double totalPayroll = components.stream()
                .filter(c -> "FIXED".equalsIgnoreCase(c.getCalculationMethod()))
                .mapToDouble(SalaryComponent::getDefaultValue).sum();

        // Compliance based on "Required" components
        long requiredCount = components.stream()
                .filter(c -> c.isRequired() != null && c.isRequired())
                .count();

        dto.setMonthlyPayrollTotal(totalPayroll);
        dto.setPayrollStatus("Active");
        dto.setCompliancePercentage(100);
        dto.setPendingVerifications((int) requiredCount);

        return dto;
    }
}