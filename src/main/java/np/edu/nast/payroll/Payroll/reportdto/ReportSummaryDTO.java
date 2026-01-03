package np.edu.nast.payroll.Payroll.reportdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportSummaryDTO {
    private long totalEmployees;
    private double monthlyPayroll;
    private double totalDeductions;
    private double totalAllowances;
    private long pendingLeaves;
}
