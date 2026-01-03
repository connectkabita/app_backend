package np.edu.nast.payroll.Payroll.dtoReports;

import lombok.*;

@Getter

public class MonthlyPayrollDTO {
    private String month;
    private double amount;

    public MonthlyPayrollDTO(String month, double amount) {
        this.month = month;
        this.amount = amount;
    }

}
