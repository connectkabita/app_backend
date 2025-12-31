package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO;
import np.edu.nast.payroll.Payroll.reportdto.ReportSummaryDTO;
import np.edu.nast.payroll.Payroll.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports/analytics")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService analyticsService;

    @GetMapping("/summary")
    public ReportSummaryDTO getSummary(@RequestParam int year) {
        return analyticsService.getSummary(year);
    }

    @GetMapping("/monthly-payroll")
    public List<MonthlyPayrollDTO> getMonthlyPayroll(@RequestParam int year) {
        return analyticsService.getMonthlyPayroll(year);
    }
}
