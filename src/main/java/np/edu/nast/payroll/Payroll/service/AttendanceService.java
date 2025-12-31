package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import np.edu.nast.payroll.Payroll.repository.AttendanceRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {


    Attendance createAttendance(Attendance attendance);

    Attendance updateAttendance(Integer id, Attendance attendance);
    void deleteAttendance(Integer id);
    Attendance getAttendanceById(Integer id);
    List<Attendance> getAllAttendance();
    List<Attendance> getAttendanceByEmployee(Integer empId);




}
