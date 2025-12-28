package np.edu.nast.payroll.Payroll.dto.auth;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalaryReportDTO {
    private String employeeName;
    private String designation;
    private Double basicSalary;
    private Double totalAllowances;
    private Double totalDeductions;
    private Double netPay;
    private String paymentStatus;
}