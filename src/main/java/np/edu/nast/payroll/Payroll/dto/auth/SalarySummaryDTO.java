package np.edu.nast.payroll.Payroll.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for the Salary Management Dashboard.
 * Maps to the cards and charts seen in image_aa07da.jpg.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalarySummaryDTO {
    // Card 1: Total Gross Pay
    private double totalGross;

    // Card 2: Total Deductions (Tax + SSF + CIT)
    private double totalDeductions;

    // Card 3: Total Net Disbursement
    private double totalNet;

    // Data for the Departmental Breakdown bars
    private List<DeptBreakdown> departments;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeptBreakdown {
        private String name;
        private double net;
        private double tax;
    }
}