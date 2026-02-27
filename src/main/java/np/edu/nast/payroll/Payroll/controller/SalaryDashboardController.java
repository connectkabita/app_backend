package np.edu.nast.payroll.Payroll.controller;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.dto.auth.SalarySummaryDTO;
import np.edu.nast.payroll.Payroll.repository.PayrollRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/salary-summary") // React must call this path
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class SalaryDashboardController {

    private final PayrollRepository payrollRepo;

    @GetMapping
    public SalarySummaryDTO getSummary() {
        // Fetches [0]=Name, [1]=Net, [2]=Tax, [3]=Gross from DB
        List<Object[]> rawDeptData = payrollRepo.getDepartmentalTotals(1);

        List<SalarySummaryDTO.DeptBreakdown> breakdownList = new ArrayList<>();
        double grandTotalGross = 0;
        double grandTotalNet = 0;
        double grandTotalTax = 0;

        for (Object[] row : rawDeptData) {
            String deptName = (String) row[0];
            double deptNet = ((Number) row[1]).doubleValue();
            double deptTax = ((Number) row[2]).doubleValue();
            double deptGross = ((Number) row[3]).doubleValue(); // Data exists: image_aad9b7.png

            grandTotalNet += deptNet;
            grandTotalTax += deptTax;
            grandTotalGross += deptGross;

            breakdownList.add(new SalarySummaryDTO.DeptBreakdown(deptName, deptNet, deptTax));
        }

        SalarySummaryDTO dto = new SalarySummaryDTO();
        dto.setDepartments(breakdownList);
        dto.setTotalNet(grandTotalNet);
        dto.setTotalDeductions(grandTotalTax);
        dto.setTotalGross(grandTotalGross); // This fixes the 0.00M card

        return dto;
    }
}