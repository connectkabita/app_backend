package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.dto.auth.SalaryReportDTO;
import np.edu.nast.payroll.Payroll.entity.Report;
import np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO;
import np.edu.nast.payroll.Payroll.reportdto.ReportSummaryDTO;

import java.util.List;

public interface ReportService {
    List<Report> getAllReports();
    void generateAndSaveReport(String category);
    Report getReportById(Long id);
    byte[] getFileData(String filePath);
    // This MUST match the implementation name exactly
    List<SalaryReportDTO> getSalarySummaryData();

    ReportSummaryDTO getSummary(int year);
    List<MonthlyPayrollDTO> getMonthlyPayroll(int year);
}