package np.edu.nast.payroll.Payroll.controller;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.dto.auth.SalarySummaryDTO;
import np.edu.nast.payroll.Payroll.service.SalarySummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") // Base path to handle multiple modules
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SalarySummaryController {

    private final SalarySummaryService salarySummaryService;

    // Fix for Dashboard: Matches /api/salary-summary/command-center
    @GetMapping("/salary-summary/command-center")
    public ResponseEntity<SalarySummaryDTO> getDashboardData() {
        return ResponseEntity.ok(salarySummaryService.getSummaryData());
    }

    // Fix for Salary Management Page: Matches /api/payrolls/summary
    @GetMapping("/payrolls/summary")
    public ResponseEntity<SalarySummaryDTO> getSummary() {
        return ResponseEntity.ok(salarySummaryService.getSummaryData());
    }
}