package np.edu.nast.payroll.Payroll.dto.auth;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayrollDashboardDTO {
    private Integer empId;
    private String fullName;
    private Double basicSalary; // The base rate from the contract
    private Double earnedSalary; // Calculated based on attendance hours
    private Double totalWorkedHours;
    private String maritalStatus;
    private String status;
}