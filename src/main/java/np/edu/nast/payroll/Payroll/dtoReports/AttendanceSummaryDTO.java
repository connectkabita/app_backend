package np.edu.nast.payroll.Payroll.dtoReports;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class AttendanceSummaryDTO {
    private long presentDays;
    private long absentDays;
    private long leaveDays;
}
