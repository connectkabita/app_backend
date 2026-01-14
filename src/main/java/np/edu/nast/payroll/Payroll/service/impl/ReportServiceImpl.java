package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
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
    public List<MonthlyPayrollDTO> getMonthlyPayroll(int year) {
        return repo.monthlyPayroll(year);
    }

    @Override
    public double sumDeductions(int year) {
        return repo.sumDeductions(year);
    }

    @Override
    public long countEmployees() {
        return repo.countEmployees();
    }

    @Override
    public ReportSummaryDTO getSummary(int year) {
        return new ReportSummaryDTO(
                repo.countEmployees(),
                repo.sumPayroll(year),
                repo.sumDeductions(year),
                repo.sumAllowances(year),
                0L
        );
    }

    @Override
    public List<Report> getAllReports() {
        return List.of(); // Implement if needed for UI history
    }
}