package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.Report;
import np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO;
import np.edu.nast.payroll.Payroll.reportdto.ReportSummaryDTO;
import java.util.List;

public interface ReportService {
    List<MonthlyPayrollDTO> getMonthlyPayroll(int year);
    double sumDeductions(int year);
    long countEmployees();
    ReportSummaryDTO getSummary(int year);

    // For historical records
    List<Report> getAllReports();
}