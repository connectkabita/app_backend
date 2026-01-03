package np.edu.nast.payroll.Payroll.dtoReports;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class PayrollSummaryDTO {
    private long totalEmployees;
    private double monthlyPayroll;
    private double totalDeductions;
    private double totalAllowances;
    private long pendingLeaves;
}
