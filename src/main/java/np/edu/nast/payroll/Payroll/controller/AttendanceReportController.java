package np.edu.nast.payroll.Payroll.controller;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.reportdto.AttendanceSummaryDTO;
import np.edu.nast.payroll.Payroll.service.AttendanceReportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports/attendance")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AttendanceReportController {

    private final AttendanceReportService service;

    @GetMapping("/summary")
    public AttendanceSummaryDTO getSummary(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return service.getAttendanceSummary(month, year);
    }
}
