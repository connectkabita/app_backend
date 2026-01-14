package np.edu.nast.payroll.Payroll.reportdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyPayrollDTO {
    private String month;
    private Double totalAmount;
    private Long employeeCount;

    // This constructor must exist for your JPQL 'new' query in the Repository
    public MonthlyPayrollDTO(String month, Double totalAmount) {
        this.month = month;
        this.totalAmount = totalAmount;
    }
}