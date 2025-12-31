package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.dto.auth.SalaryReportDTO;
import np.edu.nast.payroll.Payroll.entity.Report;
import np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO;
import np.edu.nast.payroll.Payroll.reportdto.ReportSummaryDTO;
import np.edu.nast.payroll.Payroll.repository.ReportRepository;
import np.edu.nast.payroll.Payroll.service.ReportService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository repo;

    @Override
    public List<Report> getAllReports() {
        return List.of();
    }

    @Override
    public void generateAndSaveReport(String category) {

    }

    @Override
    public Report getReportById(Long id) {
        return null;
    }

    @Override
    public byte[] getFileData(String filePath) {
        return new byte[0];
    }

    @Override
    public List<SalaryReportDTO> getSalarySummaryData() {
        return List.of();
    }

    @Override
    public ReportSummaryDTO getSummary(int year) {
        return new ReportSummaryDTO(
                repo.countEmployees(),
                repo.sumPayroll(year),
                repo.sumDeductions(year),
                repo.sumAllowances(year),
                (long)0.0

        );
    }

    @Override
    public List<MonthlyPayrollDTO> getMonthlyPayroll(int year) {
        return repo.monthlyPayroll(year);
    }
}
