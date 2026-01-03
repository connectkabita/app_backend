package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.reportdto.AttendanceSummaryDTO;

public  interface AttendanceReportService  {

        AttendanceSummaryDTO getAttendanceSummary(int month, int year);
    }

