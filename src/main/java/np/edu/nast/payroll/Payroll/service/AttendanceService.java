package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import java.util.List;

public interface AttendanceService {

    Attendance createAttendance(Attendance attendance);
    Attendance updateAttendance(Long id, Attendance attendance);
    void deleteAttendance(Long id);
    Attendance getAttendanceById(Long id);
    List<Attendance> getAllAttendance();
    List<Attendance> getAttendanceByEmployee(Long empId);
}
