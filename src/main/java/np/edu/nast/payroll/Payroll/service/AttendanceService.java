package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
    Attendance createAttendance(Attendance attendance);
    Attendance updateAttendance(Integer id, Attendance attendance);
    void deleteAttendance(Integer id);
    Attendance getAttendanceById(Integer id);
    List<Attendance> getAllAttendance();
    List<Attendance> getAttendanceByEmployee(Integer empId);

    // NEW FEATURE: Dashboard Statistics
    Map<String, Object> getMonthlyStats(Integer empId, int year, int month);
}