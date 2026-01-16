package np.edu.nast.payroll.Payroll.salaryDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
public class EmployeeSalaryAnalyticsDTO {
    private String employeeName;
    private String bankAccount;
    private String bankName; // <-- new
    private String designation;
    private String employmentStatus;
    private Double baseSalary;
    private Double grossSalary;
    private  Double totalAllowances;
    private Double totalDeductions;
    private Double taxableAmount;
    private Double netSalary;


}
