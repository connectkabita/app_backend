package np.edu.nast.payroll.Payroll.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalarySummaryDTO {
    // Standard Summary Fields
    private double totalGross;
    private double totalDeductions;
    private double totalNet;
    private List<DeptBreakdown> departments;

    // Command Center Dashboard Fields
    private double monthlyPayrollTotal;
    private String payrollStatus;
    private int compliancePercentage;
    private int pendingVerifications;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeptBreakdown {
        private String name;
        private double net;
        private double tax;
    }
}