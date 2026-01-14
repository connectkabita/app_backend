package np.edu.nast.payroll.Payroll.service;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.dto.auth.SalarySummaryDTO;
import np.edu.nast.payroll.Payroll.repository.PayrollRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SalarySummaryService {

    private final PayrollRepository payrollRepository;

    public SalarySummaryDTO getSummaryData() {
        SalarySummaryDTO dto = new SalarySummaryDTO();

        // LOGIC FIX: Use primitive double or check for null explicitly to prevent 500 error
        Double gross = payrollRepository.sumTotalGross();
        Double deductions = payrollRepository.sumTotalDeductions();
        Double net = payrollRepository.sumTotalNet();

        dto.setTotalGross(gross != null ? gross : 0.0);
        dto.setTotalDeductions(deductions != null ? deductions : 0.0);
        dto.setTotalNet(net != null ? net : 0.0);

        // Required for Dashboard View
        dto.setMonthlyPayrollTotal(dto.getTotalNet());
        dto.setPayrollStatus("Processing");
        dto.setCompliancePercentage(100);

        // Ensure status string matches exactly what is in your database
        long pending = payrollRepository.countByStatus("PENDING");
        dto.setPendingVerifications((int) pending);

        // Prevent NullPointerException in Frontend map() function
        dto.setDepartments(new ArrayList<>());

        return dto;
    }
}