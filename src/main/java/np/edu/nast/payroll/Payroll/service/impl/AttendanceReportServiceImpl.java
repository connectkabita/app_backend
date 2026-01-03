package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.reportdto.AttendanceSummaryDTO;
import np.edu.nast.payroll.Payroll.repository.AttendanceReportRepository;
import np.edu.nast.payroll.Payroll.service.AttendanceReportService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceReportServiceImpl implements AttendanceReportService {

    private final AttendanceReportRepository repo;

    @Override
    public AttendanceSummaryDTO getAttendanceSummary(int month, int year) {

        return new AttendanceSummaryDTO(
                repo.countPresent(month, year),
                repo.countAbsent(month, year),
                0,
                0.0,
                0.0
        );
    }
}
