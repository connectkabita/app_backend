package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Report;
import np.edu.nast.payroll.Payroll.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/history")
    public List<Report> getHistory() {
        return reportService.getAllReports();
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generate(@RequestParam String category) {
        reportService.generateAndSaveReport(category);
        return ResponseEntity.ok("Generated");
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        Report report = reportService.getReportById(id);
        byte[] data = reportService.getFileData(report.getFilePath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + report.getFileName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}