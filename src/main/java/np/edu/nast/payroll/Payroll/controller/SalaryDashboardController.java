package np.edu.nast.payroll.Payroll.controller;

// Correct the import to match your project folder structure (dto.auth)
import np.edu.nast.payroll.Payroll.dto.auth.SalarySummaryDTO;
import np.edu.nast.payroll.Payroll.dto.auth.CommandCenterDTO;
import np.edu.nast.payroll.Payroll.entity.SalaryComponent;
import np.edu.nast.payroll.Payroll.entity.Department;
import np.edu.nast.payroll.Payroll.service.SalaryComponentService;
import np.edu.nast.payroll.Payroll.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/salary-summary")
@CrossOrigin(origins = "http://localhost:5173")
public class SalaryDashboardController {

    @Autowired
    private SalaryComponentService componentService;

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public SalarySummaryDTO getSummary() {
        List<SalaryComponent> components = componentService.getAll();
        List<Department> departments = departmentService.getAll();

        SalarySummaryDTO dto = new SalarySummaryDTO();
        dto.departments = new ArrayList<>();

        double totalGross = 0;
        double totalDeductions = 0;

        for (Department dept : departments) {
            // FIX: Access ID via the related componentType object to fix errors 35 and 39
            double deptGross = components.stream()
                    .filter(c -> c.getDescription() != null &&
                            c.getDescription().equals(String.valueOf(dept.getDeptId())) &&
                            c.getComponentType() != null &&
                            c.getComponentType().getComponentTypeId() == 1)
                    .mapToDouble(SalaryComponent::getDefaultValue).sum();

            double deptTax = components.stream()
                    .filter(c -> c.getDescription() != null &&
                            c.getDescription().equals(String.valueOf(dept.getDeptId())) &&
                            c.getComponentType() != null &&
                            c.getComponentType().getComponentTypeId() == 3)
                    .mapToDouble(SalaryComponent::getDefaultValue).sum();

            if (deptGross > 0 || deptTax > 0) {
                totalGross += deptGross;
                totalDeductions += deptTax;
                dto.departments.add(new SalarySummaryDTO.DeptBreakdown(
                        dept.getDeptName(),
                        deptGross - deptTax,
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

        double totalPayroll = components.stream()
                .filter(c -> c.getComponentType() != null && c.getComponentType().getComponentTypeId() == 1)
                .mapToDouble(SalaryComponent::getDefaultValue).sum();

        long pendingCount = components.stream()
                .filter(c -> c.getRequired() != null && c.getRequired())
                .count();

        dto.setMonthlyPayrollTotal(totalPayroll);
        dto.setPayrollStatus("Processing");
        dto.setCompliancePercentage(100);
        dto.setPendingVerifications((int) pendingCount);

        return dto;
    }
}