package np.edu.nast.payroll.Payroll.controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import np.edu.nast.payroll.Payroll.entity.Report;
import np.edu.nast.payroll.Payroll.repository.ReportFileRepository;
import np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO;
import np.edu.nast.payroll.Payroll.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ReportFileRepository reportFileRepository;

    @PostMapping("/generate")
    public ResponseEntity<Report> generateReport(@RequestParam String category) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("NAST Payroll Management System").setBold().setFontSize(18));
            document.add(new Paragraph(category + " Report").setFontSize(14));
            document.add(new Paragraph("Generated: " + LocalDateTime.now()));
            document.add(new Paragraph("\n"));

            if (category.equalsIgnoreCase("Salary Summaries")) {
                List<MonthlyPayrollDTO> data = reportService.getMonthlyPayroll(2026);
                Table table = new Table(UnitValue.createPercentArray(new float[]{5, 5})).useAllAvailableWidth();
                table.addHeaderCell("Month");
                table.addHeaderCell("Total Net Salary (NPR)");
                for (MonthlyPayrollDTO item : data) {
                    table.addCell(item.getMonth() != null ? item.getMonth() : "N/A");
                    table.addCell(String.format("%.2f", item.getTotalAmount()));
                }
                document.add(table);
            } else if (category.equalsIgnoreCase("Tax & SSF Reports")) {
                double deductions = reportService.sumDeductions(2026);
                Table table = new Table(UnitValue.createPercentArray(new float[]{7, 3})).useAllAvailableWidth();
                table.addHeaderCell("Description");
                table.addHeaderCell("Total NPR");
                table.addCell("Statutory Deductions (Tax/SSF)");
                table.addCell(String.format("%.2f", deductions));
                document.add(table);
            } else if (category.equalsIgnoreCase("Attendance Logs")) {
                long count = reportService.countEmployees();
                document.add(new Paragraph("Active employees for cycle: " + count));
            }

            document.close();
            byte[] pdfBytes = baos.toByteArray();

            // File persistence logic
            String fileName = category.replace(" ", "_") + "_" + System.currentTimeMillis() + ".pdf";
            String dir = System.getProperty("user.home") + File.separator + "payroll_reports" + File.separator;
            new File(dir).mkdirs();
            Path path = Paths.get(dir + fileName);
            Files.write(path, pdfBytes);

            Report newReport = Report.builder()
                    .fileName(fileName).category(category).dateGenerated(LocalDateTime.now())
                    .fileSize((pdfBytes.length / 1024) + " KB").filePath(path.toString()).build();

            return ResponseEntity.ok(reportFileRepository.save(newReport));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/history")
    public List<Report> getHistory() {
        return reportFileRepository.findAllByOrderByDateGeneratedDesc();
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadReport(@PathVariable Long id) {
        try {
            Report report = reportFileRepository.findById(id).orElseThrow();
            Path path = Paths.get(report.getFilePath());
            Resource resource = new UrlResource(path.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}