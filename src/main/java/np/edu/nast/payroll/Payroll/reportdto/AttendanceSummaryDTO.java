package np.edu.nast.payroll.Payroll.reportdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttendanceSummaryDTO {
    private long totalPresent;
    private long totalAbsent;
    private long lateArrivals;
    private double overtimeHours;
    private double overtimePay;
}
